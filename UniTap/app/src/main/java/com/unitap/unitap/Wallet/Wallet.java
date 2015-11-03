package com.unitap.unitap.Wallet;

//SimpleXML

import org.simpleframework.xml.Default;

import java.util.ArrayList;
import java.util.Collections;

/** This will be the wallet which will contain all of the users tags
 * Created by Brandon Marino on 9/22/2015.
 */
@Default
public class Wallet extends ArrayList {
    //this is the specific list that contains all of their tags
    //maybe later, we could support multiple wallets, and those wallets could each be assigned to a username
    private String username;    //for later when we implement multiple wallets
    //this should be moved to a setting option which writes to a config file
    public enum SORT_TYPE {institution_name, institution_id, added_date}
    private SORT_TYPE sortBy = SORT_TYPE.institution_name;

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
        return (Tag) this.get(index);
    }

    /**
     * Get the username
     * @return a username
     */
    public String getUsername(){
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
        this.add(newTag);
        sort(); //should be sorted as the user sees fit
        return (this.contains(newTag));
    }

    /********************************************
     *              Sort this Wallet
     ********************************************/
    /**
     * Change the sort type
     * @param sortBy by type
     */
    public void changeSort(SORT_TYPE sortBy){
        this.sortBy = sortBy;
    }

    public void sort(){
        switch (sortBy){
            case institution_name:
                sortByInstitutionalName();
                break;
            /*
            case institution_id:
                sortByInstitutionId();
                break;
                */
            case added_date:
                sortByDate();
                break;
            default:
                sortByInstitutionalName();
        }
    }

    private void sortByInstitutionalName(){
        Collections.sort(this, new Tag.TagSortByInstitutionName());
    }
/*
    private void sortByInstitutionId(){
        Collections.sort(this, new Tag.TagSortByInstitutionId() );
    }
*/
    private void sortByDate(){
        Collections.sort(this, new Tag.TagSortByDate() );
    }


}
