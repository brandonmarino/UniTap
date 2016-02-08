package com.unitap.unitap.NFCBackend.HCE;

import com.unitap.unitap.CyclicRedundancyCheck.CRC16;

import java.nio.ByteBuffer;

/**
 * This Class will add all encapsulating data to an initial message. That message must be below
 *
 * Packet Dimensions
 *
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-number | 2 byte phone-id CRC | <=26 byte message user's id
 * Packet types: (0=Generic ,1=Acknowledgement ,2=Error)
 *
 * Created by Brandon Marino on 2/7/2016.
 */
public class Encapsulation {
    final static int HCE_MAX_MESSAGE = 31;

    /**
     * This function will add all of the inportant info to the message. If it is within length boundries, it will return the encapsulated string. If outside, it will return null;
     * @param message the actual message
     * @param type the type of message ie(0=general, 1=ack, 3=error)
     * @param messageNumber the number of this message in the transfer
     * @param phone_id the 32 byte hash generated from the phone's uuid
     * @return if message properly enncapsulated, return it. if not, return null
     */
    public static byte[] encapsulate(byte[] message, int type, int messageNumber, byte[] phone_id){
        //we need to fully encapsulate this message with all of the packet additions that are shown in the file header
        //step 1: first we add the phone-id CRC
        message = addPhoneIdentifier(message, phone_id);
        //step 2: now the packet number
        if (message != null)
            message = addPacketNumber(message, messageNumber);
        //step 3: now the type
        if (message != null)
            message = addType(message, type);
        //step 4: finally the CRC
        if (message != null)
            message = addCRC(message);
        //step 5: return that
        return message;

    }
    /**
     * Will append a CRC to a message within the proper length requirements. Added to the front.
     * @param message
     * @return
     */
    private static byte[] addCRC(byte[] message){
        if (message.length <= (HCE_MAX_MESSAGE-2))
            return CRC16.appendCRCBytes(message);
        else return null;
    }

    /**
     * This will add the packet's type for encapsulation.
     * 0x00 = General, 0x01 = Acknowledgement, 0x02 = Error
     * @param message
     * @param type
     * @return
     */
    private static byte[] addType(byte[] message, int type){
        if (type < 3) {
            if (message.length <= (HCE_MAX_MESSAGE - 3)) {
                return appendHeadIntValue(message, type);
            }
        }
        return null;
    }

    /**
     * This will send the transfer number of the packet. Transfer numbers only increase with the receipt of a general packet on each side.
     * Not extreamly useful now, but it could hurt us in the future to not have it.
     * 1 byte is worth it for future proof purposes.
     * @param message the message that needs to be appended
     * @param num the number of this packet transfer
     */
    private static byte[] addPacketNumber(byte[] message, int num){
        if (message.length <=(HCE_MAX_MESSAGE - 4)) {
            return appendHeadIntValue(message, num);
        }
        return null;
    }

    /**
     * Will add a 2 byte CRC of the Phone's ID
     * @param message the original message
     * @param phone_id a 32 byte phone id
     * @return an appended message
     */
    private static byte[] addPhoneIdentifier(byte[] message, byte[] phone_id){
        if (message.length <=(HCE_MAX_MESSAGE - 6)){
            return CRC16.appendCRCOnDifferentArray(message,phone_id);
        }
        return null;
    }

    /**
     * Add the byte equivalent of an integer value (under 256 bytes of course to the beginning of a message)
     * @param message the message to append
     * @param value the value to append it with
     * @return the appended message
     */
    private static byte[] appendHeadIntValue(byte[] message, int value){
        if (value < 256) {
            //strip out the important byte from the int object
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(value);
            byte[] result = b.array();
            byte byteType = result[3]; //this is an array because we are going to concatenate this and the message
            //now copy that type value (as a byte to save space) to the packet
            byte[] output = new byte[message.length + 1];
            System.arraycopy(message, 0, output, 1, message.length);
            output[0] = byteType;
            return output;
        }
        return null;
    }

}
