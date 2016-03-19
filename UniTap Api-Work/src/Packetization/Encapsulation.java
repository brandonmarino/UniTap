package Packetization;

import java.nio.ByteBuffer;

/**
 * This Class will add all encapsulating data to an initial message. That message must be below
 *
 * Packet Dimensions
 *
 * This is the packet design for the message from the phone to the terminal
 *
 * 0                     2             3                      4                            5
 * | 2-byte Message CRC | 1 byte Type  | 1 byte packet-length | 1 byte accept(1)/reject(0) |  <=26 byte message user's id
 *
 * Its efficiency is about O(5n) where n is the length of the byte message (up to 26 bytes)
 * Created by Brandon Marino on 2/7/2016.
 */
public class Encapsulation {
    final static int HCE_MAX_MESSAGE = 31;
    final static int OVERHEAD = 9;
    /**
     * This function will add all of the important info to the message. If it is within length boundaries, it will return the encapsulated string. If outside, it will return null;
     * @param message the actual message
     * @param type the type of message ie(0=general, 1=ack, 3=error)
     * @param accepted if the server granted access or not
     * @return if message properly enncapsulated, return it. if not, return null
     */
    public static byte[] encapsulate(byte[] message, int type, boolean accepted){
        //we need to fully encapsulate this message with all of the packet additions that are shown in the file header
        //step 1: fist add the 3-byte company identifier
        int initialLength = message.length;
        if(message != null)
            message = addAccept(message, accepted);
        //step 1: first we add the 2-byte phone-id CRC
        if (message != null)
            message = addPacketLength(message, initialLength + OVERHEAD);
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
    private static byte[] addPacketLength(byte[] message, int num){
        if (message.length <=(HCE_MAX_MESSAGE - 4)) {
            return appendHeadIntValue(message, num, 1);
        }
        return null;
    }

    /**
     * Add the bit that indicates if the user has been accepted by the server
     * @param message the message to be passed alongside the acceptance
     * @param accepted if the user has been accepted or not
     * @return the appended message
     */
    private static byte[] addAccept(byte[] message, boolean accepted){
        if (message.length <=(HCE_MAX_MESSAGE - 5)) {
            if(accepted)
                return appendHeadIntValue(message, 1, 1);
            else
                return appendHeadIntValue(message, 0, 1);
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

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}