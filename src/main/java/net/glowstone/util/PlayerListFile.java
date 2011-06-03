package net.glowstone.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Utility class for storing lists of player names.
 * @author Tad
 */
public final class PlayerListFile {
    
    /**
     * The list as we currently know it.
     */
    private final ArrayList<String> list = new ArrayList<String>();
    
    /**
     * The file the list is associated with.
     */
    private final File file;
    
    /**
     * Initialize the player list from the given file.
     * @param path The file to use for this list.
     */
    public PlayerListFile(String path) {
        this(new File(path));
    }
    
    /**
     * Initialize the player list from the given file.
     * @param file The file to use for this list.
     */
    public PlayerListFile(File file) {
        this.file = file;
        load();
    }
    
    /**
     * Reloads from the file.
     */
    public void load() {
        list.clear();
        try {
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine().trim().toLowerCase();
                if (line.length() > 0) {
                    if (!list.contains(line))
                        list.add(line);
                }
            }
            Collections.sort(list);
            save();
        } catch (FileNotFoundException ex) {
            save();
        }
    }
    
    /**
     * Saves to the file.
     */
    private void save() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file));
            for (String str : list) {
                out.println(str);
            }
            out.flush();
            out.close();
        } catch (IOException ex) {
            // Pfft.
        }
    }
    
    /**
     * Add a player to the list.
     */
    public void add(String player) {
        if (!contains(player)) list.add(player.trim().toLowerCase());
        Collections.sort(list);
        save();
    }
    
    /**
     * Remove a player from the list.
     */
    public void remove(String player) {
        list.remove(player.trim());
        save();
    }
    
    /**
     * Check if a player is in the list.
     */
    public boolean contains(String player) {
        for (String str : list) {
            if (str.equalsIgnoreCase(player.trim()))
                return true;
        }
        return false;
    }
    
}
