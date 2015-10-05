package com.unitap.unitap.Exceptions;

/**
 * Created by Brandon Marino on 10/4/2015.
 */
public abstract class ProjectExceptions extends Exception {
    private String message; //message delivered to the user about this specific tag exception

    public ProjectExceptions(String message){
        this.message = message;
    }
    @Override
    public String getMessage(){
        return message;
    }
}
