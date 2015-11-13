package com.unitap.unitap.Wallet;

//SimpleXML

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collections;

/** This will be the wallet which will contain all of the users tags
 * Created by Brandon Marino on 9/22/2015.
 */
@Root
public class Wallet{
    @ElementList(inline=true)
    private ArrayList<Tag> wallet = new ArrayList<>(); //this is the specific list that contains all of their tags
    @Attribute
    private String username;    //for later when we implement multiple wallets
    @Element
    private Integer sortBy = 0;

    //below fields should be moved to a settings activity which writes to a config file
    //public enum SORT_TYPE {institution_name, institution_id, added_date}
    //private SORT_TYPE sortBy = SORT_TYPE.institution_name;

    /**
     * Default constructor for simpleXML
     */
    public Wallet(){
        this("");
        wallet = new ArrayList<>();
    }

    /**
     * Constructor when given a name
     * @param username a name
     */
    public Wallet(String username){
        this.username = username;
    }

    /*******************************************
     *              Getters
     *******************************************/

    /**
     * Get a tag by a certain location, good which you are selecting the card out of the the cardset
     * @param index the spot in the list
     * @return the actual full tag
     */
    public Tag getByIndex(int index){
        return (Tag) wallet.get(index);
    }

    /**
     * Get the username
     * @return a username
     */
    public String getUsername(){
        return username;
    }

    /**
     * Need the wallet for display purposes.
     * @return the wallet
     */
    public ArrayList<Tag> getWallet(){
        return wallet;
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
        wallet.add(newTag);
        sort(); //should be sorted as the user sees fit
        return (wallet.contains(newTag));
    }

    /********************************************
     *              Sort this Wallet
     ********************************************/
    /**
     * Change the sort type
     * @param sortBy by type
     */
    //public void changeSort(SORT_TYPE sortBy){
    //    this.sortBy = sortBy;
    //}

    public void changeSort(int sortBy){
        this.sortBy = sortBy;
    }

    public void sort(){
        switch (sortBy){
            case 0:
                sortByInstitutionalName();
                break;
            case 1:
                sortByDate();
                break;
            default:
                sortByInstitutionalName();
        }
    }

    private void sortByInstitutionalName(){
        Collections.sort(wallet, new Tag.TagSortByInstitutionName());
    }

    private void sortByDate(){
        Collections.sort(wallet, new Tag.TagSortByDate() );
    }
}
