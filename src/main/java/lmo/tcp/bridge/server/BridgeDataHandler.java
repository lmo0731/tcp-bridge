/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import lmo.tcp.bridge.BridgeData;
import lmo.tcp.bridge.listener.BridgeDataListener;
import lmo.tcp.bridge.listener.impl.DefaultBridgeDataListener;
import org.apache.log4j.Logger;

/**
 *
 * @author LMO
 */
public class BridgeDataHandler implements Runnable {

    Logger logger;
    Integer id = null;
    Socket socket;
    BridgeDataListener listener = new DefaultBridgeDataListener();

    public BridgeDataHandler(Socket socket) {
        this.socket = socket;
    }

    public void setListener(BridgeDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            InputStream in = this.socket.getInputStream();
            while (!this.socket.isClosed()) {
                try {
                    BridgeData d = BridgeData.read(in);
                    listener.onRead(d);
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
            listener.onError("", ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
            }
            try {
                listener.onDisconnect();
            } catch (Exception ex) {
            }
        }
    }

    synchronized public void send(BridgeData d) throws Exception {
        d.write(socket.getOutputStream());
        listener.onSend(d);
    }

    public void start() {
        new Thread(this).start();
    }

    public void end() {
        try {
            socket.close();
        } catch (IOException ex) {
        }
    }
}
