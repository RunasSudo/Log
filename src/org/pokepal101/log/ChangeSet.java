package org.pokepal101.log;

public class ChangeSet implements java.io.Serializable {

    /**
     * 
     * @author cedeel
     */
    private static final long serialVersionUID = 3238231916493178312L;
    private Modification mod;
    private Position pos;
    
    public ChangeSet(Modification mod, Position pos) {
        this.mod = mod;
        this.pos = pos;
    }
    
    public Position getPos() {
        return pos;
    }
    
    public Modification getMod() {
        return mod;
    }

}
