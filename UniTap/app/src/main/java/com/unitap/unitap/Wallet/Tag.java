package com.unitap.unitap.Wallet;


import android.media.Image;

import com.unitap.unitap.Exceptions.InheritedExceptions.VirtualTagFormatException;
import com.unitap.unitap.Wallet.Payload;

import org.simpleframework.xml.Default;
import java.util.Date;

/**
 * This is the physical representation of the Tag itself.  These will need to be stored in some XML document on closure of the application.
 * Created by Brandon Marino on 9/22/2015.
 */
@Default
public class Tag {
    private String name;
    //private Image picture;
    private Date addedDate;
    private Payload payload;

    public Tag() {
        this("Generic", new Payload());
    }

    /**
     * Creating the tag, we have all available info
     *
     * @param name    the name of the tag
     *                param picture the picture of the tag
     *                param id the id of the vendor/card type
     * @param payload the specific payload which needs to be dumped to the NDEF device
     */
    public Tag(String name, /*Image picture, */ Payload payload) {
        /**
         * Check the format of the tag.  Names and images can be fixed, however vendor id and payloads cannot.
         */
        //if (payload == null || payload.isValid())
        //    throw new VirtualTagFormatException("Wallet.Payload is either empty or corrupt");
        this.name = name;
        //this.picture = new Image();
        this.addedDate = new Date();
        this.payload = payload;
    }

    /******************************************************
     *                         Getters
     *****************************************************/

    /**
     * Get the name of the card, which was either definied by the user or the company
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the payload which contains the byte map of the tag which this is representing
     * Internal structure is to be defined in the Wallet.Payload class
     * @return the payload being represented here
     */
    public Payload getPayload() {
        return payload;
    }
    /**
     * The picture which will appear in the cardview for the tag. This will be used to make it look cool.
     * @return The image

    public Image getPicture() {
    return picture;
    }*/

    /******************************************************
     *                         Setters
     *****************************************************/

    /**
     * The name of the tag in the list.  To be added at the card's birth.  This can be edited at any moment by a user.
     * @param name - this is the specific tag's name.  Can be set whenever
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Set the picture which will make the carview look cool
     * @param picture the picture for the cardview*/
    /*public void setPicture(Image picture) {
        this.picture = picture;
    */

    /**
     * Date that this card was added to the system
     * @param addedDate some date on which this card was created
     */
    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
}