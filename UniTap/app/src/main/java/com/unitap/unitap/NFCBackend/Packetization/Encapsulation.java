package com.unitap.unitap.NFCBackend.HCE.Packetization;

import java.nio.ByteBuffer;

/**
 * This Class will add all encapsulating data to an initial message. That message must be below
 *
 * Packet Dimensions
 *
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-number | 2 byte phone-id CRC | 3-byte Company id |<=23 byte message user's id
 * Packet types: (0=Generic ,1=Acknowledgement ,2=Error)
 *
 * We decided on headers because we don't know what information/what length the company would want to send. So we are giving them as much room at the back as possible.
 *
 * Its efficiency is about O(5n) where n is the length of the byte message (up to 26 bytes)
 * Created by Brandon Marino on 2/7/2016.
 */
public class Encapsulation {
    final static int HCE_MAX_MESSAGE = 31;

    /**
     * This function will add all of the important info to the message. If it is within length boundries, it will return the encapsulated string. If outside, it will return null;
     * @param message the actual message
     * @param type the type of message ie(0=general, 1=ack, 3=error)
     * @param messageNumber the number of this message in the transfer
     * @param phoneId the 32 byte hash generated from the phone's uuid
     * @return if message properly enncapsulated, return it. if not, return null
     */
    public static byte[] encapsulate(byte[] message, int type, int messageNumber, byte[] phoneId, int companyId){
        //we need to fully encapsulate this message with all of the packet additions that are shown in the file header
        //step 1: fist add the 3-byte company identifier
        if(message != null)
            message = addCompanyId(message, companyId);
        //step 1: first we add the 2-byte phone-id CRC
        if (message!=null)
            message = addPhoneId(message, phoneId);
        //step 2: now the 1-byte packet number
        if (message != null)
            message = addPacketNumber(message, messageNumber);
        //step 3: now the 1-byte type
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
                return appendHeadIntValue(message, type, 1);
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
            return appendHeadIntValue(message, num, 1);
        }
        return null;
    }

    /**
     * Will add a 2 byte CRC of the Phone's Id
     * @param message the original message
     * @param phoneId a 32 byte phone id
     * @return an appended message
     */
    private static byte[] addPhoneId(byte[] message, byte[] phoneId){
        if (message.length <=(HCE_MAX_MESSAGE - 6)){
            return CRC16.appendCRCOnDifferentArray(message, phoneId);
        }
        return null;
    }

    /**
     * This will append a 3-byte company id to the packet.
     * 3-bytes was decided for the length because it allows for 2^24 or 16,777,216 different company IDs
     * 2-bytes wasn't way so small at 2^16 == 65536. But why set ourselves up for an issue later?
     * That left-most byte can always be used for something later anyway.
     * @param companyId Some 3-byte id of a company. These will be assigned serialy first come, first serve. Will map to their database key.
     * @return A byte array with the company id appended to the front of it.
     */
    private static byte[] addCompanyId(byte[] message, int companyId){
        if (message.length <=(HCE_MAX_MESSAGE-7)){
            return appendHeadIntValue(message, companyId, 3);
        }
        return null;
    }

    /**
     * Add the byte equivalent of an integer value (under 256 bytes of course to the beginning of a message)
     * @param message the message to append
     * @param value the value to append it with
     * @param significantDigits The number of bytes in int that are important
     * @return the appended message
     */
    private static byte[] appendHeadIntValue(byte[] message, int value, int significantDigits){
        if (value < 256) {
            if (significantDigits <= 8) {
                //strip out the important byte from the int object
                ByteBuffer b = ByteBuffer.allocate(4);
                b.putInt(value);
                byte[] intInBytes = b.array();
                byte[] output = new byte[message.length + significantDigits];
                //append those bytes to the output of this function
                for (int i = 0; i<significantDigits;i++){
                    output[i] = intInBytes[(intInBytes.length-significantDigits)+i];
                }
                //System.arraycopy(intInBytes, 1, output, 0, significantDigits);
                //append the original message to the output of this message
                System.arraycopy(message, 0, output, significantDigits, message.length);
                return output;
            }
        }
        return null;
    }



}