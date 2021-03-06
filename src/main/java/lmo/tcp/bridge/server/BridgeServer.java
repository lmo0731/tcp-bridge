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

    public static final int SO_TIMEOUT = 15000;
    public static final int PING_DELAY = 10000;

    final Logger logger;
    int port;
    ServerSocket ss;
    Map<Integer, BridgeDataHandler> clients = new HashMap<>();

    public BridgeServer(int port) {
        this.port = port;
        logger = Logger.getLogger("bridgeserver." + port);

    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        Timer timer = new Timer("Timer-Server-" + port + "-" + Math.random());
        try {
            ss = new ServerSocket(port);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.info("clients: " + clients);
                }
            }, 0, 10000);
            logger.info("bridge server started: " + ss);
            while (true) {
                Socket s = ss.accept();
                s.setSoTimeout(BridgeServer.SO_TIMEOUT);
                logger.info("bridge client connecting: " + s);
                final Timer pingTimer = new Timer("Timer-Ping-" + s);
                final BridgeDataHandler dataHandler = new BridgeDataHandler(s);
                dataHandler.setListener(new BridgeDataListener() {

                    @Override
                    public void onConnect() throws Throwable {
                        logger.info("bridge client init");
                    }

                    @Override
                    public void onRead(BridgeData d) throws Throwable {
                        logger.info("received from bridge client: " + d);
                        if (d.dataType == BridgeData.TYPE_START) {
                            if (!clients.containsKey(d.srcId)) {
                                dataHandler.id = d.srcId;
                                dataHandler.description = new String(d.data);
                                logger.info("bridge client connected: " + dataHandler.id);
                                clients.put(dataHandler.id, dataHandler);
                                dataHandler.send(d);
                                pingTimer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        try {
                                            BridgeData d = new BridgeData();
                                            d.dataType = BridgeData.TYPE_PING;
                                            d.data = new byte[0];
                                            d.dataLen = d.data.length;
                                            dataHandler.send(d);
                                        } catch (Throwable ex) {
                                            logger.error("ping fail: " + dataHandler.id);
                                        }
                                    }
                                }, BridgeServer.PING_DELAY, BridgeServer.PING_DELAY);
                            } else {
                                logger.info("bridge client id already exists: " + d.srcId);
                                dataHandler.end();
                            }
                        } else if (d.dataType == BridgeData.TYPE_CLOSE_REQ
                                || d.dataType == BridgeData.TYPE_CLOSE_RES
                                || d.dataType == BridgeData.TYPE_OPEN_REQ
                                || d.dataType == BridgeData.TYPE_OPEN_RES
                                || d.dataType == BridgeData.TYPE_REQ
                                || d.dataType == BridgeData.TYPE_RES) {
                            BridgeDataHandler client = clients.get(d.dstId);
                            if (client != null) {
                                client.send(d);
                            } else if (d.dataType != BridgeData.TYPE_CLOSE_REQ && d.dataType != BridgeData.TYPE_CLOSE_RES) {
                                BridgeData d1 = new BridgeData();
                                d1.dataType = (d.dataType == BridgeData.TYPE_REQ || d.dataType == BridgeData.TYPE_OPEN_REQ) ? (BridgeData.TYPE_CLOSE_RES) : (BridgeData.TYPE_CLOSE_REQ);
                                d1.srcId = d.dstId;
                                d1.srcPort = d.dstPort;
                                d1.dstId = d.srcId;
                                d1.dstPort = d.srcPort;
                                d1.dataLen = 0;
                                d1.data = new byte[0];
                                dataHandler.send(d1);
                            }
                        }
                    }

                    @Override
                    public void onSend(BridgeData data) throws Throwable {
                        logger.info("sent to bridge client: " + data);
                    }

                    @Override
                    public void onDisconnect() throws Throwable {
                        if (dataHandler.id != null) {
                            logger.info("bridge client disconnected: " + dataHandler.id);
                            clients.remove(dataHandler.id);
                        }
                        try {
                            pingTimer.cancel();
                        } catch (Throwable ex) {
                        }
                        try {
                            pingTimer.purge();
                        } catch (Throwable ex) {
                        }
                    }

                    @Override
                    public void onError(String message, Throwable ex) {
                        logger.error(message, ex);
                    }
                });
                dataHandler.start();
            }
        } catch (Throwable ex) {
            logger.error("bridge server error", ex);
        } finally {
            try {
                ss.close();
            } catch (Throwable ex) {
            }
            try {
                timer.cancel();
            } catch (Throwable ex) {
            }
            try {
                timer.purge();
            } catch (Throwable ex) {
            }
            for (BridgeDataHandler handler : clients.values()) {
                handler.end();
            }
            logger.info("bridge server stopped");
        }
    }

    public void start() {
        new Thread(this, "BridgeServer-" + port).start();
    }
}
