package org.pokepal101.log;

/**
 * 
 * @author pokepal101
 * @author chris
 *
 */
public class Position implements java.io.Serializable {
    static final long serialVersionUID = -6953548090074503L;
    private int x, y, z;
    private String world;

    public Position(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    public String getWorld() {
        return world;
    }

    public boolean equals(Object oth) {
        if (!(oth instanceof Position))
            return false;
        return (oth.hashCode() == hashCode());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        return world + "," + x + ", " + y + ", " + z;
    }

    public String getData() {
        return world + ";" + x + ";" + y + ";" + z;
    }
}