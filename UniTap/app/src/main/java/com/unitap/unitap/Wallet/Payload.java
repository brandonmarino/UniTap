package com.unitap.unitap.Wallet;

import org.simpleframework.xml.*;

/**The actual information of the card
 * Created by Brandon Marino on 9/22/2015.
 */
@Default
public class Payload{
    private int terminal_id;
    private int user_id;
    private boolean pinRequired;

    /**
     * Default constructor required for simpleXML
     */
    public Payload(){
        this(1,1);
    }

    /**
     * What we'll use when we dont want to specify a pin to be used
     * @param terminal_id some id by which we will differentiate the different terminals
     * @param user_id the user's id. This is their id on the database, not the app
     */
    public Payload (int terminal_id, int user_id){
        this.terminal_id = terminal_id;
        this.user_id = user_id;
        this.pinRequired = false;
    }

    /**
     * What to use when a pin is required
     * @param terminal_id some id by which we will differentiate the different terminals
     * @param user_id the user's id. This is their id on the database, not the wallet
     * @param pinRequired if this card requires a pin to work
     */
    public Payload (int terminal_id, int user_id, boolean pinRequired)
    {
        this.terminal_id = terminal_id;
        this.user_id = user_id;
        this.pinRequired = pinRequired;
    }

    /**
     * Check if this card has been properly initialized
     * @return if this card has been properly initialized
     */
    public boolean isValid(){
        return (terminal_id != 0) && (user_id != 0);
    }

    /**
     * check if this message needs to be send with a pin
     * @return if a pin is required
     */
    public boolean hasPin(){
        return pinRequired;
    }
}
