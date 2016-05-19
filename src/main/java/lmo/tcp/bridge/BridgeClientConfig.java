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
public class BridgeClientConfig {

    //shost sport srcid srcpass dstid dstpass rhost rport lport
    @ConfigName(required = true)
    public static int port;

    @ConfigName(required = true)
    public static String shost;
    @ConfigName(required = true)
    public static int sport;
    @ConfigName(required = true)
    public static int srcid;
    public static String srcpass;

    @ConfigName(required = true)
    public static int dstid;
    public static String dstpass;
    @ConfigName(required = true)
    public static String rhost;
    @ConfigName(required = true)
    public static int rport;
}
