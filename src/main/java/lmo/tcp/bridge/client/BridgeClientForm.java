/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.client;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import lmo.tcp.bridge.BridgeClientConfig;
import lmo.tcp.bridge.BridgeServerConfig;
import lmo.tcp.bridge.conf.ConfigLoader;
import lmo.tcp.bridge.listener.BridgeClientListener;
import lmo.tcp.bridge.server.BridgeServer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author LMO
 */
public class BridgeClientForm extends javax.swing.JFrame {

    BridgeClient client = null;
    static Logger logger = Logger.getLogger("UI");
    boolean started = false;
    final LinkedList<Timer> timers = new LinkedList<>();

    BridgeClientListener listener = new BridgeClientListener() {

        @Override
        public void onConnectionStart() {
            connectButton.setText("Disconnect");
            startButton.setEnabled(true);
            serverStatusLabel.setText("connected");
            final long startMs = new Date().getTime();
            Timer timer = new Timer("Timer-UI." + Math.random());
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    long runtime = (new Date().getTime() - startMs) / 1000;
                    long sec = runtime % 60;
                    long min = runtime / 60 % 60;
                    long hour = runtime / 60 / 60;
                    serverStatusLabel.setText(String.format("%d:%02d:%02d", hour, min, sec));
                }
            }, 0, 1000);
            timers.addLast(timer);
            if (onDemandCheckBox.isSelected() && started) {
                startButtonActionPerformed(null);
            }
        }

        @Override
        public void onConnectionEnd() {
            try {
                timers.getFirst().cancel();
            } catch (Exception ex) {
            }
            try {
                timers.getFirst().purge();
            } catch (Exception ex) {
            }
            try {
                timers.removeFirst();
            } catch (Exception ex) {
            }
            connectButton.setText("Connect");
            startButton.setEnabled(false);
            serverStatusLabel.setText("disconnected");
            if (onDemandCheckBox.isSelected()) {
                connectButtonActionPerformed(null);
            }
        }

        @Override
        public void onServerStart() {
            startButton.setText("Stop");
            clientStatusField.setText("remote connection started");
        }

        @Override
        public void onServerEnd() {
            startButton.setText("Start");
            clientStatusField.setText("remote connection ended");
            if (onDemandCheckBox.isSelected() && started) {
                startButtonActionPerformed(null);
            }
        }

        @Override
        public void onError(String msg, Exception ex) {
            logger.error(msg, ex);
            clientStatusField.setText(msg);
        }
    };

    /**
     * Creates new form BridgeClientForm
     */
    public BridgeClientForm() {
        initComponents();
        if (onDemandCheckBox.isSelected()) {
            connectButtonActionPerformed(null);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        idField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        serverLabel = new javax.swing.JLabel();
        serverField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        remoteServerField = new javax.swing.JTextField();
        localPortField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        remoteIdField = new javax.swing.JTextField();
        onDemandCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        startButton = new javax.swing.JButton();
        serverStatusLabel = new javax.swing.JLabel();
        clientStatusField = new javax.swing.JLabel();
        localPassField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        remotePassField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TCP Bridge Client");
        setResizable(false);

        idField.setText("2");

        jLabel1.setText("Local ID");

        serverLabel.setText("Bridge Server");

        serverField.setText("103.9.89.165:1783");

        jLabel2.setText("Remote Server");

        remoteServerField.setText("localhost:3389");
        remoteServerField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remoteServerFieldActionPerformed(evt);
            }
        });

        localPortField.setText("13000");

        jLabel3.setText("Local Port");

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Remote ID");

        remoteIdField.setText("2");

        onDemandCheckBox.setSelected(true);
        onDemandCheckBox.setText("On demand");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        startButton.setText("Start");
        startButton.setEnabled(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        serverStatusLabel.setText("...");

        clientStatusField.setText("...");

        jLabel4.setText("Local Pass");

        jLabel6.setText("Remote Pass");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(onDemandCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(serverLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(localPassField)
                            .addComponent(serverField)
                            .addComponent(idField)
                            .addComponent(connectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(remoteIdField)
                            .addComponent(remotePassField)
                            .addComponent(remoteServerField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(localPortField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(clientStatusField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(remoteIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(remotePassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(serverField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(serverLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(localPassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(remoteServerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(onDemandCheckBox)
                                .addComponent(connectButton))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(startButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientStatusField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(serverStatusLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed

        if (client != null && client.isConnected()) {
            client.disconnect();
        } else {
            URI serverURI = URI.create("tcp://" + serverField.getText());
            int id = Integer.parseInt(idField.getText());
            client = new BridgeClient(serverURI.getHost(), serverURI.getPort(), id, System.getProperty("user.name"), localPassField.getText().trim());
            client.setListener(listener);
            client.connect();
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    private void remoteServerFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remoteServerFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_remoteServerFieldActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        if (client.isConnected()) {
            if (!client.isStarted()) {
                int localPort = Integer.parseInt(localPortField.getText());
                URI remoteURI = URI.create("tcp://" + remoteServerField.getText());
                int dstId = Integer.parseInt(remoteIdField.getText());
                int dstPort = remoteURI.getPort();
                String dstHost = remoteURI.getHost();
                client.setRemote(dstId, dstHost, dstPort, remotePassField.getText().trim(), localPort);
                client.start();
                if (evt != null) {
                    started = true;
                }
            } else {
                client.stop();
                if (evt != null) {
                    started = false;
                }
            }
        }
    }//GEN-LAST:event_startButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        BasicConfigurator.configure();
        if (args.length == 2) {
            PropertyConfigurator.configure(args[1]);
            String mode = args[0];
            if (mode.equals("server")) {
                ConfigLoader.load(BridgeServerConfig.class, args[1]);
                new BridgeServer(BridgeServerConfig.port).start();
                return;
            } else if (mode.equals("client")) {
                ConfigLoader.load(BridgeClientConfig.class, args[1]);
                final LinkedList<Timer> timers = new LinkedList<>();
                final BridgeClient client = new BridgeClient(
                        BridgeClientConfig.shost, BridgeClientConfig.sport,
                        BridgeClientConfig.srcid, System.getProperty("user.name"), BridgeClientConfig.srcpass);
                client.setRemote(BridgeClientConfig.dstid, BridgeClientConfig.rhost, BridgeClientConfig.rport, BridgeClientConfig.dstpass,
                        BridgeClientConfig.port);
                client.setListener(new BridgeClientListener() {

                    @Override
                    public void onConnectionStart() {
                        final long startMs = new Date().getTime();
                        Timer timer = new Timer("Timer-UI." + Math.random());
                        timer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                long runtime = (new Date().getTime() - startMs) / 1000;
                                long sec = runtime % 60;
                                long min = runtime / 60 % 60;
                                long hour = runtime / 60 / 60;
                                logger.info(String.format("%d:%02d:%02d", hour, min, sec));
                            }
                        }, 0, 5000);
                        timers.addLast(timer);
                        logger.info("server connection started, starting local server");
                        client.start();
                    }

                    @Override
                    public void onConnectionEnd() {
                        try {
                            timers.getFirst().cancel();
                            timers.getFirst().purge();
                            timers.removeFirst();
                        } catch (Exception ex) {
                        }
                        logger.info("server connection ended, starting again");
                        client.connect();
                    }

                    @Override
                    public void onServerStart() {
                        logger.info("local server started");
                    }

                    @Override
                    public void onServerEnd() {
                        logger.info("local server ended");
                        client.start();
                    }

                    @Override
                    public void onError(String msg, Exception ex) {
                        logger.error(msg, ex);
                    }

                });
                client.connect();
                return;
            } else {
                logger.info("mode is invalid: server or client");
            }
        } else if (args.length > 0) {
            logger.info("args format invalid: mode config");
            return;
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BridgeClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BridgeClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BridgeClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BridgeClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BridgeClientForm().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel clientStatusField;
    private javax.swing.JButton connectButton;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField localPassField;
    private javax.swing.JTextField localPortField;
    private javax.swing.JCheckBox onDemandCheckBox;
    private javax.swing.JTextField remoteIdField;
    private javax.swing.JTextField remotePassField;
    private javax.swing.JTextField remoteServerField;
    private javax.swing.JTextField serverField;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JLabel serverStatusLabel;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
