package jfcontrols.panels;

/** Monitors tag changes for components on Panel.
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.*;
import javaforce.webui.*;

import jfcontrols.db.*;
import jfcontrols.tags.*;

public class ClientContext extends Thread {
  private WebUIClient client;
  private ArrayList<Monitor> listeners = new ArrayList<>();
  private volatile boolean active;
  private Object lock = new Object();
  private ArrayList<Monitor> stack = new ArrayList<>();

  public HashMap<String, Component> alarms = new HashMap<>();
  public int lastAlarmID;
  public int debug_en_idx;
  public int debug_tv_idx;
  public DebugContext debug;
  public WatchContext watch;
  public boolean alarmActive;

  public ClientContext(WebUIClient client) {
    this.client = client;
  }

  public TagBase getTag(String name) {
    return TagsService.getTag(name);
  }

  public String read(String name) {
    TagBase tag = getTag(name);
    return tag.getValue();
  }

  public void write(String name, String value) {
    TagBase tag = getTag(name);
    tag.setValue(value);
  }

  public TagBase decode(String name) {
    return getTag(name);
  }

  private static class Monitor implements TagBaseListener {
    public TagBase tag;
    public ClientContext ctx;
    public String oldValue, newValue;
    public Component cmp;
    public TagAction action;
    public boolean anyChange;

    public Monitor(TagBase tag, Component cmp, TagAction action, ClientContext ctx) {
//      JFLog.log("addListener2:" + tag.getTagID() + "," + tag.getIndex() + "," + tag.getMember() + "," + tag.getMemberIndex() + ":" + this);
      this.tag = tag;
      this.cmp = cmp;
      this.action = action;
      this.ctx = ctx;
    }
    public void tagChanged(TagBase tagBase, String oldValue, String newValue) {
      //NOTE : this function is running in FunctionService - it must return asap
      synchronized(ctx.lock) {
//        JFLog.log("tagChanged:" + tag + ":" + id + ":" + oldValue + ":" + newValue + ":" + this);
        this.oldValue = oldValue;
        this.newValue = newValue;
        ctx.stack.add(this);
        ctx.lock.notify();
      }
    }
  }

  public void addListener(TagBase tag, Component cmp, boolean anyChange, TagAction action) {
    if (tag == null) return;
    Monitor monitor = new Monitor(tag, cmp, action, this);
    monitor.anyChange = anyChange;
    listeners.add(monitor);
    tag.addListener(monitor);
  }

  public void clear() {
    while (listeners.size() > 0) {
      Monitor monitor = listeners.remove(0);
      monitor.tag.removeListener(monitor);
    }
//    tags.clear();  //TODO
    if (debug != null) {
      debug.cancel();
      debug = null;
    }
    if (watch != null) {
      watch.cancel();
      watch = null;
    }
  }

  public void run() {
    Monitor monitor;
    active = true;
    //wait till client is ready
    while (!client.isReady()) {
      JF.sleep(100);
    }
    while (active) {
      synchronized(lock) {
        try {lock.wait();} catch (Exception e) {}
        if (stack.size() == 0) continue;
        monitor = stack.remove(0);
      }
      if (monitor == null) continue;
//      JFLog.log("tagChanged:" + monitor.tag + ":" + monitor.oldValue + ":" + monitor.newValue);
      monitor.action.tagChanged(monitor.tag, monitor.oldValue, monitor.newValue, monitor.cmp);
    }
  }

  public void cancel() {
    active = false;
    synchronized(lock) {
      lock.notify();
    }
    if (debug != null) {
      debug.cancel();
      debug = null;
    }
  }
}
