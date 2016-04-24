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
public interface BridgeClientListener {

    public void onError(String msg, Exception ex);

    public void onStart();

    public void onEnd();

}
