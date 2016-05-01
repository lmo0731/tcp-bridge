/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.client;

import java.io.InputStream;
import java.net.Socket;
import lmo.tcp.bridge.BridgeData;
import lmo.tcp.bridge.listener.BridgeDataListener;
import lmo.tcp.bridge.listener.impl.DefaultBridgeDataListener;
import lmo.tcp.bridge.server.BridgeServer;

/**
 *
 * @author LMO
 */
public class BridgeDataHandler implements Runnable {

    int srcId;
    Socket socket;
    String host;
    int port;
    BridgeDataListener listener = new DefaultBridgeDataListener();

    public BridgeDataHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setListener(BridgeDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(BridgeServer.SO_TIMEOUT);
            listener.onConnect();
            InputStream in = socket.getInputStream();
            while (true) {
                BridgeData d = BridgeData.read(in);
                listener.onRead(d);
            }
        } catch (Exception ex) {
            listener.onError("server connection error", ex);
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
            }
            try {
                listener.onDisconnect();
            } catch (Exception ex) {
            }
        }
    }

    synchronized public void send(BridgeData data) {
        try {
            data.srcId = srcId;
            data.write(socket.getOutputStream());
            listener.onSend(data);
        } catch (Exception ex) {
            listener.onError("sending data error", ex);
        }
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public void start() {
        new Thread(this).start();
    }

    public void end() {
        try {
            socket.close();
        } catch (Exception ex) {
        }
    }

}
