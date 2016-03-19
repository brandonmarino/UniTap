import Packetization.DeEncapsulation;

/**
 * Created by Brandon Marino on 3/19/2016.
 */
public class UniTapBroker {
    private byte[] apdu;
    //0                    2             3                      4                     6                   9
    //| 2-byte Message CRC | 1 byte Type | 1 byte packet-length | 2 byte phone-id CRC | 3-byte Company id |<=23 byte message user's id
    //Packet types: (0=Generic, 1=Acknowledgement, 2=Error)
    //
    protected static boolean requestBackEndServer(byte[] apdu){
        int phoneCrc = DeEncapsulation.getPhoneCrc(apdu);
        byte [] message= DeEncapsulation.getMessage(apdu);
        String userId = new String(message);



        return true;
    }

}
