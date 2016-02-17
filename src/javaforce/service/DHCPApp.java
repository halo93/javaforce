package javaforce.service;

/**
 *
 * @author pquiring
 *
 * Created : Nov 16, 2013
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javaforce.*;
import javaforce.jbus.*;

public class DHCPApp extends javax.swing.JFrame {

  /**
   * Creates new form DHCPApp
   */
  public DHCPApp() {
    initComponents();
    //create tray icon to open app
    JFImage img = new JFImage();
    img.loadPNG(this.getClass().getResourceAsStream("/javaforce/icons/dhcp.png"));
    new Thread() {
      public void run() {
        Random r = new Random();
        busClient = new JBusClient(DHCP.busPack + ".client" + r.nextInt(), new JBusMethods());
        busClient.setPort(DHCP.getBusPort());
        busClient.start();
        busClient.call(DHCP.busPack, "getConfig", "\"" + busClient.pack + "\"");
      }
    }.start();
    JF.centerWindow(this);
  }

  public void readConfig() {
    try {
      BufferedReader br = new BufferedReader(new FileReader(JF.getUserPath() + "/.jfdhcp.cfg"));
      StringBuilder str = new StringBuilder();
      while (true) {
        String ln = br.readLine();
        if (ln == null) break;
        str.append(ln);
        str.append("\n");
      }
      config.setText(str.toString());
    } catch (Exception e) {
      JFLog.log(e);
    }
  }

  public void writeConfig() {
    busClient.call(DHCP.busPack, "setConfig", busClient.quote(busClient.encodeString(config.getText())));
  }

  public void restart() {
    busClient.call(DHCP.busPack, "restart", "");
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    save = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    config = new javax.swing.JTextArea();
    jLabel1 = new javax.swing.JLabel();
    viewLog = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("DHCP Server");

    save.setText("Save");
    save.setEnabled(false);
    save.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveActionPerformed(evt);
      }
    });

    config.setColumns(20);
    config.setRows(5);
    config.setText(" [ loading ... ]");
    config.setEnabled(false);
    jScrollPane1.setViewportView(config);

    jLabel1.setText("DHCP Configuration:");

    viewLog.setText("View Log");
    viewLog.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        viewLogActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(viewLog)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(save))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(save)
          .addComponent(viewLog))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
    writeConfig();
    restart();
    JF.showMessage("Notice", "Settings saved!");
  }//GEN-LAST:event_saveActionPerformed

  private void viewLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLogActionPerformed
    showViewLog();
  }//GEN-LAST:event_viewLogActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new DHCPApp().setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JTextArea config;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JButton save;
  private javax.swing.JButton viewLog;
  // End of variables declaration//GEN-END:variables

  public ViewLog viewer;

  public void showViewLog() {
    if (viewer == null || viewer.isClosed) {
      viewer = new ViewLog(DHCP.getLogFile());
    }
    viewer.setTitle("DHCP Log");
    viewer.setVisible(true);
  }

  public void hideViewLog() {
    if (viewer != null) {
      if (!viewer.isClosed) viewer.dispose();
      viewer = null;
    }
  }

  public JBusClient busClient;

  public class JBusMethods {
    public void getConfig(String cfg) {
      final String _cfg = cfg;
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          config.setText(JBusClient.decodeString(_cfg));
          config.setEnabled(true);
          save.setEnabled(true);
        }
      });
    }
  }
}
