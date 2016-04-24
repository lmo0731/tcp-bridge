/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge.listener;

import lmo.tcp.bridge.BridgeData;

/**
 *
 * @author LMO
 */
public interface BridgeDataListener {

    public void onConnect() throws Exception;

    public void onRead(BridgeData data) throws Exception;

    public void onSend(BridgeData data) throws Exception;

    public void onDisconnect() throws Exception;

    public void onError(String message, Exception ex);
}
