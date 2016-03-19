import Packetization.Encapsulation;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.DataInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class ApiAdapter {

    private String message = "";
    private HttpURLConnection conn;

    public byte[] requestAccess(String phonehash, int crc) {
        try {
            String urlParameters = "cardhash=" +phonehash+ "&phonecrc=" +crc;
            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            String request = "http://localhost:3000/api/company_cards";

            URL url = new URL(request);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty( "charset", "utf-8");

            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );

            OutputStream os = conn.getOutputStream();
            os.write(postData);

            InputStream is = conn.getInputStream();
            message = convertStreamToString(is);


            if (conn.getResponseCode() == 200) {
                System.out.println("Request Granted! "+ message);
                byte[] response = message.getBytes();
                conn.disconnect();
                System.out.println("Message Size: " + response.length);
                return Encapsulation.encapsulate(response,0,true);
            }else{
                System.out.println("Request Denied! " + message);
                byte[] response = message.getBytes();
                conn.disconnect();
                System.out.println("Message Size: " + response.length);
                return Encapsulation.encapsulate(response,0,false);
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            System.out.println("Request Denied! " + message);
            byte[] response = message.getBytes();
            conn.disconnect();
            System.out.println("Message Size: " + response.length);
            return Encapsulation.encapsulate(response,0,false);

        }

        return Encapsulation.encapsulate("Server Error".getBytes(), 0, false);
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
