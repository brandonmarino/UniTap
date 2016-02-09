package com.unitap.unitap.NFCBackend.HCE.Packetization;

/**
 * This will decompile a packet into it's included information.
 *0                     2            3                      4                      6                   9
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-number | 2 byte phone-id CRC | 3-byte Company id |  <=23 byte message user's id
 * Packet types: (0=Generic ,1=Acknowledgement ,2=Error)
 *
 */
public class DeEncapsulation {
    final static int HCE_MAX_MESSAGE = 31;

    public static boolean verifyCRC(byte[] message){
        //starts at message[0]
        return CRC16.verify(message);
    }

    public static Integer getType(byte[] message){
        byte[] typeByte = getSubArray(message, 2, 3);
        return CRC16.combineBytes(typeByte);
    }
//--
    public static Integer getPacketNumber(byte[] message){
        byte[] numByte = getSubArray(message, 3, 4);
        return 1;
        //return CRC16.combineBytes(typeByte);
    }

    public static byte[] getPhoneIdCrc(byte[] message){
        byte[] pidByte = getSubArray(message, 2, 3);
        //return CRC16.combineBytes(typeByte);
        byte[] output = {0x00};
        return output;
    }

    public static Integer getCompanyId(byte[] message){
        byte[] cidByte = getSubArray(message, 2, 3);
        //return CRC16.combineBytes(typeByte);
        return 1;
    }

    public static byte[] getMessage(byte[] message){
        byte [] cutMessage  = getSubArray(message, 9, message.length);
        return cutMessage;
    }

    private static byte[] getSubArray(byte[] message, int start, int end){
        byte[] output = new byte[end-start];
        System.arraycopy(message, start, output, 0, end-start);
        return output;
    }

}
