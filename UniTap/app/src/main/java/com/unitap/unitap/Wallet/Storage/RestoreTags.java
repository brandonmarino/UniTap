package com.unitap.unitap.Wallet.Storage;

import com.unitap.unitap.Wallet.VirtualWallet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This will take a XML block and read it into a card
 * Created by Brandon Marino on 10/1/2015.
 */
public class RestoreTags {
    public static VirtualWallet restoreTags(File tagCache){

        //Right now this is just doing a serialization.  In the future, this will be swapped out for XML for encryption purposes

        VirtualWallet newWallet = null;
        try {
            ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tagCache)));
            try {
                Object o = objectStream.readObject();
                if (o instanceof VirtualWallet)
                    newWallet = (VirtualWallet) o;
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //close streams
            if (objectStream != null)
                objectStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return newWallet;
    }
}
