package com.unitap.unitap.NFCBackend.Packetization;

/**
 * This will decompile a packet (From the terminal) into it's included information.
 * This is the packet design from the terminal to the phone
 * 0                     2            3                      4
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-length | <=27 byte message user's id
 *
 * Packet types: (0=Generic ,1=Acknowledgement ,2=Error)
 *
 */
public class DeEncapsulation {
    final static int HCE_MAX_MESSAGE = 31;

    public static boolean verifyCRC(byte[] message){
        return CRC16.verify(message);
    }

    public static Integer getType(byte[] message){
        byte[] typeByte = getSubArray(message, 2, 3);
        return CRC16.combineBytes(typeByte);
    }

    public static Integer getPacketNumber(byte[] message){
        byte[] numByte = getSubArray(message, 3, 4);
        return CRC16.combineBytes(numByte);
    }

    public static byte[] getMessage(byte[] message){
        byte [] cutMessage  = getSubArray(message, 4, message.length);
        return cutMessage;
    }

    private static byte[] getSubArray(byte[] message, int start, int end){
        byte[] output = new byte[end-start];
        System.arraycopy(message, start, output, 0, end-start);
        return output;
    }
}
