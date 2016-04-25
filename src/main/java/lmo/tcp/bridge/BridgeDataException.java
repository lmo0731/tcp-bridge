/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge;

/**
 *
 * @author LMO
 */
public class BridgeDataException extends Exception {

    public BridgeDataException(String message) {
        super(message);
    }

    public BridgeDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
