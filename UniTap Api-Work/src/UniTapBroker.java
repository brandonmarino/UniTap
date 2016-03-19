import Packetization.DeEncapsulation;

/**
 * Created by Brandon Marino on 3/19/2016.
 */
public class UniTapBroker {
    private byte[] apdu;

    protected boolean requestBackEndServer(byte[] apdu){
        int phoneCrc = DeEncapsulation.getPhoneCrc(apdu);
        byte [] message= DeEncapsulation.getMessage(apdu);
        String userId = new String(message);



        return true;
    }

    final public static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static char[] bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return hexChars;
    }

    public static void main(String[] args){
        ApiAdapter adapter = new ApiAdapter();
        byte[] response = adapter.requestAccess("Hello",3);
        char[] printResponse = bytesToHex(response);
        System.out.print("Message to Terminal: ");
        for (int i = 0; i < printResponse.length; i=i+2){
            System.out.print("0x"+printResponse[i] + printResponse[i+1]  +",");
        }
        System.out.print("\n");
                //System.out.println("Message to Terminal: " + printResponse);
    }
}
