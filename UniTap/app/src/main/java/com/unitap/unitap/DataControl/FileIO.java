package com.unitap.unitap.DataControl;

import com.unitap.unitap.Wallet.Wallet;

import java.io.*;

/**This abstract class will handle all direct FileIO
 * Created by Brandon Marino on 10/2/2015.
 */
public abstract class FileIO {

    /**
     * Read from a file
     * @param file Some file
     * @return a string
     */
    public static String readFromFile(File file){
        try {
            if (file.exists()) {
                Reader reader = new FileReader(file);
                int data = reader.read();
                //char[] output = {};
                StringBuilder output = new StringBuilder();
                int i = 0;
                while (data != -1) {
                    output.append((char) data);
                    data = reader.read();
                    i++;
                }
                return output.toString();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Write to a file
     * @param file some file which you wish to store
     * @param string some string you with to store
     */
    public static void saveToFile(File file, String string){
        try {
            if (!file.exists())
                file.createNewFile();
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            System.out.print(string);
            outputStream.write(string.getBytes());
            //close streams
            outputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
