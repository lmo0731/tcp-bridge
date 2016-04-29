/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.client;

import lmo.tcp.bridge.listener.BridgeClientListener;
import lmo.tcp.bridge.listener.TcpDataListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lmo.tcp.bridge.BridgeData;
import lmo.tcp.bridge.listener.BridgeDataListener;
import lmo.tcp.bridge.listener.impl.DefaultBridgeClientListener;
import org.apache.log4j.Logger;

/**
 *
 * @author LMO
 */
public class BridgeClient implements Runnable {

    Logger logger;
    int srcId;
    int localPort;
    String serverHost;
    int serverPort;
    String dstHost = "localhost";
    int dstId;
    int dstPort;
    String description;
    ServerSocket ss;
    BridgeDataHandler serverConnection;
    Map<Integer, TcpDataHandler> clients = new HashMap<>();
    Map<Integer, TcpDataHandler> servers = new HashMap<>();
    BridgeClientListener listener = new DefaultBridgeClientListener();

    public BridgeClient(String serverHost, int serverPort, int srcId, String description) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.srcId = srcId;
        this.description = description;
        this.logger = Logger.getLogger("bridgeclient");
    }

    public int getPort() {
        return localPort;
    }

    public void setListener(BridgeClientListener listener) {
        this.listener = listener;
    }

    void setRemote(int dstId, String dstHost, int dstPort, int localPort) {
        this.dstId = dstId;
        this.dstHost = dstHost;
        this.dstPort = dstPort;
        this.localPort = localPort;
        this.logger = Logger.getLogger("bridgeclient." + localPort);
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        try {
            ss = new ServerSocket(localPort);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.info("clients: " + clients);
                    logger.info("servers: " + servers);
                }
            }, 0, 5000);
            logger.info("client server started on port " + localPort);
            listener.onServerStart();
            while (true) {
                final Socket s = ss.accept();
                logger.info("client connected: " + s);
                final TcpDataHandler dataHandler = new TcpDataHandler(s, s.getPort());
                final Logger logger = Logger.getLogger("client." + dataHandler.id);
                dataHandler.setListener(new TcpDataListener() {

                    @Override
                    public void onRead(int id, int seq, byte[] b) {
                        logger.info("client request: " + b.length);
                        BridgeData d = new BridgeData();
                        d.srcPort = id;
                        d.data = b;
                        d.dataType = BridgeData.TYPE_REQ;
                        d.dstId = dstId;
                        d.dstPort = dataHandler.getDstPort();
                        d.dataSeq = seq;
                        d.dataLen = b.length;
                        d.data = b;
                        serverConnection.send(d);
                    }

                    @Override
                    public void onWrite(int id, byte[] b) {
                        logger.info("client response: " + b.length);
                    }

                    @Override
                    public void onStart(int id) {
                        logger.info("client start: " + id);
                        clients.put(id, dataHandler);
                        BridgeData d = new BridgeData();
                        d.srcPort = id;
                        d.dataType = BridgeData.TYPE_OPEN_REQ;
                        d.dstId = dstId;
                        d.dstPort = dstPort;
                        d.data = dstHost.getBytes();
                        d.dataLen = d.data.length;
                        serverConnection.send(d);
                        logger.info("waiting for ready");
                        dataHandler.waitReady();
                    }

                    @Override
                    public void onEnd(int id) {
                        logger.info("client end: " + id);
                        clients.remove(id);
                        BridgeData d = new BridgeData();
                        d.srcPort = id;
                        d.dataType = BridgeData.TYPE_CLOSE_REQ;
                        d.dstId = dstId;
                        d.dstPort = dataHandler.getDstPort();
                        d.dataLen = 0;
                        d.data = new byte[0];
                        serverConnection.send(d);
                    }

                    @Override
                    public void onError(int id, String message, Exception ex) {
                        logger.error("client connection error: " + id + ", " + message, ex);
                    }

                });
                new Thread(dataHandler).start();
            }
        } catch (Exception ex) {
            logger.error("bridge client error", ex);
        } finally {
            try {
                ss.close();
            } catch (Exception ex) {
            }
            try {
                ss = null;
            } catch (Exception ex) {
            }
            try {
                timer.cancel();
                timer.purge();
            } catch (Exception ex) {
            }
            listener.onServerEnd();
        }
    }

    public boolean isStarted() {
        return ss != null;
    }

    public synchronized void connect() {
        final Logger logger = Logger.getLogger("server." + serverHost + "." + serverPort);
        logger.info("connecting to server");
        if (serverConnection != null) {
            logger.info("already connected to server");
            return;
        }

        final BridgeDataHandler dataHandler = new BridgeDataHandler(serverHost, serverPort);
        dataHandler.setListener(new BridgeDataListener() {

            @Override
            public void onConnect() {
                logger.info("connected to server");
                BridgeData d = new BridgeData();
                d.srcId = srcId;
                d.dataType = BridgeData.TYPE_START;
                d.data = description.getBytes();
                d.dataLen = d.data.length;
                dataHandler.setSrcId(srcId);
                dataHandler.send(d);
            }

            @Override
            public void onRead(BridgeData data) throws Exception {
                logger.info("data from server: " + data);
                if (data.dataType == BridgeData.TYPE_OPEN_REQ) {
                    open(data.srcId, data.srcPort, new String(data.data), data.dstPort);
                } else if (data.dataType == BridgeData.TYPE_OPEN_RES) {
                    TcpDataHandler handler = clients.get(data.dstPort);
                    if (handler != null) {
                        handler.setDstPort(data.srcPort);
                        handler.ready();
                        logger.info("ready to go");
                    }
                } else if (data.dataType == BridgeData.TYPE_REQ) {
                    TcpDataHandler handler = servers.get(data.dstPort);
                    if (handler != null) {
                        handler.send(data.data);
                    }
                } else if (data.dataType == BridgeData.TYPE_RES) {
                    TcpDataHandler handler = clients.get(data.dstPort);
                    if (handler != null) {
                        handler.send(data.data);
                    }
                } else if (data.dataType == BridgeData.TYPE_CLOSE_REQ) {
                    TcpDataHandler serverHandler = servers.get(data.dstPort);
                    if (serverHandler != null) {
                        serverHandler.end();
                    }
                } else if (data.dataType == BridgeData.TYPE_CLOSE_RES) {
                    TcpDataHandler clientHandler = clients.get(data.dstPort);
                    if (clientHandler != null) {
                        clientHandler.end();
                    }
                } else if (data.dataType == BridgeData.TYPE_START) {
                    logger.info("server connection success: ID=" + data.dstId);
                    dataHandler.setSrcId(data.dstId);
                    if (serverConnection != null) {
                        logger.warn("another server connection already exists. closing this connection");
                        dataHandler.end();
                    } else {
                        serverConnection = dataHandler;
                        listener.onConnectionStart();
                    }
                } else if (data.dataType == BridgeData.TYPE_PING) {
                    BridgeData d = new BridgeData();
                    d.dataType = BridgeData.TYPE_PONG;
                    d.data = new byte[0];
                    d.dataLen = d.data.length;
                    serverConnection.send(d);
                } else {
                    logger.warn("unknown datatype: " + data.dataType);
                }
            }

            @Override
            public void onSend(BridgeData data) {
                logger.info("send to server: " + data);
            }

            @Override
            public void onDisconnect() {
                for (TcpDataHandler h : clients.values()) {
                    h.end();
                }
                for (TcpDataHandler h : servers.values()) {
                    h.end();
                }
                logger.info("disconnected from server");
                if (serverConnection == dataHandler) {
                    serverConnection = null;
                    listener.onConnectionEnd();
                    stop();
                }
            }

            @Override
            public void onError(String message, Exception ex) {
                logger.error(message, ex);
            }
        });
        dataHandler.start();
    }

    void open(final int srcId, final int srcPort, final String host, final int port) throws IOException {
        logger.info("connecting to " + host + ":" + port);
        Socket s = new Socket(host, port);
        final TcpDataHandler dataHandler = new TcpDataHandler(s, s.getLocalPort());
        final Logger logger = Logger.getLogger("app." + dataHandler.id);
        dataHandler.setListener(new TcpDataListener() {

            @Override
            public void onRead(int id, int seq, byte[] b) throws Exception {
                logger.info("response from app: " + b.length);
                BridgeData d = new BridgeData();
                d.srcPort = id;
                d.dataType = BridgeData.TYPE_RES;
                d.dstId = srcId;
                d.dstPort = srcPort;
                d.dataSeq = seq;
                d.dataLen = b.length;
                d.data = b;
                serverConnection.send(d);
            }

            @Override
            public void onWrite(int id, byte[] b) throws Exception {
                logger.info("request to app: " + b.length);
            }

            @Override
            public void onStart(int id) throws Exception {
                logger.info("connected to " + host + ":" + port);
                servers.put(id, dataHandler);
                BridgeData d = new BridgeData();
                d.srcPort = id;
                d.dataType = BridgeData.TYPE_OPEN_RES;
                d.dstId = srcId;
                d.dstPort = srcPort;
                d.dataLen = 0;
                d.data = new byte[0];
                serverConnection.send(d);
            }

            @Override
            public void onEnd(int id) throws Exception {
                logger.info("disconnected from " + host + ":" + port);
                servers.remove(id);
                BridgeData d = new BridgeData();
                d.srcPort = id;
                d.dataType = BridgeData.TYPE_CLOSE_RES;
                d.dstId = srcId;
                d.dstPort = srcPort;
                d.dataLen = 0;
                d.data = new byte[0];
                serverConnection.send(d);
            }

            @Override
            public void onError(int id, String message, Exception ex) {
                logger.error("app connection error " + id + ", " + message, ex);
            }
        });
        dataHandler.start();
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        try {
            ss.close();
        } catch (Exception ex) {
        }
    }

    public void disconnect() {
        if (serverConnection != null) {
            serverConnection.end();
        }
    }

    public boolean isConnected() {
        return serverConnection != null;
    }
}
