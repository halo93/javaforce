/** jfDataLogger
 *
 * @author pquiring
 */

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import javaforce.*;
import javaforce.controls.*;

public class App extends javax.swing.JFrame {

  public static String version = "0.6";

  public static int delays[] = new int[] {
    25, 50, 100, 500, 1000, 3000, 5000, 10000, 30000, 60000, 300000
  };

  public static int ticks[] = new int[] {
    40, 20, 10, 10, 10, 10, 10, 10, 10, 10, 10
  };

  /**
   * Creates new form App
   */
  public App() {
    app = this;
    initComponents();
    JFImage icon = new JFImage();
    icon.loadPNG(this.getClass().getClassLoader().getResourceAsStream("jfdatalogger.png"));
    setIconImage(icon.getImage());
    for(int a=0;a<delays.length;a++) {
      speed.addItem(Integer.toString(delays[a]) + "ms");
    }
    table.setModel(tableModel);
    list.setModel(listModel);
    newProject();
    setTitle("jfDataLogger/" + version);
    JF.centerWindow(this);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jToolBar1 = new javax.swing.JToolBar();
    newProject = new javax.swing.JButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    load = new javax.swing.JButton();
    save = new javax.swing.JButton();
    jSeparator2 = new javax.swing.JToolBar.Separator();
    run = new javax.swing.JToggleButton();
    clear = new javax.swing.JButton();
    jSeparator3 = new javax.swing.JToolBar.Separator();
    logBtn = new javax.swing.JButton();
    jSeparator4 = new javax.swing.JToolBar.Separator();
    jLabel1 = new javax.swing.JLabel();
    speed = new javax.swing.JComboBox<>();
    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    img = new javax.swing.JLabel() {
      public void paint(Graphics g) {
        if (logImage == null) return;
        drawImage(g);
      }
    };
    jScrollPane2 = new javax.swing.JScrollPane();
    table = new javax.swing.JTable();
    jPanel2 = new javax.swing.JPanel();
    jToolBar2 = new javax.swing.JToolBar();
    add = new javax.swing.JButton();
    edit = new javax.swing.JButton();
    delete = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    list = new javax.swing.JList<>();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("jfDataLogger");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    jToolBar1.setFloatable(false);
    jToolBar1.setRollover(true);

    newProject.setText("New");
    newProject.setFocusable(false);
    newProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    newProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    newProject.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        newProjectActionPerformed(evt);
      }
    });
    jToolBar1.add(newProject);
    jToolBar1.add(jSeparator1);

    load.setText("Load");
    load.setFocusable(false);
    load.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    load.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    load.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loadActionPerformed(evt);
      }
    });
    jToolBar1.add(load);

    save.setText("Save");
    save.setFocusable(false);
    save.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    save.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    save.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveActionPerformed(evt);
      }
    });
    jToolBar1.add(save);
    jToolBar1.add(jSeparator2);

    run.setText("Run");
    run.setFocusable(false);
    run.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    run.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    run.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        runActionPerformed(evt);
      }
    });
    jToolBar1.add(run);

    clear.setText("Clear");
    clear.setFocusable(false);
    clear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    clear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    clear.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearActionPerformed(evt);
      }
    });
    jToolBar1.add(clear);
    jToolBar1.add(jSeparator3);

    logBtn.setText("LogFile");
    logBtn.setFocusable(false);
    logBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    logBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    logBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logBtnActionPerformed(evt);
      }
    });
    jToolBar1.add(logBtn);
    jToolBar1.add(jSeparator4);

    jLabel1.setText("Speed");
    jToolBar1.add(jLabel1);

    speed.setMaximumSize(new java.awt.Dimension(100, 32767));
    jToolBar1.add(speed);

    jSplitPane1.setDividerLocation(250);

    img.setMaximumSize(new java.awt.Dimension(32768, 100));
    img.setMinimumSize(new java.awt.Dimension(0, 100));

    table.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Title 1", "Title 2", "Title 3", "Title 4"
      }
    ));
    jScrollPane2.setViewportView(table);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(img, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 792, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(img, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jSplitPane1.setRightComponent(jPanel1);

    jToolBar2.setFloatable(false);
    jToolBar2.setRollover(true);

    add.setText("Add");
    add.setFocusable(false);
    add.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    add.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    add.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addActionPerformed(evt);
      }
    });
    jToolBar2.add(add);

    edit.setText("Edit");
    edit.setFocusable(false);
    edit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    edit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    edit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        editActionPerformed(evt);
      }
    });
    jToolBar2.add(edit);

    delete.setText("Delete");
    delete.setFocusable(false);
    delete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    delete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    delete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteActionPerformed(evt);
      }
    });
    jToolBar2.add(delete);

    jScrollPane1.setViewportView(list);

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE))
    );

    jSplitPane1.setLeftComponent(jPanel2);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jSplitPane1))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
    add();
  }//GEN-LAST:event_addActionPerformed

  private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
    delete();
  }//GEN-LAST:event_deleteActionPerformed

  private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
    run();
  }//GEN-LAST:event_runActionPerformed

  private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
    clear();
  }//GEN-LAST:event_clearActionPerformed

  private void loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadActionPerformed
    load();
  }//GEN-LAST:event_loadActionPerformed

  private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
    save();
  }//GEN-LAST:event_saveActionPerformed

  private void newProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectActionPerformed
    newProject();
  }//GEN-LAST:event_newProjectActionPerformed

  private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
    edit();
  }//GEN-LAST:event_editActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    if (worker == null) {
      System.exit(0);
    }
  }//GEN-LAST:event_formWindowClosing

  private void logBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logBtnActionPerformed
    logFile();
  }//GEN-LAST:event_logBtnActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new App().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton add;
  private javax.swing.JButton clear;
  private javax.swing.JButton delete;
  private javax.swing.JButton edit;
  private javax.swing.JLabel img;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JToolBar.Separator jSeparator2;
  private javax.swing.JToolBar.Separator jSeparator3;
  private javax.swing.JToolBar.Separator jSeparator4;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JToolBar jToolBar1;
  private javax.swing.JToolBar jToolBar2;
  private javax.swing.JList<String> list;
  private javax.swing.JButton load;
  private javax.swing.JButton logBtn;
  private javax.swing.JButton newProject;
  private javax.swing.JToggleButton run;
  private javax.swing.JButton save;
  private javax.swing.JComboBox<String> speed;
  private javax.swing.JTable table;
  // End of variables declaration//GEN-END:variables

  public static App app;
  public static ArrayList<Tag> tags = new ArrayList<Tag>();
  public static DefaultTableModel tableModel = new DefaultTableModel();
  public static DefaultListModel listModel = new DefaultListModel();
  public static JFImage logImage = new JFImage(1, 510);
  public static Worker worker;
  public static Task task;
  public static int speedIdx;
  public static int delay;
  public static int tickCounter;
  public static FileOutputStream logger;
  public String projectFile;
  public String logFile;
  public static boolean active;

  public void newProject() {
    tags.clear();
    listModel.clear();
    list.removeAll();
    clear();
    projectFile = null;
  }

  public void clear() {
    while (tableModel.getRowCount() > 0) {
      tableModel.removeRow(0);
    }
    logImage.fill(0, 0, logImage.getWidth(), logImage.getHeight(), 0xffffffff);
  }

  public static String dl_filters[][] = new String[][] { {"Data Logger Files (*.dl)", "dl"} };
  public static String csv_filters[][] = new String[][] { {"CSV Files (*.csv)", "csv"} };

  public void load() {
    String filename = JF.getOpenFile(JF.getUserPath(), dl_filters);
    if (filename == null) return;
    newProject();
    projectFile = filename;
    XML xml = new XML();
    xml.read(filename);
    int cnt = xml.root.getChildCount();
    for(int a=0;a<cnt;a++) {
      XML.XMLTag xmltag = xml.root.getChildAt(a);
      Tag tag = new Tag();
      tag_load(tag, xmltag.content);
      tags.add(tag);
      listModel.addElement(tag.toString());
    }
  }

  public void tag_load(Tag tag, String data) {
    String f[] = data.split("[|]");
    tag.host = f[0];
    switch (f[1]) {
      case "S7": tag.type = Controller.types.S7; break;
      case "AB": tag.type = Controller.types.AB; break;
      case "MB": tag.type = Controller.types.MB; break;
      case "NI": tag.type = Controller.types.NI; break;
    }
    tag.tag = f[2];
    switch (f[3]) {
      case "bit": tag.size = Controller.sizes.bit; break;
      case "int8": tag.size = Controller.sizes.int8; break;
      case "int16": tag.size = Controller.sizes.int16; break;
      case "int32": tag.size = Controller.sizes.int32; break;
      case "float32": tag.size = Controller.sizes.float32; break;
      case "float64": tag.size = Controller.sizes.float64; break;
    }
    if (tag.isFloat()) {
      tag.fmin = Float.valueOf(f[4]);
      tag.fmax = Float.valueOf(f[5]);
    } else {
      tag.min = Integer.valueOf(f[4]);
      tag.max = Integer.valueOf(f[5]);
    }
    tag.color = Integer.valueOf(f[6], 16);
  }

  public String tag_save(Tag tag) {
    return tag.host + "|" + tag.type + "|" + tag + "|" + tag.size + "|" + tag.getmin() + "|" + tag.getmax() + "|" + Integer.toUnsignedString(tag.color, 16);
  }

  public void save() {
    String filename;
    if (projectFile != null) {
      filename = JF.getSaveFile(projectFile, dl_filters);
    } else {
      filename = JF.getSaveAsFile(JF.getUserPath(), dl_filters);
    }
    if (filename == null) return;
    if (!filename.toLowerCase().endsWith(".dl")) {
      filename += ".dl";
    }
    XML xml = new XML();
    xml.setRoot("jfDataLogger", "", "");
    int cnt = tags.size();
    for(int a=0;a<cnt;a++) {
      Tag tag = tags.get(a);
      xml.addTag(xml.root, "tag", "", tag_save(tag));
    }
    xml.write(filename);
    projectFile = filename;
  }

  public void logFile() {
    if (logFile == null) {
      logFile = JF.getSaveAsFile(JF.getUserPath(), csv_filters);
      if (logFile == null) return;
      if (!logFile.toLowerCase().endsWith(".csv")) {
        logFile += ".csv";
      }
      logBtn.setText("LogFile*");
    } else {
      logFile = null;
      JF.showMessage("Notice", "Log file turned off");
    }
  }

  public void add() {
    TagDialog dialog = new TagDialog(null, true);
    dialog.setVisible(true);
    if (dialog.accepted()) {
      Tag tag = new Tag();
      dialog.save(tag);
      tags.add(tag);
      listModel.addElement(tag.toString());
    }
    clear();
  }

  public void edit() {
    int idx = list.getSelectedIndex();
    if (idx == -1) return;
    Tag tag = tags.get(idx);
    TagDialog dialog = new TagDialog(null, true);
    dialog.load(tag);
    dialog.setVisible(true);
    if (dialog.accepted()) {
      dialog.save(tag);
    }
    listModel.setElementAt(tags.get(idx).toString(), idx);
    clear();
  }

  public void delete() {
    int idx = list.getSelectedIndex();
    if (idx == -1) return;
    tags.remove(idx);
    listModel.remove(idx);
    clear();
  }

  public void run() {
    if (worker == null) {
      if (tags.size() == 0) {
        JF.showError("Error", "No tags defined");
        return;
      }
      setState(true);
      speedIdx = speed.getSelectedIndex();
      delay = delays[speedIdx];
      tickCounter = ticks[speedIdx];
      clear();
      if (logFile != null) {
        try {
          logger = new FileOutputStream(logFile);
        } catch (Exception e) {
          e.printStackTrace();
          logger = null;
        }
      } else {
        logger = null;
      }
      run.setText("Stop");
      worker = new Worker();
      worker.start();
    } else {
      worker.cancel();
    }
  }

  private void setState(boolean running) {
    newProject.setEnabled(!running);
    load.setEnabled(!running);
    save.setEnabled(!running);
    add.setEnabled(!running);
    edit.setEnabled(!running);
    delete.setEnabled(!running);
    logBtn.setEnabled(!running);
    speed.setEnabled(!running);
  }

  public void drawImage(Graphics g) {
    if (logImage.getWidth() != img.getWidth()) {
      int ow = logImage.getWidth();
      int nw = img.getWidth();
      int diff = nw - ow;
      if (diff > 0) {
        //expanding image
        int px[] = logImage.getPixels();
        logImage.setSize(img.getWidth(), 510);
        logImage.fill(0, 0, logImage.getWidth(), logImage.getHeight(), 0xffffffff);
        logImage.putPixels(px, diff, 0, ow, 510, 0);
      } else {
        //shrinking image
        diff *= -1;
        int px[] = logImage.getPixels(diff, 0, nw, 510);
        logImage.setSize(img.getWidth(), 510);
        logImage.fill(0, 0, logImage.getWidth(), logImage.getHeight(), 0xffffffff);
        logImage.putPixels(px, 0, 0, nw, 510, 0);
      }
    }
    g.drawImage(logImage.getImage(), 0, 0, null);
  }

  public static void gui(Runnable task) {
    try {
      java.awt.EventQueue.invokeAndWait(task);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static java.util.Timer timer;
  public static boolean running;

  public static class Worker extends Thread {
    public void run() {
      JFLog.log("connecting to controllers...");
      running = false;
      //create controllers and find fastest timer
      tableModel.setColumnCount(0);
      tableModel.addColumn("timestamp");
      Controller.rate = 1000 / delay;
      active = true;
      System.out.println("rate=" + Controller.rate);
      System.gc();  //ensure all prev connections are closed
      int cnt = tags.size();
      //start tag timers
      for(int a=0;a<cnt;a++) {
        Tag tag = tags.get(a);
        tag.delay = delay;
        tag.start();
        tableModel.addColumn(tag.toString());
      }
      //start timer
      timer = new java.util.Timer();
      task = new Task();
      timer.scheduleAtFixedRate(task, delay, delay);
      JFLog.log("running...");
      running = true;
    }
    public void cancel() {
      if (!running) return;
      active = false;
      timer.cancel();
      timer = null;
      worker = null;
      task = null;
      if (logger != null) {
        try { logger.close(); } catch (Exception e) {}
        logger = null;
      }
      int cnt = tags.size();
      for(int a=0;a<cnt;a++) {
        Tag tag = tags.get(a);
        tag.stop();
      }
      app.run.setText("Run");
      app.setState(false);
      running = false;
    }
  }
  public static class Task extends TimerTask {
    public String[] row;
    public int idx;
    public int delaycount = 0;
    public String ln;
    public long start = -1;
    public void run() {
      try {
        int cnt = tags.size();
        row = new String[cnt+1];
        if (start == -1) {
          start = System.nanoTime();
        }
        int timestamp = (int)((System.nanoTime() - start) / 1000000L);
        String now = Long.toString(timestamp);
        row[0] = now;
        ln = now;
        idx = 1;
        for(int a=0;a<cnt;a++) {
          Tag tag = tags.get(a);
          String data = tag.getValue();
          if (data == null) {
            row[idx] = "error";
          } else {
            row[idx] = data;
          }
          ln += ",";
          ln += row[idx];
          idx++;
        }
        ln += "\r\n";
        if (logger != null) {
          logger.write(ln.getBytes());
        }
        gui(() -> {
          tableModel.addRow(row);
          if (tableModel.getRowCount() > 10) {
            tableModel.removeRow(0);
          }
        });
        updateImage();
        delaycount += delay;
        if (delaycount >= 100) {
          gui( () -> {
            app.img.repaint();
          });
          delaycount = 0;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    public int scaleInt(Tag tag, int value) {
      if (value < tag.min) return 0;
      if (value > tag.max) return 100;
      float delta = tag.max - tag.min;
      return (int)((value - tag.min) / delta * 100.0);
    }
    public int scaleFloat(Tag tag, float value) {
      if (value < tag.fmin) return 0;
      if (value > tag.fmax) return 100;
      float delta = tag.fmax - tag.fmin;
      float fval = value;
      float fmin = tag.fmin;
      return (int)((fval - fmin) / delta * 100.0);
    }
    public int scaleDouble(Tag tag, double value) {
      if (value < tag.fmin) return 0;
      if (value > tag.fmax) return 100;
      double delta = tag.fmax - tag.fmin;
      double fval = value;
      double fmin = tag.min;
      return (int)((fval - fmin) / delta * 100.0);
    }
    public int sv, lsv;
    public void getValues(Tag tag) {
      String value = tag.getValue();
      if (value == null) value = "0";
      int iv;
      float fv;
      double dv;
      if (tag.isFloat()) {
        if (tag.getSize() == 8) {
          dv = Double.valueOf(value);
          sv = scaleDouble(tag, dv);
        } else {
          fv = Float.valueOf(value);
          sv = scaleFloat(tag, fv);
        }
      } else {
        iv = Integer.valueOf(value);
        sv = scaleInt(tag, iv);
      }
      Integer lv = (Integer)tag.getData("last");
      if (lv != null) {
        lsv = lv;
      } else {
        lsv = sv;
      }
      tag.setData("last", sv);
    }
    public void updateImage() {
      int x2 = logImage.getWidth() - 1;
      int px[] = logImage.getPixels(1, 0, x2, 510);
      logImage.putPixels(px, 0, 0, x2, 510, 0);
      logImage.line(x2, 0, x2, 509, 0xffffff);
      int cnt = tags.size();
      for(int a=0;a<cnt;a++) {
        Tag tag = tags.get(a);
        getValues(tag);
        int y = 5 + 500 - (sv * 5);
        int ly = 5 + 500 - (lsv * 5);
        logImage.line(x2-1, ly, x2, y, tag.color);
      }
      tickCounter--;
      if (tickCounter == 0) {
        tickCounter = ticks[speedIdx];
        logImage.line(x2, 0, x2, 4, 0x000000);
        logImage.line(x2, 505, x2, 509, 0x000000);
      }
    }
  }
}
