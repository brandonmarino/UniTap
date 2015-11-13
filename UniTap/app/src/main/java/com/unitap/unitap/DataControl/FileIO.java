package com.unitap.unitap.DataControl;

import java.io.*;
import com.unitap.unitap.Exceptions.InheritedExceptions.IoXmlException;

/**This abstract class will handle all direct FileIO
 * Created by Brandon Marino on 10/2/2015.
 */
public abstract class FileIO {

    /**
     * Read from a file
     * @param file Some file
     * @return a string
     */
    public static String readFromFile(File file) throws IoXmlException{
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
            else
                return null;
        }catch(IOException e){
            throw new IoXmlException("Cannot read from this file");
        }
    }

    /**
     * Write to a file
     * @param file some file which you wish to store
     * @param string some string you with to store
     */
    public static boolean saveToFile(File file, String string) throws IoXmlException{
        try {
            if (!file.exists())
                file.createNewFile();
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            System.out.print(string);
            outputStream.write(string.getBytes());
            //close streams
            outputStream.close();
            return true;
        }catch(IOException e){
            throw new IoXmlException("Cannot save string to file");
        }
    }
}
