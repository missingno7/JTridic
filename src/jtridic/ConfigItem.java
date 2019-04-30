/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.nio.file.Path;
import java.util.Vector;

/**
 *
 * @author vestfal
 */
public class ConfigItem {

    public String contains;
    public String copyTo;
    public Vector<String> readInFolders;
   

    @Override
    public String toString() {
        String str = "";

        str += "From folders" + readInFolders.size() + ": ";

        for (String fld : readInFolders) {
            str +=fld+",";
        }

        str += ",Contains: " + contains;
        str += ",CopyTo: " + copyTo;
        //str += ", EXTRAS: "+extras.toString();
                
        return str;
    }

}
