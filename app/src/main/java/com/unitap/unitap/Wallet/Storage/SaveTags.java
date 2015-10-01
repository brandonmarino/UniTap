package com.unitap.unitap.Wallet.Storage;

import com.unitap.unitap.Wallet.VirtualWallet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * This Class will take a VitualTag object, chop it up into XML, then save that XML to a file which is stored in the app's directory
 * Created by Brandon Marino on 10/1/2015.
 */
public class SaveTags {

    public static void saveTags(File tagCache,VirtualWallet wallet){

        //Right now this is just doing a serialization.  In the future, this will be swapped out for XML for encryption purposes

        BufferedOutputStream outputStream = null;

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(tagCache));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        try {
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(wallet);
            //close streams
            if (objectStream != null)
                objectStream.close();
            if (outputStream != null)
                outputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
