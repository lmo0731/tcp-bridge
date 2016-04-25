/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.client;

import lmo.tcp.bridge.listener.TcpDataListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import lmo.tcp.bridge.listener.impl.DefaultTcpDataListener;

/**
 *
 * @author LMO
 */
public class TcpDataHandler implements Runnable {

    int id;
    Socket socket;
    int dstPort;
    TcpDataListener listener = new DefaultTcpDataListener();
    boolean ready = false;

    public TcpDataHandler(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    public void setListener(TcpDataListener listener) {
        this.listener = listener;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public void waitReady() {
        if (!ready) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public void ready() {
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            InputStream in = this.socket.getInputStream();
            listener.onStart(id);
            int seq = 0;
            while (true) {
                int l = in.read(buffer);
                if (l == -1) {
                    break;
                }
                listener.onRead(id, seq++, Arrays.copyOf(buffer, l));
            }
        } catch (Exception ex) {
            listener.onError(id, "", ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
            }
            try {
                listener.onEnd(id);
            } catch (Exception ex) {
            }
        }
    }

    public void send(byte[] b) throws Exception {
        socket.getOutputStream().write(b);
        listener.onWrite(id, b);
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
