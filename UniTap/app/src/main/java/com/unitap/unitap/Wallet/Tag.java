package com.unitap.unitap.Wallet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;

import com.unitap.unitap.Activities.CardActivity;
import com.unitap.unitap.DataControl.FileIO;
import com.unitap.unitap.R;

import org.simpleframework.xml.Default;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * This is the physical representation of the Tag itself.  These will need to be stored in some XML document on closure of the application.
 * Created by Brandon Marino on 9/22/2015.
 */
@Default
public class Tag implements Serializable {
    private String name;
    private String imageFile;
    private Date addedDate;
    private byte[] payload;
    private String tagID;
    private String companyName;
    private int companyID;

    /**
     * Blank default constructor for SimpleXML marshaling
     */
    public Tag() {
        this.name = "John Doe";
        this.addedDate = new Date();
        this.payload = "THIS IS A MESSAGE".getBytes();
        //this.imageArray = new byte[0];
        this.imageFile = "FileName";
        this.tagID = "0";
        this.companyName = "Generic";
        this.companyID = 0;
    }

    /**
     * Creating the tag when we only have the name, and the payload
     *
     * @param name    the name of the tag
     *                param picture the picture of the tag
     *                param id the id of the vendor/card type
     * @param payload the specific payload which needs to be dumped to the NDEF device
     * @param wActivity reference to the activity, required for image manipulation
     */
    public Tag(String name, byte[] payload, Activity wActivity) {
        this(name, BitmapFactory.decodeResource(wActivity.getResources(), R.drawable.tagstand_logo_icon), payload, "0", "Generic Company", 0, wActivity);
    }

    /**
     * Creating the tag when we have all of the available info
     * @param name Name of the person
     * @param image The image from the company
     * @param payload The payload (identifier)
     * @param tagID The number of this tag in the wallet
     * @param companyName The name of the company
     * @param companyID The id of the company
     * @param wActivity reference to the activity, required for image manipulation
     */
    public Tag(String name, Bitmap image, byte[] payload, String tagID, String companyName, int companyID, Activity wActivity) {
        this.name = name;
        this.addedDate = new Date();
        setImage(image, wActivity);
        this.payload = payload;
        this.tagID = tagID;
        this.companyName = companyName;
        this.companyID = companyID;
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
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Get the date that this card was added to the wallet
     * @return the date that this tag was added to the wallet
     */
    public Date getAddedDate(){
        return addedDate;
    }

    /**
     * The name of the company that this tag is linked to.
     * @return the company name
     */
    public String getCompanyName(){
        return companyName;
    }

    /**
     * Get hte company's id
     * @return the company's id
     */
    public int getCompanyID(){
        return companyID;
    }

    /**
     * Get the tag's id, will be generated when the card is created
     * @return the tag's id
     */
    public String getTagID() {
        return tagID;
    }

    /**
     * The terminalID/companyID/etc.
     * @param companyID the companies id.
     */
    public void getCompanyID(int companyID){
        this.companyID = companyID;
    }

    /**
     * Return the bitmap of this card (must be read on the fly from storage)
     * @param wActivity a reference to the context for image manipulation
     * @return the Bitmap of the image
     */
    public Bitmap getImage(Activity wActivity) {
        return FileIO.readImageFromFile(imageFile, wActivity);
    }
    /******************************************************
     *                         Setters
     *****************************************************/

    /**
     * The name of the tag in the list.  To be added at the card's birth.  This can be edited at any moment by a user.
     * @param name this is the specific tag's name.  Can be set whenever
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Set the picture which will make the carview look cool
     * @param bitmap the picture for the cardview
     */
    public boolean setImage(Bitmap bitmap, Activity wActivity) {
        imageFile = addedDate.toString();
        return FileIO.saveImageToFile(imageFile, bitmap, wActivity);
    }

    /**
     * set the added date, the image name must also be set here.
     * @param date the date to change it to
     * @return if the date was changed. Will return false if this date already exists in the wallet
     */
    private boolean setAddedDate(Date date){
        //change filename + name of file, then change the date
        boolean success = FileIO.renameImage(imageFile, date.toString());
        if (success){
            imageFile = date.toString();
            addedDate = date;
        }
        return success;
    }

    /**
     * Set the tag's ID
     * @param tagID the id of the tag
     */
    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    /**
     * Set the company's name
     * @param companyName the name of the company
     */
    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }


    /************************************************************
     *              Comparators for Sorting
     ************************************************************/
    /**
     * Sort the tags in the wallet by the Institution name
     */
    static class TagSortByInstitutionName implements Comparator<Tag> {
        public int compare(Tag tag1, Tag tag2){
            String institutionName1 = tag1.getName().toUpperCase();
            String institutionName2 = tag2.getName().toUpperCase();
            return institutionName1.compareTo(institutionName2);
        }
    }

    /**
     * Sort the tags in the wallet by institution id
     */
    static class TagSortByInstitutionId implements Comparator<Tag>{
        public int compare(Tag tag1, Tag tag2){
            String id1 = tag1.getCompanyName();
            String id2 = tag2.getCompanyName();
            return id1.compareTo(id2);
        }
    }

    /**
     * Sort the tags by the date that they were added to the wallet
     */
    static class TagSortByDate implements Comparator<Tag>{
        public int compare(Tag tag1, Tag tag2){
            Date date1 = tag1.getAddedDate();
            Date date2 = tag2.getAddedDate();
            return date1.compareTo(date2);
        }
    }
    /*********************************************************************
     *                           Other Stuff
     ********************************************************************/

    /**
     * Convert a bitmap into an array of bytes
     * @param picture the original bitmap image
     * @param wActivity the context of the original app
     * @return the byte array
     */
    public static byte[] bitmapToByteArray(Bitmap picture, Activity wActivity){
        Drawable d = new BitmapDrawable(wActivity.getResources(), picture);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Convert a byte Array into a bitmap
     * @param byteArray The byte array that the image is stored in
     * @param wActivity a reference to the activity
     * @return the combined bitmap
     */
    public static Bitmap byteArrayToBitmap(byte[] byteArray, Activity wActivity){
        Bitmap bmp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        return bmp;
    }

}