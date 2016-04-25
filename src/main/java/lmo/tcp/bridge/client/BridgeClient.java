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
    String localHost = "localhost";
    int dstId;
    int dstPort;
    ServerSocket ss;
    BridgeDataHandler serverConnection;
    Map<Integer, TcpDataHandler> clients = new HashMap<>();
    Map<Integer, TcpDataHandler> servers = new HashMap<>();
    BridgeClientListener listener = new DefaultBridgeClientListener();
    boolean running = false;

    public BridgeClient(String serverHost, int serverPort, int localPort, int srcId, String localHost) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.localPort = localPort;
        this.srcId = srcId;
        this.logger = Logger.getLogger("CLIENT." + localPort);
        this.localHost = localHost;
        new Timer().schedule(new TimerTask() {

            public void run() {
                logger.info("clients: " + clients);
                logger.info("servers: " + servers);
            }
        }, 0, 5000);
    }

    public int getPort() {
        return localPort;
    }

    public void setListener(BridgeClientListener listener) {
        this.listener = listener;
    }

    void setRemote(int id, int port) {
        dstId = id;
        dstPort = port;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                serverConnection = connect();
                ss = new ServerSocket(localPort);
                listener.onStart();
                while (true) {
                    final Socket s = ss.accept();
                    logger.info("client connected: " + s);
                    final TcpDataHandler dataHandler = new TcpDataHandler(s);
                    final Logger logger = Logger.getLogger("client." + dataHandler.id);
                    dataHandler.setListener(new TcpDataListener() {

                        @Override
                        public void onRead(int id, byte[] b) {
                            logger.info("client request: " + b.length);
                            BridgeData d = new BridgeData();
                            d.srcPort = id;
                            d.data = b;
                            d.dataType = BridgeData.TYPE_REQ;
                            d.dstId = dstId;
                            d.dstPort = dstPort;
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
                        }

                        @Override
                        public void onEnd(int id) {
                            logger.info("client end: " + id);
                            clients.remove(id);
                            BridgeData d = new BridgeData();
                            d.srcPort = id;
                            d.dataType = BridgeData.TYPE_CLOSE_REQ;
                            d.dstId = dstId;
                            d.dstPort = dstPort;
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
            } catch (IOException ex) {
                logger.error("starting client server error", ex);
                listener.onError("server error", ex);
            } finally {
                close();
                listener.onEnd();
            }
        }
    }

    public BridgeDataHandler connect() throws IOException {
        logger.info("connecting to server");
        try {
            final BridgeDataHandler dataHandler = new BridgeDataHandler(serverHost, serverPort);
            final Logger logger = Logger.getLogger("server");
            dataHandler.setListener(new BridgeDataListener() {

                @Override
                public void onConnect() {
                    logger.info("connected to server");
                    BridgeData d = new BridgeData();
                    d.srcId = srcId;
                    d.dataType = BridgeData.TYPE_START;
                    d.data = new byte[0];
                    d.dataLen = 0;
                    serverConnection.setSrcId(srcId);
                    serverConnection.send(d);
                }

                @Override
                public void onRead(BridgeData data) throws Exception {
                    logger.info("data from server: " + data);
                    if (data.dataType == BridgeData.TYPE_REQ) {
                        TcpDataHandler handler = servers.get(data.dstPort);
                        if (handler != null) {
                            handler.send(data.data);
                        } else {
                            open(data.srcId, data.srcPort, localHost, data.dstPort);
                            this.onRead(data);
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
                        logger.info("MY ID: " + data.srcId);
                        dataHandler.setSrcId(data.srcId);
                    }
                }

                @Override
                public void onSend(BridgeData data) {
                    logger.info("send to server: " + data);
                }

                @Override
                public void onDisconnect() {
                    logger.info("disconnected from server");
                    close();
                }

                @Override
                public void onError(String message, Exception ex) {
                    logger.error("server connection error: " + message, ex);
                    listener.onError(message, ex);
                }
            });
            dataHandler.start();
            return dataHandler;
        } catch (Exception ex) {
            throw new IOException("cant connect to bridge server", ex);
        }

    }

    void open(final int srcId, final int srcPort, final String host, final int port) throws IOException {
        logger.info("connecting to " + host + ":" + port);
        Socket s = new Socket(host, port);
        final TcpDataHandler dataHandler = new TcpDataHandler(s);
        final Logger logger = Logger.getLogger("app." + dataHandler.id);
        dataHandler.setListener(new TcpDataListener() {

            @Override
            public void onRead(int id, byte[] b) throws Exception {
                logger.info("response from app: " + b.length);
                BridgeData d = new BridgeData();
                d.srcPort = id;
                d.dataType = BridgeData.TYPE_RES;
                d.dstId = srcId;
                d.dstPort = srcPort;
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
                listener.onError(message, ex);
            }
        });
        dataHandler.start();
    }

    public void start() {
        new Thread(this).start();
    }

    public void close() {
        try {
            ss.close();
        } catch (Exception ex) {
        }
        serverConnection.end();
    }
}
