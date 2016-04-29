/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.client;

import java.net.URI;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import lmo.tcp.bridge.listener.BridgeClientListener;
import lmo.tcp.bridge.server.BridgeServer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author LMO
 */
public class BridgeClientForm extends javax.swing.JFrame {

    BridgeClient client = null;
    static Logger logger = Logger.getLogger("UI");
    boolean started = false;

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(onDemandCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(serverLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(serverField)
                            .addComponent(idField)
                            .addComponent(connectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(remoteServerField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(remoteIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(remoteServerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(remoteIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(localPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(startButton)))
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
                                    .addComponent(connectButton)
                                    .addComponent(onDemandCheckBox))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientStatusField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(serverStatusLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        final Timer timer = new Timer();
        BridgeClientListener listener = new BridgeClientListener() {

            @Override
            public void onConnectionStart() {
                connectButton.setText("Disconnect");
                startButton.setEnabled(true);
                serverStatusLabel.setText("connected");
                final long startMs = new Date().getTime();
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
                if (onDemandCheckBox.isSelected() && started) {
                    startButtonActionPerformed(null);
                }
            }

            @Override
            public void onConnectionEnd() {
                connectButton.setText("Connect");
                if (onDemandCheckBox.isSelected()) {
                    connectButtonActionPerformed(null);
                }
                startButton.setEnabled(false);
                serverStatusLabel.setText("disconnected");
                timer.cancel();
                timer.purge();
            }

            @Override
            public void onServerStart() {
                startButton.setText("Stop");
                clientStatusField.setText("remote connection started");
            }

            @Override
            public void onServerEnd() {
                startButton.setText("Start");
                if (onDemandCheckBox.isSelected() && started) {
                    startButtonActionPerformed(null);
                }
                clientStatusField.setText("remote connection ended");
            }
        };
        if (client != null && client.isConnected()) {
            client.disconnect();
        } else {
            URI serverURI = URI.create("tcp://" + serverField.getText());
            int id = Integer.parseInt(idField.getText());
            client = new BridgeClient(serverURI.getHost(), serverURI.getPort(), id, System.getProperty("user.name"));
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
                client.setRemote(dstId, dstHost, dstPort, localPort);
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
    public static void main(String args[]) {
        BasicConfigurator.configure();
        if (args.length == 1) {
            new BridgeServer(Integer.parseInt(args[0])).start();
            return;
        } else if (args.length == 7) {
            final Timer timer = new Timer();
            final BridgeClient client = new BridgeClient(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), System.getProperty("user.name"));
            client.setRemote(Integer.parseInt(args[3]), args[4], Integer.parseInt(args[5]), Integer.parseInt(args[6]));
            client.setListener(new BridgeClientListener() {

                @Override
                public void onConnectionStart() {
                    final long startMs = new Date().getTime();
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
                    logger.info("server connection started, starting local server");
                    client.start();
                }

                @Override
                public void onConnectionEnd() {
                    timer.cancel();
                    timer.purge();
                    logger.info("server connection ended, starting again");
                    client.connect();
                }

                @Override
                public void onServerStart() {
                    logger.info("local server started");
                }

                @Override
                public void onServerEnd() {
                    logger.info("local server ended, starting again");
                    client.start();
                }
            });
            client.connect();
            return;
        } else if (args.length > 0) {
            logger.info("shost sport srcid dstid rhost rport lport");
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField localPortField;
    private javax.swing.JCheckBox onDemandCheckBox;
    private javax.swing.JTextField remoteIdField;
    private javax.swing.JTextField remoteServerField;
    private javax.swing.JTextField serverField;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JLabel serverStatusLabel;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
