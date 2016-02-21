package com.unitap.unitap.DataControl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;
import com.unitap.unitap.Exceptions.InheritedExceptions.IoXmlException;
import com.unitap.unitap.Wallet.Tag;

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
     * Read an Image from a file
     * @param fileName Some file
     */
    public static Bitmap readImageFromFile(String fileName, Activity context){
        Log.v("Attempting the read",fileName);
        Bitmap bitmap = null;
        try {
            File filePath = context.getFileStreamPath(fileName);
            if (filePath.exists()) {
                FileInputStream fi = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(fi);
                Log.v("Succeeded reading file", "Byte Array: ");
            }else
                filePath.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Write an Image to a file
     * @param fileName some file which you wish to store this image in
     * @param bitmap some string you with to store
     */
    public static boolean saveImageToFile(String fileName, Bitmap bitmap, Activity context) {
        try {
            Log.v("Attempting the write", fileName);
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.v("Succeeded writing file", fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            throw new IoXmlException("Cannot save string to file");
        }
    }

    public static boolean renameImage(String fileOldName, String fileNewName){
        File fileOld = new File(fileOldName);
        File fileNew = new File(fileNewName);
        if(fileNew.exists())
            return false;
        return  fileOld.renameTo(fileNew);
    }
}
