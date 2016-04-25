/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.listener;

/**
 *
 * @author LMO
 */
public interface TcpDataListener {

    void onRead(int id, int seq, byte[] b) throws Exception;

    void onWrite(int id, byte[] b) throws Exception;

    void onStart(int id) throws Exception;

    void onEnd(int id) throws Exception;

    void onError(int id, String message, Exception ex);
}
