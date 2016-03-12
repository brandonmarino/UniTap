package com.unitap.unitap.NFCBackend.HCE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.unitap.unitap.NFCBackend.Packetization.DeEncapsulation;
import com.unitap.unitap.NFCBackend.Packetization.Encapsulation;

import java.util.Arrays;

public class UnitapApduService extends HostApduService {

    /***********************************************************************************************
     *                      Service State Functions (Create, Pause, Resume)
     ***********************************************************************************************/

    private byte[] lastMessage = null;
    private static byte[] nextMessage = null;
    private static byte[] originalMessage = null;
    private boolean switchedIntoActivityDuringTransaction = false;
    private int count = 0;

    /**
     * What happens when the service is first run using startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!HCEAdapter.isActive())
            switchedIntoActivityDuringTransaction = true;
        return START_NOT_STICKY;
    }

    /**
     * When the service is destroyed using stopService()
     */
    @Override
    public void onDestroy(){
        unregisterReceiver(hceNotificationsReceiver);   //remove the broadcast receiver
        count = 0;
        super.onDestroy();
    }

    /**
     * Service was deactivated, why?
     * @param reason some exist reason
     */
    @Override
    public void onDeactivated(int reason) {
        Log.i("ApduService", "Phone Disconnected");
    }

    /***********************************************************************************************
     *                      APDU Processing, HCE Messaging
     ***********************************************************************************************/

    private void recurringOperations(){
        registerBroadcastReceivers(); //register broadcasts receivers to handle communication
        if(nextMessage == null && lastMessage == null && originalMessage !=null && count == 0)
            nextMessage = originalMessage;
    }

    /**
     * Receives an APDU (if that APDU was registered to the same AID as the app/service)
     * @param apdu Tag's payload.  Contains a message, some header bytes and the UID
     * @param extras
     * @return some response that we want to give the terminal.  This could be replaced with an ack.
     */
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        recurringOperations();
        if (HCEAdapter.isActive() && !switchedIntoActivityDuringTransaction){
            boolean isEmpty = (apdu == null) || (apdu.length == 0); //check if empty message
            if(isJunk(apdu) || isEmpty) {
                //use this as a chance to send the next message
                if(nextMessage == null){
                    return "u.".getBytes();
                    //return junk back to keep transfer going
                } else {
                    //there is a message to return
                    if (lastMessage == null){
                        //lastMessage received its ack and was wiped
                        lastMessage = nextMessage;
                        nextMessage = null;
                        return lastMessage;
                    } else {
                        //return junk until that lastAck is received
                        return "u.".getBytes();
                    }
                }
            }
            return handleUniTapMessage(apdu);
        } else
            return handleRejection(apdu);
    }

    /************************************************************
     *              Create Protocol Responses
     * **********************************************************/

    /**
     * create an ack packet to a message from the terminal
     * @param apdu the original message form the server
     * @return an Ack apdu
     */
    private byte[] createAck(byte[] apdu){
        apdu[3] = 0x02;
        //this should be modified later
        return apdu;
    }

    /**
     * create an ack packet to a message from the terminal
     * @param apdu the original message from the server
     * @return an error apdu
     */
    private byte[] createError(byte[] apdu){
        apdu[3] = 0x03;
        //this should be modified later
        return apdu;
    }

    /***********************************************************
     *                UniTap Protocol Handlers
     ***********************************************************/
    /**
     * A message from a device has been received when the service was active, it passed the 'u.' and empty filter
     * @param apdu
     * @return
     */
    private byte[] handleUniTapMessage(byte[] apdu){
        //Log.v("Terminal Message", new String(apdu));
        Log.v("Message from Terminal", Arrays.toString(apdu));
        if (isSelectAidApdu(apdu)) {
            Log.i("Term-Message","Select Apdu");
            return handleUniTapSelectApdu(apdu);
        }
        else if (isGenericApdu(apdu)) {
            Log.i("Term-Message","Generic Apdu");

            return handleUniTapGeneric(apdu);
        }
        else if (isAckApdu(apdu)) {
            Log.i("Term-Message","Ack Apdu");
            return handleUniTapAck(apdu);
        }
        else if (isErrorApdu(apdu)) {
            Log.i("Term-Message","Error Apdu");
            return handleUniTapError(apdu);
        }
        return "u.".getBytes();
    }

    /**
     * Handle the rejection due to the application not being active
     * @param apdu the original message
     * @return the rejection response
     */
    private byte[] handleRejection(byte[] apdu){
        if (isSelectAidApdu(apdu))
            return "j.".getBytes();
        return "o.".getBytes();
    }

    /**
     * Handle the original select AID-APDU
     * For now, just generate an Ack for the terminal
     * @param apdu the aid apdu
     * @return the response to this aid-apdu
     */
    private byte[] handleUniTapSelectApdu(byte[] apdu){
        return createAck(apdu);
    }

    /**
     * Handle a generic UniTap message.
     * - Compare the CRC against the message
     * - Pass stripped down message up to the adapter
     * @param apdu
     * @return
     */
    private byte[] handleUniTapGeneric(byte[] apdu){
        if(DeEncapsulation.verifyCRC(apdu)) {
            count++;
            byte[] message = DeEncapsulation.getMessage(apdu);
            sendToBroadcastReceiver(new String(message));
            return createAck(apdu);
        }
        else
            return createError(apdu);
    }

    /**
     * Handle a UniTap Acknowledgment.
     *  - save the received status
     *  - respond with... junk... do not ack an ack
     * @param apdu the ack apdu
     * @return the response for this ack
     */
    private byte[] handleUniTapAck(byte[] apdu){
        lastMessage = null;
        count++;
        return "u.".getBytes();
    }

    /**
     *
     * @param apdu
     * @return
     */
    private byte[] handleUniTapError(byte[] apdu){
        return createError(apdu);
    }

    /**
     * Check if the apdu is a generic message. Deal with appropriately.
     * @param apdu some apdu-message from a device
     */

    /***************************************************
     *              Message Type Comparators
     ***************************************************/
    private boolean isGenericApdu(byte[] apdu){
        Log.v("Byte Array-Terminal",""+apdu[0]+", "+apdu[1]+", "+apdu[2]);
        if(apdu[2] == 0x00) {
            Log.v("HCE: Terminal Generic", "Packet-Number:- " + count);
            Log.v("Full Message", Arrays.toString(apdu));
            return true;
        }
        return false;
    }

    /**
     * Check if the apdu is an acknowledgement, ignore
     * @param apdu some apdu-message from a device
     */
    private boolean isAckApdu(byte[] apdu){
        if(apdu[2] == 0x01) {
            Log.v("HCE: Terminal Ack", "Packet-Number:- " + count);
            Log.v("Full Message", Arrays.toString(apdu));
            return true;
        }
        return false;
    }

    /**
     * Check if the apdu is an error and the lsat message needs to be resent
     * @param apdu some apdu-message from a device
     */
    private boolean isErrorApdu(byte[] apdu){
        if(apdu[2] == 0x02) {
            Log.v("HCE: Terminal Error", "Packet-Number:- " + count);
            Log.v("Full Message", Arrays.toString(apdu));
            return true;
        }
        return false;
    }

    /**check if this is the very first APDU from the broadcaster
     *
     * @param apdu payload
     * @return if this is a selectApdu
     */
    private boolean isSelectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    private boolean isJunk(byte[] apdu){
        return (apdu[0] == 0x75 && apdu[1] == 0x2e);
    }

    /***********************************************************************************************
     *               Receive Messages from another Activity which passed a defined Filter
     ***********************************************************************************************/

    public static void provisionTransfer(byte[] message, int companyID, byte[] phoneID){
        message = Encapsulation.encapsulate(message, 0, phoneID, companyID);
        nextMessage = message;
        originalMessage = message;
    }

    /**
     * Receive some data (Message) from an external Activity.  Used for sending direct messages
     */
    final BroadcastReceiver hceNotificationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] hcedata = intent.getByteArrayExtra("hcedata");
            nextMessage = hcedata;
        }
    };

    /**
     * Register a broadcast receiver for this Service
     * This is to receive messages from the application using this service
     */
    private void registerBroadcastReceivers() {
        final IntentFilter hceNotificationsFilter = new IntentFilter();
        hceNotificationsFilter.addAction("unitap.action.NOTIFY_HCE_DATA");
        registerReceiver(hceNotificationsReceiver, hceNotificationsFilter);
    }

    /**
     * Send a message out to the adapter/activity which is making use of the HCE peer messaging
     * @param message some message to be send up to the other activity
     */
    public void sendToBroadcastReceiver(String message){
        Intent intent = new Intent("unitap.action.NOTIFY_MAIN_DATA");
        intent.putExtra("hcedata", message);
        sendBroadcast(intent);
    }
}