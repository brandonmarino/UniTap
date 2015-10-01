package com.unitap.unitap.Wallet;

import java.util.ArrayList;
import java.util.List;

/** This will be the wallet which will contain all of the users tags
 * Created by Brandon Marino on 9/22/2015.
 */
public class VirtualWallet implements java.io.Serializable{
    //this is the specific list that contains all of their tags
    //private Hashtable<Integer, VirtualTag> tags = new Hashtable<>();
    //maybe later, we could support multiple wallets, and those wallets could each be assigned to a username
    private List<VirtualTag> tags = new ArrayList<>();

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
     * @return if the wwallet is empty
     */
    public boolean isEmpty(){
        return (getSize() == 0 );
    }

    /**  Get the card by the vendor or cardId
     *  Good for tapping the card to the terminal, getting the card by id, then returning it
     * @param id te id of the specific card type or vendor
     * @return the actual full tag
     */
    public VirtualTag getById(int id){
        for (VirtualTag currentTag: tags)
            if (currentTag.getId() == id)
                return currentTag;
        return null;
    }

    /**
     * Get a tag by a certain location, good which you are selecting the card out of the the cardset
     * @param index the spot in the list
     * @return the actual full tag
     */
    public VirtualTag getByIndex(int index){
        return tags.get(index);
    }

    /*****************************************
     *      Adders
     *****************************************/
    /**
     *  add a tag to the wallet
     * @param newTag a vitualTag tag which is being added to the wallet.  This should autosort it by the user's decided sort type
     * @return if the card was inserted, sorted and the list still contains it
     */
    public boolean addTag(VirtualTag newTag){
        tags.add(newTag);
        sortAlphabetical(); //should be sorted as the user sees fit
        return (tags.contains(newTag));

    }

    /********************************************
     *              Sort this VirtualWallet
     ********************************************/

    public void sortAlphabetical(){
        /*
        for (VirtualTag currentTag: tags){
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
