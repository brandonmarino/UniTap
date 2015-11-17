package com.unitap.unitap.NFCBackend.HCE;

import android.app.AlertDialog;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class MyHostApduService extends HostApduService {

    private int messageCounter = 0;
    private String newMessage = "";

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (!newMessage.isEmpty()) {
            Log.i("HCEDEMO", "SendingMessage");
            return newMessage.getBytes();
        }
        if (selectAidApdu(apdu)) {
            Log.i("HCEDEMO", "Application selected");
            //sendResponseApdu("Hello Terminal!".getBytes());
            return "Hello Terminal!".getBytes();
        }
        else {
            Log.i("HCEDEMO", "Received: " + new String(apdu));
            return getNextMessage();
        }
    }

    private byte[] getNextMessage() {
        return ("Message from android: " + messageCounter++).getBytes();
    }

    private boolean selectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    @Override
    public void onDeactivated(int reason) {
        Log.i("HCEDEMO", "Deactivated: " + reason);
    }

    /**
     * This will just make it easier to send an alert to the user when need be
     * @param message some message to display
     */
    protected void dialogMessage(String title, String message){
        AlertDialog dialogMessage = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        dialogMessage.setTitle(title);
        dialogMessage.setMessage(message);
        dialogMessage.show();
    }
    public void setMessage(String message){
        newMessage = message;
    }
}