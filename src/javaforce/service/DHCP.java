package javaforce.service;

/**
 * DHCP Server (support IP4 only)
 *
 * @author pquiring
 *
 * Created : Nov 17, 2013
 */

import java.io.*;
import java.nio.*;
import java.net.*;
import java.util.*;

import javaforce.*;
import javaforce.jbus.*;

public class DHCP extends Thread {

  public final static String busPack = "net.sf.jfdhcp";

  private final static String UnixConfigFile = "/etc/jfdhcp.cfg";
  private final static String WinConfigFile =  System.getenv("ProgramData") + "/jfdhcp.cfg";

  public static String getConfigFile() {
    if (JF.isWindows()) {
      return WinConfigFile;
    } else {
      return UnixConfigFile;
    }
  }

  private final static String UnixLogFile = "/var/log/jfdhcp.log";
  private final static String WinLogFile =  System.getenv("ProgramData") + "/jfdhcp.log";

  public static String getLogFile() {
    if (JF.isWindows()) {
      return WinLogFile;
    } else {
      return UnixLogFile;
    }
  }

  private static int maxmtu = 1500 - 20 - 8;  //IP=20 UDP=8
  private static class Pool {
    public Object lock = new Object();
    public String name;
    public String server_ip;  //dhcp server ip
    public int server_ip_int;
    public String bind_ip;  //bind ip (0.0.0.0 = all interfaces)
    public int bind_ip_int;
    public String pool_first;  //pool first ip
    public int pool_first_int;  //pool first ip (as int)
    public String pool_last;  //pool last ip
    public int pool_last_int;  //pool last ip (as int)
    public String mask;  //subnet mask
    public int mask_int;  //subnet mask (as int)
    public int count = 0;  //# of IPs in pool
    public long pool_time[];  //timestamp of issue (0=not in use)
    public int pool_hwlen[];  //hwaddr len of client
    public byte pool_hwaddr[][];  //hwaddr of client
    public int next = 0;  //offset
    public String router;
    public String dns = "8.8.8.8";
    public int leaseTime = 3600 * 24;  //in seconds
  }
  private static ArrayList<Pool> pools = new ArrayList<Pool>();
  private static Pool global = new Pool();
  private static InetAddress broadcastAddress;
  private static class Host {
    public String ip;
    public int ip_int;
    public DatagramSocket ds;
  }
  private static ArrayList<Host> hosts = new ArrayList<Host>();
  private static Object close = new Object();

  public static enum State {Loading, Running, Error, Stopped};
  public static State state = State.Loading;
  public static Object stateMonitor = new Object();

  public void run() {
    JFLog.append(getLogFile(), true);
    try {
      loadConfig();
      busClient = new JBusClient(busPack, new JBusMethods());
      busClient.setPort(getBusPort());
      busClient.start();
      if (!validConfig()) {
        throw new Exception("invalid config");
      }
      broadcastAddress = InetAddress.getByName("255.255.255.255");
      for(int a=0;a<hosts.size();a++) {
        new HostWorker(hosts.get(a)).start();
      }
      setState(State.Running);
      //wait for close request
      synchronized(close) {
        close.wait();
      }
      setState(State.Stopped);
    } catch (Exception e) {
      JFLog.log(e);
      setState(State.Error);
    }
  }

  public void setState(State newState) {
    synchronized(stateMonitor) {
      state = newState;
      stateMonitor.notify();
    }
  }

  public void close() {
    int cnt = hosts.size();
    for(int a=0;a<cnt;a++) {
      DatagramSocket ds;
      ds = hosts.get(a).ds;
      if (ds != null) ds.close();
    }
    busClient.close();
    synchronized(close) {
      close.notify();
    }
  }

  private static class HostWorker extends Thread {
    private Host host;
    public HostWorker(Host host) {
      this.host = host;
    }
    public void run() {
      try {
        host.ds = new DatagramSocket(67, InetAddress.getByName(host.ip));
        while (true) {
          byte data[] = new byte[maxmtu];
          DatagramPacket packet = new DatagramPacket(data, maxmtu);
          host.ds.receive(packet);
          new RequestWorker(packet, host).start();
        }
      } catch (Exception e) {
        JFLog.log(e);
      }
    }
    public void close() {
      try {
        host.ds.close();
        host.ds = null;
      } catch (Exception e) {
        JFLog.log(e);
      }
    }
  }

  enum Section {None, Global, Pool};

  private final static String defaultConfig
    = "#comments start with a # symbol\r\n"
    + "[global]\r\n"
    + "#dns=8.8.8.8\r\n"
    + "\r\n"
    + "[pool_192_168_0_x]\r\n"
    + "#server_ip=192.168.0.2\r\n"
    + "#bind_ip=192.168.0.2\r\n"
    + "#pool_first=192.168.0.100\r\n"
    + "#pool_last=192.168.0.199\r\n"
    + "#mask=255.255.255.0\r\n"
    + "#router=192.168.0.1\r\n"
    + "#dns=8.8.8.8\r\n"
    + "#lease=86400  #24 hrs\r\n"
    + "\r\n"
    + "[pool_192_168_1_x]\r\n"
    + "#server_ip=192.168.1.2\r\n"
    + "#bind_ip=192.168.1.2\r\n"
    + "#pool_first=192.168.1.100\r\n"
    + "#pool_last=192.168.1.250\r\n"
    + "#mask=255.255.255.0\r\n"
    + "#router=192.168.1.1\r\n"
    + "#dns=8.8.8.8\r\n"
    + "#lease=7200  #2 hrs\r\n"
    + "\r\n"
    + "[pool_10_1_1_x_for_relay_agents_only]\r\n"
    + "#server_ip=192.168.1.2\r\n"
    + "#bind_ip=0.0.0.0  #bind to all interfaces\r\n"
    + "#pool_first=10.1.1.100\r\n"
    + "#pool_last=10.1.1.250\r\n"
    + "#mask=255.255.255.0\r\n"
    + "#router=10.1.1.1\r\n"
    ;

  private void loadConfig() {
    pools.clear();
    hosts.clear();
    Pool pool = null;
    try {
      StringBuilder cfg = new StringBuilder();
      BufferedReader br = new BufferedReader(new FileReader(getConfigFile()));
      while (true) {
        String ln = br.readLine();
        if (ln == null) break;
        cfg.append(ln);
        cfg.append("\r\n");
        ln = ln.trim().toLowerCase();
        int idx = ln.indexOf('#');
        if (idx != -1) ln = ln.substring(0, idx).trim();
        if (ln.length() == 0) continue;
        JFLog.log("ln=" + ln);
        if (ln.startsWith("[") && ln.endsWith("]")) {
          String sectionName = ln.substring(1,ln.length() - 1);
          if (sectionName.equals("global")) {
            pool = global;
          } else {
            pool = new Pool();
            pool.name = sectionName;
            pools.add(pool);
          }
          continue;
        }
             if (ln.startsWith("pool_first=")) pool.pool_first = ln.substring(11);
        else if (ln.startsWith("pool_last=")) pool.pool_last = ln.substring(10);
        else if (ln.startsWith("server_ip=")) pool.server_ip = ln.substring(10);
        else if (ln.startsWith("bind_ip=")) pool.bind_ip = ln.substring(8);
        else if (ln.startsWith("mask=")) pool.mask = ln.substring(5);
        else if (ln.startsWith("dns=")) pool.dns = ln.substring(4);
        else if (ln.startsWith("router=")) pool.router = ln.substring(7);
        else if (ln.startsWith("lease=")) pool.leaseTime = JF.atoi(ln.substring(6));
      }
      config = cfg.toString();
    } catch (FileNotFoundException e) {
      //create default config
      try {
        FileOutputStream fos = new FileOutputStream(getConfigFile());
        fos.write(defaultConfig.getBytes());
        fos.close();
        config = defaultConfig;
      } catch (Exception e2) {
        JFLog.log(e2);
      }
    } catch (Exception e) {
      JFLog.log(e);
    }
  }

  private boolean validConfig() {
    try {
      if (pools.size() == 0) throw new Exception("no pools defined");
      if (global.server_ip != null) {
        if (!validIP4(global.server_ip)) throw new Exception("global : invalid server_ip");
        global.server_ip_int = IP4toInt(global.server_ip);
      }
      if (global.bind_ip == null) global.bind_ip = "0.0.0.0";
      global.bind_ip_int = IP4toInt(global.bind_ip);
      int cnt = pools.size();
      for(int a=0;a<cnt;a++) {
        Pool pool = pools.get(a);
        if (pool.server_ip == null) pool.server_ip = global.server_ip;
        if (pool.router == null) pool.router = global.router;
        if (pool.dns == null) pool.dns = global.dns;
        if (pool.bind_ip == null) pool.bind_ip = global.bind_ip;
        if (!validIP4(pool.server_ip)) throw new Exception(pool.name + " : invalid server_ip");
        if (!validIP4(pool.pool_first)) throw new Exception(pool.name + " : invalid pool_first");
        if (!validIP4(pool.pool_last)) throw new Exception(pool.name + " : invalid pool_last");
        if (!validIP4(pool.router)) throw new Exception(pool.name + " : invalid router");
        if (!validIP4(pool.mask)) throw new Exception(pool.name + " : invalid mask");
        if (!validIP4(pool.dns)) throw new Exception(pool.name + " : invalid dns");
        if (pool.leaseTime < 3600 || pool.leaseTime > 3600 * 24) {
          JFLog.log(pool.name + " : leaseTime invalid, using 24 hrs");
          pool.leaseTime = 3600 * 24;
        }
        pool.server_ip_int = IP4toInt(pool.server_ip);
        pool.bind_ip_int = IP4toInt(pool.bind_ip);
        pool.pool_first_int = IP4toInt(pool.pool_first);
        pool.pool_last_int = IP4toInt(pool.pool_last);
        pool.mask_int = IP4toInt(pool.mask);
        if ((pool.pool_first_int & pool.mask_int) != (pool.pool_last_int & pool.mask_int)) {
          throw new Exception(pool.name + " : invalid pool range : " + pool.pool_first + "-" + pool.pool_last + ",mask=" + pool.mask);
        }
        pool.count = pool.pool_last_int & pool.mask_int - pool.pool_first_int & pool.mask_int + 1;
        pool.pool_time = new long[pool.count];
        pool.pool_hwlen = new int[pool.count];
        pool.pool_hwaddr = new byte[pool.count][16];
      }
      for(int a=0;a<cnt;a++) {
        Pool poola = pools.get(a);
        for(int b=0;b<cnt;b++) {
          if (b == a) continue;
          Pool poolb = pools.get(b);
          if ((poola.pool_first_int & poola.mask_int) == (poolb.pool_first_int & poolb.mask_int)) {
            throw new Exception("multiple pools overlap");
          }
        }
      }
      for(int a=0;a<cnt;a++) {
        Pool pool = pools.get(a);
        boolean hasHost = false;
        for(int b=0;b<hosts.size();b++) {
          Host host = hosts.get(b);
          if (host.ip == pool.bind_ip) {hasHost = true; break;}
        }
        if (!hasHost) {
          Host host = new Host();
          host.ip = pool.bind_ip;
          host.ip_int = IP4toInt(host.ip);
          hosts.add(host);
        }
      }
    } catch (Exception e) {
      JFLog.log(e);
      return false;
    }
    return true;
  }

  private boolean validIP4(String ip) {
    if (ip == null) return false;
    String o[] = ip.split("[.]");
    if (o.length != 4) return false;
    for(int a=0;a<4;a++) {
      int v = Integer.valueOf(o[a]);
      if (v < 0) return false;
      if (v > 255) return false;
    }
    return true;
  }

  private static int IP4toInt(String ip) {
    String o[] = ip.split("[.]");
    int ret = 0;
    for(int a=0;a<4;a++) {
      ret <<= 8;
      ret += (JF.atoi(o[a]));
    }
    return ret;
  }

  private static byte[] IP4toByteArray(String ip) {
    String o[] = ip.split("[.]");
    byte ret[] = new byte[4];
    for(int a=0;a<4;a++) {
      ret[a] = (byte)JF.atoi(o[a]);
    }
    return ret;
  }

  private static String IP4toString(int ip) {
    return String.format("%d.%d.%d.%d", ip >> 24, (ip >> 16) & 0xff, (ip >> 8) & 0xff, ip & 0xff);
  }

  private static final int cookie = 0x63825363;

  private static final int DHCP_OPCODE_REQUEST = 1;
  private static final int DHCP_OPCODE_REPLY = 2;

  private static final int DHCPDISCOVER = 1;
  private static final int DHCPOFFER = 2;
  private static final int DHCPREQUEST = 3;
  private static final int DHCPDECLINE = 4;
  private static final int DHCPACK = 5;
  private static final int DHCPNAK = 6;
  private static final int DHCPRELEASE = 7;
  //private static final int DHCPINFORM = 8;  //???

  private static final byte OPT_PAD = 0;
  private static final byte OPT_SUBNET_MASK = 1;
  private static final byte OPT_ROUTER = 3;
  private static final byte OPT_DNS = 6;
  private static final byte OPT_REQUEST_IP = 50;
  private static final byte OPT_LEASE_TIME = 51;
  private static final byte OPT_DHCP_MSG_TYPE = 53;
  private static final byte OPT_DHCP_SERVER_IP = 54;
  private static final byte OPT_END = -1;  //255

  private static class RequestWorker extends Thread {
    private DatagramPacket packet;
    private Host host;

    private byte req[];
    private byte reply[];
    private int replyOffset;  //offset while encoding name
    private ByteBuffer replyBuffer;

    private Pool pool;

    public RequestWorker(DatagramPacket packet, Host host) {
      this.packet = packet;
      this.host = host;
    }
    public void run() {
      try {
        req = packet.getData();
        ByteBuffer bb = ByteBuffer.wrap(req);
        bb.order(ByteOrder.BIG_ENDIAN);
        byte opcode = req[0];
        if (opcode != DHCP_OPCODE_REQUEST) throw new Exception("not a request");
        byte hwtype = req[1];
//        if (hwtype != 1) throw new Exception("not ethernet");
        byte hwlen = req[2];
//        if (hwlen != 6) throw new Exception("bad hardware length");
        byte hop = req[3];
        int id = bb.getInt(4);
        short seconds = bb.getShort(8);
        short flags = bb.getShort(10);
        int cip = bb.getInt(12);  //client ip
        int yip = bb.getInt(16);  //your ip
        int sip = bb.getInt(20);  //server ip
        int rip = bb.getInt(24);  //relay ip
        int msgType = -1;
        int yipOffset = -1;
        //detect pool
        int cnt = pools.size();
        int from_ip = rip;
        if (from_ip == 0) {
          String src = packet.getAddress().getHostAddress();
          from_ip = IP4toInt(src);
        }
        if (from_ip == 0) {
          from_ip = host.ip_int;
        }
        if (from_ip == 0 && pools.size() == 1) {
          from_ip = pools.get(0).server_ip_int;
        }
        if (from_ip == 0) {
          throw new Exception("can not determine pool for request");
        }
//        JFLog.log("ip=" + IP4toString(from_ip));
        for(int a=0;a<cnt;a++) {
          pool = pools.get(a);
          if ((pool.pool_first_int & pool.mask_int) == (from_ip & pool.mask_int)) {
            break;
          }
          pool = null;
        }
        if (pool == null) throw new Exception("no pool for request");
        if (yip !=0 && (yip & pool.mask_int) == (pool.pool_first_int & pool.mask_int)) {
          yipOffset = yip - pool.pool_first_int;
        }
        //28 = 16 bytes = client hardware address (ethernet : 6 bytes)
        //44 = 64 bytes = server host name (ignored)
        //108 = 128 bytes = boot filename (ignored)
        //236 = 4 bytes = cookie (0x63825363)
        //240 = options...
        int offset = 240;
        while (true) {
          byte opt = req[offset++];
          if (opt == OPT_PAD) continue;
          byte len = req[offset++];
          switch (opt) {
            case OPT_DHCP_MSG_TYPE:
              if (len != 1) throw new Exception("bad dhcp msg type");
              msgType = req[offset];
              break;
            case OPT_REQUEST_IP:
              if (len != 4) throw new Exception("bad request ip size");
              yip = bb.getInt(offset);
              if ((yip & pool.mask_int) == (pool.pool_first_int & pool.mask_int)) {
                yipOffset = yip - pool.pool_first_int;
              }
              break;
          }
          if (opt == OPT_END) break;
          offset += len;
        }
        if (msgType == -1) throw new Exception("no dhcp msg type");
        long now = System.currentTimeMillis();
        switch (msgType) {
          case DHCPDISCOVER:
            synchronized(pool.lock) {
              int i = pool.next++;
              int c = 0;
              while (c < pool.count) {
                if (pool.pool_time[i] != 0) {
                  if (pool.pool_time[i] < now) pool.pool_time[i] = 0;
                }
                if (pool.pool_time[i] == 0) {
                  //check with ping (Java 5 required)
                  byte addr[] = IP4toByteArray(pool.pool_first + i);
                  InetAddress inet = InetAddress.getByAddress(addr);
                  if (inet.isReachable(1000)) {
                    //IP still in use!
                    //this could happen if DHCP service is restarted since leases are only saved in memory
                    pool.pool_time[i] = now + (pool.leaseTime * 1000);
                  } else {
                    //offer this
                    sendReply(addr, DHCPOFFER, id, pool, rip);
                    pool.next = i+1;
                    if (pool.next == pool.count) pool.next = 0;
                    break;
                  }
                }
                c++;
                i++;
                if (i == pool.count) i = 0;
              }
            }
            //nothing left in pool to send an offer (ignore request)
            break;
          case DHCPREQUEST:
            //mark ip as used and send ack or nak if already in use
            if (yipOffset < 0 || yipOffset >= pool.count) {
              JFLog.log("request out of range");
              break;
            }
            synchronized(pool.lock) {
              byte addr[] = IP4toByteArray(pool.pool_first);
              addr[3] += yipOffset;
              if (pool.pool_time[yipOffset] != 0) {
                //check if hwaddr is the same
                boolean same = true;
                for(int a=0;a<pool.pool_hwlen[yipOffset];a++) {
                  if (pool.pool_hwaddr[yipOffset][a] != req[28 + a]) {same = false; break;}
                }
                if (same) pool.pool_time[yipOffset] = 0;
              }
              if (pool.pool_time[yipOffset] == 0) {
                //send ACK
                pool.pool_time[yipOffset] = now + (pool.leaseTime * 1000);
                pool.pool_hwlen[yipOffset] = hwlen;
                System.arraycopy(req, 28, pool.pool_hwaddr[yipOffset], 0, 16);
                sendReply(addr, DHCPACK, id, pool, rip);
              } else {
                //send NAK
                sendReply(addr, DHCPNAK, id, pool, rip);
              }
            }
            break;
          case DHCPRELEASE:
            //mark ip as unused
            if (yipOffset < 0 || yipOffset >= pool.count) {
              JFLog.log("release out of range");
              break;
            }
            synchronized(pool.lock) {
              if (pool.pool_time[yipOffset] != 0) {
                //check if hwaddr is the same
                boolean same = true;
                for(int a=0;a<pool.pool_hwlen[yipOffset];a++) {
                  if (pool.pool_hwaddr[yipOffset][a] != req[28 + a]) {same = false; break;}
                }
                if (!same) break;
                pool.pool_time[yipOffset] = 0;
              }
            }
            break;
          default:
            throw new Exception("unsupported dhcp msg type");
        }
      } catch (Exception e) {
        JFLog.log(e);
      }
    }

    private void sendReply(byte outData[], int outDataLength, int rip) {
      try {
        DatagramPacket out = new DatagramPacket(outData, outDataLength);
        if (rip == 0)
          out.setAddress(broadcastAddress);
        else
          out.setAddress(InetAddress.getByAddress(new byte[] {(byte)(rip >> 24), (byte)((rip >> 16) & 0xff), (byte)((rip >> 8) & 0xff), (byte)(rip & 0xff)}));
        out.setPort(packet.getPort());
        host.ds.send(out);
      } catch (Exception e) {
        JFLog.log(e);
      }
    }

    private void sendReply(byte yip[], int msgType /*offer,ack,nak*/, int id, Pool pool, int rip) {
      reply = new byte[maxmtu];
      replyBuffer = ByteBuffer.wrap(reply);
      replyBuffer.order(ByteOrder.BIG_ENDIAN);
      replyOffset = 0;
      reply[replyOffset++] = DHCP_OPCODE_REPLY;  //reply opcode
      reply[replyOffset++] = req[1];  //hwtype
      reply[replyOffset++] = req[2];  //hwlen
      reply[replyOffset++] = 0;  //hops
      putInt(id);
      putShort((short)0);  //seconds
      putShort((short)0);  //flags
      putInt(0);  //client IP
      putByteArray(yip);  //your IP
      putIP4(pool.server_ip);  //server ip
      putInt(rip);  //relay ip
      System.arraycopy(req, replyOffset, reply, replyOffset, 16);  //client hwaddr
      replyOffset += 16;
      replyOffset += 64;  //server name
      replyOffset += 128;  //boot filename (legacy BOOTP)
      //add cookie
      putInt(cookie);
      //add options
      reply[replyOffset++] = OPT_DHCP_MSG_TYPE;
      reply[replyOffset++] = 1;
      reply[replyOffset++] = (byte)msgType;

      reply[replyOffset++] = OPT_SUBNET_MASK;
      reply[replyOffset++] = 4;
      putIP4(pool.mask);

      reply[replyOffset++] = OPT_DNS;
      reply[replyOffset++] = 4;
      putIP4(pool.dns);

      reply[replyOffset++] = OPT_ROUTER;
      reply[replyOffset++] = 4;
      putIP4(pool.router);

      reply[replyOffset++] = OPT_DHCP_SERVER_IP;
      reply[replyOffset++] = 4;
      putIP4(pool.server_ip);

      reply[replyOffset++] = OPT_LEASE_TIME;
      reply[replyOffset++] = 4;
      putInt(pool.leaseTime);

      reply[replyOffset++] = OPT_END;

      sendReply(reply, replyOffset, rip);
    }

    private void putByteArray(byte ba[]) {
      for(int a=0;a<ba.length;a++) {
        reply[replyOffset++] = ba[a];
      }
    }

    private void putIP4(String ip) {
      String p[] = ip.split("[.]");
      for(int a=0;a<4;a++) {
        reply[replyOffset++] = (byte)JF.atoi(p[a]);
      }
    }

    private void putIP6(String ip) {
      String p[] = ip.split(":");
      for(int a=0;a<8;a++) {
        putShort((short)JF.atox(p[a]));
      }
    }

    private void putShort(short value) {
      replyBuffer.putShort(replyOffset, value);
      replyOffset += 2;
    }

    private void putInt(int value) {
      replyBuffer.putInt(replyOffset, value);
      replyOffset += 4;
    }
  }

  private static JBusServer busServer;
  private JBusClient busClient;
  private String config;

  public class JBusMethods {
    public void getConfig(String pack) {
      busClient.call(pack, "getConfig", busClient.quote(busClient.encodeString(config)));
    }
    public void setConfig(String cfg) {
      //write new file
      try {
        FileOutputStream fos = new FileOutputStream(getConfigFile());
        fos.write(JBusClient.decodeString(cfg).getBytes());
        fos.close();
      } catch (Exception e) {
        JFLog.log(e);
      }
    }
    public void restart() {
      dhcp.close();
      dhcp = new DHCP();
      dhcp.start();
    }
  }

  public static int getBusPort() {
    if (JF.isWindows()) {
      return 33004;
    } else {
      return 777;
    }
  }

  public static void main(String args[]) {
    serviceStart(args);
  }

  //Win32 Service

  private static DHCP dhcp;

  public static void serviceStart(String args[]) {
    if (JF.isWindows()) {
      busServer = new JBusServer(getBusPort());
      busServer.start();
      while (!busServer.ready) {
        JF.sleep(10);
      }
    }
    dhcp = new DHCP();
    dhcp.start();
  }

  public static void serviceStop() {
    JFLog.log("Stopping service");
    dhcp.close();
  }
}
