package com.unitap.unitap.Wallet;

/**
 * Created by Brandon Marino on 9/22/2015.
 */
public class Payload implements java.io.Serializable{
    private int payload;
    public Payload () {
        payload = 0;
    }
    public int getPayload(){
        return payload;
    }
    public boolean isEmpty(){
        return true;
    }
}
