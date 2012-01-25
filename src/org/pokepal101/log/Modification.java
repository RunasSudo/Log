package org.pokepal101.log;

/**
 * 
 * @author pokepal101
 * @author chris
 *
 */

public class Modification implements java.io.Serializable
{
    static final long serialVersionUID = 6797996890572426758L;
    private String who;
    private boolean placed;
    private int blockId = 0;
    private String date;

    public Modification (String who, boolean placed, int blockId, String date)
    {
        this.who = who;
        this.placed = placed;
        this.blockId = blockId;
        this.date = date;
    }

    /**
     * Get the player's name
     * @return The name of the player
     */
    public String getWho ()
    {
        return who;
    }

    /**
     * A boolean indicating whether the block was placed or destroyed.
     * @return True if the block was placed.
     */
    public boolean getPlaced ()
    {
        return placed;
    }

    /**
     * Gets the ID of the block in question
     * @return The block type ID.
     */
    public int getBlockID ()
    {
        return blockId;
    }

    /**
     * Date of change
     * @return The date the change happened.
     */
    public String getDate ()
    {
        return date;
    }

    public String getData ()
    {
        return who + ";" + placed + ";" + blockId + ";" + date;
    }
}