package com.unitap.unitap.NFCBackend.Packetization;

/**
 * A Full CRC Generator
 * Created by Brandon Marino on 2015-11-26.
 */
public class CRC16 {
    /**
     * Generate a CRC from a byte array
     * @param input the byte array to generate the CRC on
     * @return the int of the CRC (CRC is in the two lowest bytes)
     */
    private static int generateCrc(final byte[] input) {
        /*Format: CRC-CCITT (0xFFFF)*/
        int crc = 0xFFFF;

        for (int j = 0; j < input.length ; j++) {
            crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
            crc ^= (input[j] & 0xff);//byte to int, trunc sign
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
     * @param input int byte array with the appended crc
     * @return if the crc in the first couple of bytes
     */
    protected static boolean verify(byte[] input){
        //now cut it down to remove the crc
        byte[] crcBytesOld = {input[0], input[1]};
        //now combine into an integer for easy comparison.
        int crcIntegerOld = combineBytes(crcBytesOld);
        //now cut the actual message out of the input and generate CRC from that message
        byte[] originalMessage = new byte[input.length-2];
        System.arraycopy(input, 2, originalMessage, 0, input.length-2);
        int crcIntegerNew = generateCrc(originalMessage);
        return crcIntegerOld == crcIntegerNew;
    }

    /**
     * Combine two bytes into one integer
     * @param bytes the two bytes that should be in the least significant bytes of the integer
     * @return the combined int
     */
    protected static int combineBytes(byte [] bytes){
        if (bytes.length <= 8){
            return bytes[1]<<8 &0xFF00 | bytes[0]&0xFF;
        }
        return -1;
    }

    /**
     * Take a message and add a crc of the message to the beginning
     * @param input the original message
     * @return the message of the appended crc
     */
    protected static byte[] appendCRCBytes(byte[] input){
        int crcInt = generateCrc(input); // check

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
     * Take a message that has a CRC attached to its head and extract out the message behind it
     * @param appendedMessage take out the crc header ang get the rest of the message
     * @return the original message without the crc
     */
    protected static String stripOutCRCHeader(byte[] appendedMessage){

        byte [] receivedMessage = new byte[appendedMessage.length-2];
        System.arraycopy(appendedMessage, 2, receivedMessage, 0, appendedMessage.length-2);
        return new String(receivedMessage);
    }

    /**
     * This will take one message (in byte array 1), and append a CRC generated from another (int byte array 2)
     * @param arrayToAppend the byte array to add the CRC to
     * @param arrayToCRC the byte array to generate the CRC from
     * @return the appended array
     */
    protected static byte[] appendCRCOnDifferentArray(byte[] arrayToAppend, byte[] arrayToCRC){
        int crcInt = generateCrc(arrayToCRC); // check

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
}
