/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge;

import lmo.tcp.bridge.conf.ConfigName;

/**
 *
 * @author LMO-PC
 */
public class BridgeServerConfig {

    @ConfigName(required = true)
    public static int port;

}
