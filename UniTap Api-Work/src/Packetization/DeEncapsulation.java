package Packetization;

/**
 * This will decompile a packet (From the terminal) into it's included information.
 * This is the packet design from the terminal to the phone
 *
 * 0                    2             3                      4                     6                   9
 * | 2-byte Message CRC | 1 byte Type | 1 byte packet-length | 2 byte phone-id CRC | 3-byte Company id |<=23 byte message user's id
 * Packet types: (0=Generic, 1=Acknowledgement, 2=Error)
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

    public static Integer getPacketLength(byte[] message){
        byte[] numByte = getSubArray(message, 3, 4);
        return CRC16.combineBytes(numByte);
    }

    public static Integer getPhoneCrc(byte[] message){
        byte[] crcBytes = getSubArray(message, 4, 6);
        return CRC16.combineBytes(crcBytes);
    }

    public static Integer getCompanyID(byte[] message){
        byte[] cidBytes = getSubArray(message, 6, 9);
        return CRC16.combineBytes(cidBytes);
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
