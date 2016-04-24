/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lmo.tcp.bridge.BridgeData;
import lmo.tcp.bridge.listener.BridgeDataListener;
import org.apache.log4j.Logger;

/**
 *
 * @author LMO
 */
public class BridgeServer implements Runnable {

    int seq = 0;
    final Logger logger;
    int port;
    ServerSocket ss;
    Map<Integer, BridgeDataHandler> clients = new HashMap<>();

    public synchronized int getSeq() {
        return seq++;
    }

    public BridgeServer(int port) {
        this.port = port;
        logger = Logger.getLogger("SERVER." + port);
        new Timer().schedule(new TimerTask() {

            public void run() {
                logger.info("clients: " + clients);
            }
        }, 0, 10000);
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(port);
            logger.info("starting bridge server " + ss);
            while (true) {
                Socket s = ss.accept();
                logger.info("client connection: " + s);
                final BridgeDataHandler dataHandler = new BridgeDataHandler(s);
                dataHandler.setListener(new BridgeDataListener() {

                    @Override
                    public void onConnect() throws Exception {
                        logger.info("client connected: " + dataHandler.id);
                        clients.put(dataHandler.id, dataHandler);
                    }

                    @Override
                    public void onRead(BridgeData d) throws Exception {
                        logger.info("received from client: " + d);
                        if (d.dataType == BridgeData.TYPE_START) {
                            if (!clients.containsKey(d.srcId)) {
                                dataHandler.id = d.srcId;
                                onConnect();
                                dataHandler.send(d);
                            } else {
                                logger.info("client already exists: " + d.srcId);
                                dataHandler.end();
                            }
                        } else {
                            BridgeDataHandler client = clients.get(d.dstId);
                            if (client != null) {
                                client.send(d);
                            }
                        }
                    }

                    @Override
                    public void onSend(BridgeData data) throws Exception {
                        logger.info("sent to client: " + data);
                    }

                    @Override
                    public void onDisconnect() throws Exception {
                        if (dataHandler.id != null) {
                            logger.info("client disconnected: " + dataHandler.id);
                            clients.remove(dataHandler.id);
                        }
                    }

                    @Override
                    public void onError(String message, Exception ex) {
                        logger.error(message, ex);
                    }
                });
                dataHandler.start();
            }
        } catch (Exception ex) {
            logger.error("starting bridge server error", ex);
        } finally {
            try {
                ss.close();
            } catch (Exception ex) {
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }
}
