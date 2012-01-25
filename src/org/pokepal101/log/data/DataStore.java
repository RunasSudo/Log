package org.pokepal101.log.data;

/**
 * @author cedeel
 */

import java.util.ArrayList;

import org.pokepal101.log.ChangeSet;
import org.pokepal101.log.Position;

public interface DataStore {
    
public ArrayList<ChangeSet> getAll();
    
    public ArrayList<ChangeSet> getByName(String name);
    
    public boolean clear(long seconds);
    
    public boolean purge();
    
    public boolean writeSingle(ChangeSet cs);
    
    public ArrayList<ChangeSet> getByPos(Position p);
    
    public boolean clearPlayer(String name);
    
    public void persist() throws RuntimeException;

}
