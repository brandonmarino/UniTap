package com.unitap.unitap.Wallet;

import android.media.Image;
import org.simpleframework.xml.Default;

import java.util.Comparator;
import java.util.Date;
import com.unitap.unitap.Wallet.Tag;
/**
 * This is the physical representation of the Tag itself.  These will need to be stored in some XML document on closure of the application.
 * Created by Brandon Marino on 9/22/2015.
 */
@Default
public class Tag{
    private String name;
    //private Image picture = getDrawableResource(image);
    private Date addedDate;
    private String payload;
    private String tagID;

    public Tag() {
        this.name = "Generic";
        this.payload = "THIS IS A MESSAGE TO BE SEND BY NFC";
    }

    /**
     * Creating the tag, we have all available info
     *
     * @param name    the name of the tag
     *                param picture the picture of the tag
     *                param id the id of the vendor/card type
     * @param payload the specific payload which needs to be dumped to the NDEF device
     */
    public Tag(String name, /*Image picture, */ String payload) {
        //super(context, R.layout.content_wallet);
        /**
         * Check the format of the tag.  Names and images can be fixed, however vendor id and payloads cannot.
         */
        //if (payload == null || payload.isValid())
        //    throw new IoXmlException("payload is either empty or corrupt");
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
     * @return the payload being represented here
     */
    public String getPayload() {
        return payload;
    }
    /**
     * The picture which will appear in the cardview for the tag. This will be used to make it look cool.
     * @return The image

    public Image getPicture() {
    return picture;
    }*/
    public Date getAddedDate(){
        return addedDate;
    }
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

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    /************************************************************
     *              Comparators for Sorting
     ************************************************************/
    static class TagSortByInstitutionName implements Comparator<Tag> {
        public int compare(Tag tag1, Tag tag2){
            String institutionName1 = tag1.getName().toUpperCase();
            String institutionName2 = tag2.getName().toUpperCase();
            return institutionName1.compareTo(institutionName2);
        }
    }
/*
    static class TagSortByInstitutionId implements Comparator<Tag>{
        public int compare(Tag tag1, Tag tag2){
            Integer id1 = tag1.getInstitutionId();
            Integer id2 = tag2.getInstitutionId();
            return id1.compareTo(id2);
        }
    }
*/
    static class TagSortByDate implements Comparator<Tag>{
        public int compare(Tag tag1, Tag tag2){
            Date date1 = tag1.getAddedDate();
            Date date2 = tag2.getAddedDate();
            return date1.compareTo(date2);
        }
    }
}