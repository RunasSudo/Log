package org.pokepal101.log.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.pokepal101.log.ChangeSet;
import org.pokepal101.log.Modification;
import org.pokepal101.log.Position;

/**
 * 
 * @author cedeel
 *
 */
public class FileDataStore implements DataStore {

    private static File _logfile;
    private static BufferedDataFileWriter bw;
    private static BufferedReader rdr;

    public FileDataStore(File logfile) {
        _logfile = logfile;
        try {
            if (!_logfile.exists()) {
                if (!_logfile.getParentFile().exists()) {
                    _logfile.getParentFile().mkdirs();
                }
                _logfile.createNewFile();
            }
            bw = new BufferedDataFileWriter(_logfile);
            rdr = new BufferedReader(new FileReader(_logfile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<ChangeSet> getAll() {
        ArrayList<ChangeSet> cs = new ArrayList<ChangeSet>();
        bw.flush();
        resetReader();
        try {
            String s = null;
            while ((s = rdr.readLine()) != null) {
                if (s.length() > 0) {
                    StringTokenizer token = new StringTokenizer(s, ";");

                    cs.add(buildCs(token));
                }
            }
            rdr.close();
        } catch (Exception e) {

        }
        return cs;

    }

    @Override
    public ArrayList<ChangeSet> getByName(String name) {
        ArrayList<ChangeSet> cs = new ArrayList<ChangeSet>();
        bw.flush();
        resetReader();
        try {
            String s = null;
            while ((s = rdr.readLine()) != null) {
                if (s.length() > 0) {
                    StringTokenizer token = new StringTokenizer(s, ";");

                    ChangeSet change = buildCs(token);

                    if (change.getMod().getWho().equalsIgnoreCase(name))
                        cs.add(change);
                }
            }
            rdr.close();
        } catch (Exception e) {

        }
        return cs;

    }
    
    @Override
    public ArrayList<ChangeSet> getByPos(Position p) {
        ArrayList<ChangeSet> cs = new ArrayList<ChangeSet>();
        bw.flush();
        resetReader();
        try {
            String s = null;
            while ((s = rdr.readLine()) != null) {
                if (s.length() > 0) {
                    StringTokenizer token = new StringTokenizer(s, ";");

                    ChangeSet change = buildCs(token);

                    if (change.getPos().toString().equalsIgnoreCase(p.toString()) && p.getWorld().equalsIgnoreCase(change.getPos().getWorld()))
                        cs.add(change);
                }
            }
            rdr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cs;
    }

    @Override
    public boolean clear(long seconds) {
        bw.flush();
        resetReader();
        ArrayList<ChangeSet> all = getAll();
        ArrayList<ChangeSet> writeBuffer = new ArrayList<ChangeSet>();
        Date nowDate = new Date();
        for (ChangeSet change : all) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd/MM/yyyy HH:mm:ss");
            Date modDate = null;
            try {
                modDate = formatter.parse(change.getMod().getDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
            long s1 = ((nowDate.getTime() - modDate.getTime()) / 1000);
            
            if(s1 < seconds) {
                writeBuffer.add(change);
            }
        }
        try {
            bw.erase();
            for(int i = 0; i < writeBuffer.size(); i++) {
                writeSingle(writeBuffer.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    
    @Override
    public boolean clearPlayer(String name) {
        resetReader();
        ArrayList<ChangeSet> toWrite = new ArrayList<ChangeSet>();
        
        for(ChangeSet change : getAll()) {
            if(!change.getMod().getWho().equalsIgnoreCase(name))
                toWrite.add(change);
        }
        bw.erase();
        for(ChangeSet change : toWrite) {
            if(!writeSingle(change))
                return false;
        }
        bw.flush();
        return true;
    }
    
    @Override
    public boolean writeSingle(ChangeSet cs) {
        String toWrite =
                cs.getPos().getData() + ";"
                + cs.getMod().getData();
        
        try {
            bw.write(toWrite);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean purge() {
        try {
            rdr.close();
            bw.erase();
            resetReader();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void persist() throws RuntimeException {
        try {
            bw.close();
            rdr.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ChangeSet buildCs(StringTokenizer token) {
        String world = token.nextToken();

        int x = Integer.parseInt(token.nextToken());
        int y = Integer.parseInt(token.nextToken());
        int z = Integer.parseInt(token.nextToken());
        Position pos = new Position(world, x, y, z);

        String who = token.nextToken();

        boolean placed = Boolean.parseBoolean(token.nextToken());

        int blockid = Integer.parseInt(token.nextToken());

        String datedata = token.nextToken();
        Modification mod = new Modification(who, placed, blockid, datedata);

        return new ChangeSet(mod, pos);
    }

    private void resetReader() {
        try {
            rdr = new BufferedReader(new FileReader(_logfile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
