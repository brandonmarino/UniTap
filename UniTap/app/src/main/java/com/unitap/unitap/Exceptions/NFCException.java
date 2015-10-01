package com.unitap.unitap.Exceptions;

/**
 * Created by Brandon Marino on 9/22/2015.
 */
public class NFCException extends Exception {

    private String message; //message delivered to the user about this specific tag exception

    public NFCException(String message){
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
