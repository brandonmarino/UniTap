package com.unitap.unitap.CyclicRedundancyCheck;

/**
 * Created by Brandon Marino on 2015-11-26.
 */
public class CRC16 {
    private static int generate_crc(final byte[] buffer) {
        /*Format: CRC-CCITT (0xFFFF)*/
        int crc = 0xFFFF;

        for (int j = 0; j < buffer.length ; j++) {
            crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
            crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }

    /**
     * This function will take a full byte array, compare it's original Two bytes against the rest of the message
     * The entirety of the rest of the packet will be error checked. Nothing should have been changed
     * @param input
     * @return
     */
    public static boolean verify(byte[] input){
        String originalMessage = new String(input);
        //now cut it down to remove the crc
        byte[] crcBytesOld = originalMessage.substring(0,2).getBytes();
        //now combine into an integer for easy comparison.
        int crcIntegerOld = combineBytes(crcBytesOld);
        //now cut the actual message out of the input and generate CRC from that message
        int crcIntegerNew = generate_crc(originalMessage.substring(2).getBytes());
        return crcIntegerOld == crcIntegerNew;
    }

    public static int combineBytes(byte [] bytes){
        if (bytes.length <= 8){
            return bytes[1]<<8 &0xFF00 | bytes[0]&0xFF;
        }

        return -1;
    }

    /**
     * Take a message and add a crc of the message to the beginning
     * @param input
     * @return
     */
    public static byte[] appendCRCBytes(byte[] input){
        int crcInt = generate_crc(input); // check

        //mask out the two important bytes
        byte[] crcBytes = new byte[2];
        crcBytes[0] = (byte) (crcInt & 0x000000ff);
        crcBytes[1] = (byte) ((crcInt & 0x0000ff00) >>> 8);

        //now merge those bytes with the original message
        byte [] output = new byte[crcBytes.length+input.length];
        System.arraycopy(crcBytes, 0, output, 0, crcBytes.length);
        System.arraycopy(input, 0, output, crcBytes.length, input.length);

        //return the concatenated message
        return output;
    }

    /**
     * This will take one message (in byte array 1), and append a CRC generated from another (int byte array 2)
     * @param arrayToAppend the byte array to add the CRC to
     * @param arrayToCRC the byte array to generate the CRC from
     * @return the appended array
     */
    public static byte[] appendCRCOnDifferentArray(byte[] arrayToAppend, byte[] arrayToCRC){
        int crcInt = generate_crc(arrayToCRC); // check

        //mask out the two important bytes
        byte[] crcBytes = new byte[2];
        crcBytes[0] = (byte) (crcInt & 0x000000ff);
        crcBytes[1] = (byte) ((crcInt & 0x0000ff00) >>> 8);

        //now merge those bytes with the original message
        byte [] output = new byte[crcBytes.length+arrayToAppend.length];
        System.arraycopy(crcBytes, 0, output, 0, crcBytes.length);
        System.arraycopy(arrayToAppend, 0, output, crcBytes.length, arrayToAppend.length);

        //return the concatenated message
        return output;
    }

    /**
     * Take a message that has a CRC attached to its head and extract out the message behind it
     * @param appendedMessage
     * @return
     */
    public static String stripOutCRCHeader(byte[] appendedMessage){

        byte [] receivedMessage = new byte[appendedMessage.length-2];
        System.arraycopy(appendedMessage, 2, receivedMessage, 0, appendedMessage.length-2);
        return new String(receivedMessage);
    }
}
