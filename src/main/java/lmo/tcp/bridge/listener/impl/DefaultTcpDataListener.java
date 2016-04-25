/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.listener.impl;

import lmo.tcp.bridge.listener.TcpDataListener;

/**
 *
 * @author LMO
 */
public class DefaultTcpDataListener implements TcpDataListener {

    @Override
    public void onRead(int id, int seq, byte[] b) throws Exception {

    }

    @Override
    public void onWrite(int id, byte[] b) {

    }

    @Override
    public void onStart(int id) {

    }

    @Override
    public void onEnd(int id) {

    }

    @Override
    public void onError(int id, String message, Exception ex) {
    }

}
