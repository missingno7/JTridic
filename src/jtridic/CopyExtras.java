/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

/**
 *
 * @author vestfal
 */
public class CopyExtras {

    public boolean den = true;
    public boolean mesic = true;
    public boolean rok = true;
    public boolean obdobi = true;
    public boolean presunout = true;
    public String typ = "*.*";

    public CopyExtras() {
    }

    public CopyExtras(String dmr, String typ) {
        this.typ = typ;
        setDMRfromString(dmr);
    }

    public CopyExtras clone() {
        CopyExtras newExtras = new CopyExtras();
        newExtras.den=den;
        newExtras.mesic=mesic;
        newExtras.rok=rok;
        newExtras.obdobi=obdobi;
        newExtras.presunout=presunout;
        newExtras.typ=typ;
        
        return newExtras;
    }

    private void setDMRfromString(String dmr) {
        if (dmr.contains("rok")) {
            rok = true;
        } else {
            rok = false;
        }

        if (dmr.contains("mesic")) {
            mesic = true;
        } else {
            mesic = false;
        }

        if (dmr.contains("den")) {
            den = true;
        } else {
            den = false;
        }
        
        if (dmr.contains("obdobi")) {
            obdobi = true;
        } else {
            obdobi = false;
        }
        

        if (dmr.contains("presunout")) {
            presunout = true;
        } else {
            presunout = false;
        }

    }
    
    public String toFileString(){
        String fStr="";
        
        if(rok)fStr+="rok,";
        if(mesic)fStr+="mesic,";
        if(den)fStr+="den,";
        if(obdobi)fStr+="obdobi,";
        if(presunout)fStr+="presunout,";
        
        
        return fStr;
    }

    @Override
    public String toString() {
        String str = "";
        str += "Typ " + typ;
        str += ",den " + den;
        str += ",mesic " + mesic;
        str += ",rok " + rok;
        str += ",obdobi " + obdobi;
        str += ",presunout " + presunout;

        return str;
    }

}
