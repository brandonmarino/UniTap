package com.unitap.unitap.Wallet;

//SimpleXML

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/** This will be the wallet which will contain all of the users tags
 * Created by Brandon Marino on 9/22/2015.
 */
@Root
public class Wallet {
    //this is the specific list that contains all of their tags
    //maybe later, we could support multiple wallets, and those wallets could each be assigned to a username
    @Element
    private String username;    //for later when we implement multiple wallets
    @ElementList
    private List<Tag> tags ;

    /**
     * Default constructor for simpleXML
     */
    public Wallet(){
        this("");
    }

    /**
     * Constructor when given a name
     * @param username a name
     */
    public Wallet(String username){
        this.username = username;
        tags = new ArrayList<Tag>();
    }

    /*******************************************
     *              Getters
     *******************************************/
    /**
     * Check the number of cards in the wallet
     * @return the current size of the wallet
     */
    public int getSize(){
        return tags.size();
    }

    /**
     * Check if the wallet is just empty
     * @return if the wallet is empty
     */
    public boolean isEmpty(){
        return (getSize() == 0 );
    }

    /**
     * Get a tag by a certain location, good which you are selecting the card out of the the cardset
     * @param index the spot in the list
     * @return the actual full tag
     */
    public Tag getByIndex(int index){
        return tags.get(index);
    }

    /**
     * Get the username
     * @return a username
     */
    public String getUserName(){
        return username;
    }

    /*****************************************
     *      Adders
     *****************************************/
    /**
     *  add a tag to the wallet
     * @param newTag a vitualTag tag which is being added to the wallet.  This should autosort it by the user's decided sort type
     * @return if the card was inserted, sorted and the list still contains it
     */
    public boolean addTag(Tag newTag){
        tags.add(newTag);
        sortAlphabetical(); //should be sorted as the user sees fit
        return (tags.contains(newTag));

    }

    /********************************************
     *              Sort this Wallet
     ********************************************/

    public void sortAlphabetical(){
        /*
        for (Wallet.Tag currentTag: tags){
            int index = i;

            for (int j = i + 1; j < arr.length; j++)

                if (arr[j] < arr[index])

                    index = j;

            int smallerNumber = arr[index];
            arr[index] = arr[i];
            arr[i] = smallerNumber;
        }*/
     }

    public void sortByDate(){

    }

    public void sortByVendor(){

    }

}
