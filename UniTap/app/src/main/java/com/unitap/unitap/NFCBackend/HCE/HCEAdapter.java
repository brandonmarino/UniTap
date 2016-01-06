package com.unitap.unitap.NFCBackend.HCE;

import android.app.Activity;
import android.content.Intent;

/**Our activity will use this Adapter in order to send data to the other device.  From the external system's view, this will function will simply pass messages and notify the activity when a new message is received
 * Created by Brandon Marino on 1/6/2016.
 */
public class HCEAdapter {
    private Activity activity;
    public HCEAdapter (Activity activity) {
        this.activity = activity;

    }
    public void sendMessage(String message){
        Intent intent = new Intent("unitap.action.NOTIFY_HCE_DATA");
        intent.putExtra("hcedata", message);
        activity.sendBroadcast(intent);
    }
}
