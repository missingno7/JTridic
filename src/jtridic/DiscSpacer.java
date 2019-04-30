/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.io.File;
import java.util.Vector;

/**
 *
 * @author vestfal
 */
public class DiscSpacer {

    DiscSpacer() {
        items = new Vector<DiscSpaceItem>();
    }

    void addToItems(File from, File to, boolean move) {

        char drvLet = to.toString().charAt(0);

        long fileSize = 0;
        if (drvLet != from.toString().charAt(0) || !move) {
            fileSize = from.length();
        }

        
        boolean found = false;

        for (DiscSpaceItem item : items) {
            if (item.driveLetter == drvLet) {
                item.needed += fileSize;
                found = true;
                break;
            }
        }

        if (!found) {
            DiscSpaceItem newItem = new DiscSpaceItem();
            newItem.driveLetter = drvLet;
            newItem.remaining = to.toPath().getRoot().toFile().getFreeSpace();
            newItem.needed = fileSize;
            items.add(newItem);
        }

    }
    
    void Clear(){
        items.clear();
    }

    public Vector<DiscSpaceItem> items;
}
