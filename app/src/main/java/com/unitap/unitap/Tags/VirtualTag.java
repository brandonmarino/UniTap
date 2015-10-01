package com.unitap.unitap.Tags;

import android.media.Image;
import com.unitap.unitap.Exceptions.*;
import com.unitap.unitap.NFCBackend.*;

import java.util.Date;

/** This is the physical representation of the Tag itself.  These will need to be stored in some XML document on closure of the application.
 * Created by Brandon Marino on 9/22/2015.
 */
public class VirtualTag {
    //fields
    private String name;
    private Image picture;
    private Date addedDate;
    private Integer id;
    private Payload payload;

    /**Purely for testing purposes*/
    private VirtualTag(){}

    /**
     * Creating the tag, we have all available info
     * @param name the name of the tag
     * @param picture the picture of the tag
     * @param id the id of the vendor/card type
     * @param payload the specific payload which needs to be dumped to the NDEF device
     * @throws VirtualTagFormatException  If any info is incomplete which cannot be incomplete, this will be thrown.
     */
    public VirtualTag(String name, Image picture, Date addedDate, Integer id, Payload payload) throws VirtualTagFormatException {
        /**
         * Check the format of the tag.  Names and images can be fixed, however vendor id and payloads cannot.
         */
        if (id <= 0 || this.id == null)
            throw new VirtualTagFormatException("ID is not set or is corrupt");
        if (payload == null || payload.isEmpty())
            throw new VirtualTagFormatException("Payload is either empty or corrupt");
        this.name = name;
        this.picture = picture;
        this.addedDate = addedDate;
        this.id = id;
        this.payload = payload;
    }

    /**
     * This probably shouldn't be used irl, but I can see testing merits
     * @param id
     * @param payload
     */
    private VirtualTag(Integer id, Payload payload){
        this.id = id;
        this.payload = payload;
    }

    /******************************************************
     *                         Getters
     *****************************************************/

    /**
     *  Get the name of the card, which was either definied by the user or the company
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the payload which contains the byte map of the tag which this is representing
     * Internal structure is to be defined in the Payload class
     * @return the payload being represented here
     */
    public Payload getPayload() {
        return payload;
    }

    /** Get the external id of the card.  This will be used to sort the card's intended vendor
     * @return the vendor id of the card
     */
    public Integer getId() {
        return id;
    }

    /**
     * The picture which will appear in the cardview for the tag. This will be used to make it look cool.
     * @return The image
     */
    public Image getPicture() {
        return picture;
    }

    /******************************************************
     *                         Setters
     *****************************************************/
    /** Set the picture which will make the carview look cool
     * @param picture the picture for the cardview
     */
    public void setPicture(Image picture) {
        this.picture = picture;
    }

    /** The name of the tag in the list.  To be added at the card's birth.  This can be edited at any moment by a user.
     * @param name - this is the specific tag's name.  Can be set whenever
     */
    public void setName(String name) {
        this.name = name;
    }
}
