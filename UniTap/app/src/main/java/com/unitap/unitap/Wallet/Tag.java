package com.unitap.unitap.Wallet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;

import com.unitap.unitap.Activities.CardActivity;
import com.unitap.unitap.R;

import org.simpleframework.xml.Default;

import java.io.ByteArrayOutputStream;
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
    private byte[] imageArray;
    private Date addedDate;
    private String payload;
    private String tagID;
    private String companyName;
    private int companyID;

    public Tag() {
        this.name = "John Doe";
        this.addedDate = new Date();
        this.payload = "THIS IS A MESSAGE TO BE SENT BY NFC";
        this.imageArray = new byte[0];
        this.tagID = "0";
        this.companyName = "Generic";
        this.companyID = 0;
    }

    /**
     * Creating the tag, we have all available info
     *
     * @param name    the name of the tag
     *                param picture the picture of the tag
     *                param id the id of the vendor/card type
     * @param payload the specific payload which needs to be dumped to the NDEF device
     */
    public Tag(String name, String payload, Activity activity) {
        this(name, BitmapFactory.decodeResource(activity.getResources(), R.drawable.tagstand_logo_icon), payload, "0", "Generic Company", 0, activity);
    }

    public Tag(String name, Bitmap image, String payload, String tagID, String companyName, int companyID, Activity wActivity) {
        this.name = name;
        this.imageArray = imageToByte(image, wActivity);
        this.addedDate = new Date();
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
    public String getPayload() {
        return payload;
    }
    /**
     * The picture which will appear in the cardview for the tag. This will be used to make it look cool.
     * @return The imageArray
    */
    public byte[] getImageArray() {
        return imageArray;
    }
    public Date getAddedDate(){
        return addedDate;
    }

    public String getCompanyName(){
        return companyName;
    }
    public int getCompanyID(){
        return companyID;
    }

    public String getTagID() {
        return tagID;
    }

    public void getCompanyID(int companyID){
        this.companyID = companyID;
    }
    public Bitmap getImage(Activity activity) {
        Bitmap bmp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        bmp = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, options);
        return bmp;
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

    public void setImageArray() {
        this.imageArray = imageArray;
    }

    /** Set the picture which will make the carview look cool
     * @param picture the picture for the cardview*/

    public void setImage(Bitmap picture, Activity activity) {
        imageArray = imageToByte(picture, activity);
    }

    /**
     * Convert a bitmap into an array of bytes
     * @param picture the original bitmap image
     * @param activity the context of the original app
     * @return the byte array
     */
    public byte[] imageToByte(Bitmap picture, Activity activity){
        Drawable d = new BitmapDrawable(activity.getResources(), picture);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Date that this card was added to the system
     * @param addedDate some date on which this card was created
     */
    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }


    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public void setCompanyName(String companyName){
        this.companyName = companyName;
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

    static class TagSortByInstitutionId implements Comparator<Tag>{
        public int compare(Tag tag1, Tag tag2){
            String id1 = tag1.getCompanyName();
            String id2 = tag2.getCompanyName();
            return id1.compareTo(id2);
        }
    }

    static class TagSortByDate implements Comparator<Tag>{
        public int compare(Tag tag1, Tag tag2){
            Date date1 = tag1.getAddedDate();
            Date date2 = tag2.getAddedDate();
            return date1.compareTo(date2);
        }
    }
}