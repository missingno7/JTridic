/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author vestfal
 */
public class Other {

    public static Vector<String> splitToListByQuotationMarks(String line) {

        //System.out.println(line);
        Vector<String> lst = new Vector<String>();

        int itr = 0;

        boolean instr = false;

        String str = "";

        //System.out.println(".."+line+"..");
        while (itr < line.length()) {
            if (line.charAt(itr) == '"') {
                if (instr) {
                    instr = false;
                    lst.add(str);
                    str = "";
                } else {
                    instr = true;
                }
            } else {
                if (instr) {
                    str += line.charAt(itr);
                }
            }
            itr++;
        }

        /*if (instr) {
            lst.add(str);
        }*/
        return lst;

    }

    public static Vector<String> splitToListByCommas(String line) {

        //System.out.println(line);
        Vector<String> lst = new Vector<String>();

        int itr = 0;

        String str = "";

        while (itr < line.length()) {
            if (line.charAt(itr) == ',') {
                lst.add(str);
                str = "";

            } else {
                str += line.charAt(itr);
            }
            itr++;
        }
        lst.add(str);

        return lst;

    }

    public static String bToMB(long bytes) {
        
        double mb=bytes / 1024 / 1024;

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(mb);
        
    }

    public static boolean sameContent(Path file1, Path file2) throws IOException {
        // if(true)return false;

        int maxbytes = 1000;
        int i = 0;
        final long size = Files.size(file1);
        if (size != Files.size(file2)) {
            return false;
        }

        if (size < 4096) {
            return Arrays.equals(Files.readAllBytes(file1), Files.readAllBytes(file2));
        }

        try (InputStream is1 = Files.newInputStream(file1);
                InputStream is2 = Files.newInputStream(file2)) {
            // Compare byte-by-byte.
            // Note that this can be sped up drastically by reading large chunks
            // (e.g. 16 KBs) but care must be taken as InputStream.read(byte[])
            // does not neccessarily read a whole array!
            int data;
            while ((data = is1.read()) != -1) {
                if (i >= maxbytes) {
                    return true;
                }
                if (data != is2.read()) {
                    return false;
                }
                i++;
            }
        }

        return true;
    }

}
