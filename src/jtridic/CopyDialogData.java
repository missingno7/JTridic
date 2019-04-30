/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.io.File;

/**
 *
 * @author vestfal
 */



public class CopyDialogData {
    
    public enum State {
    MOVE,SKIP,KEEPBOTH,CANCEL,ERROR
}
    
    boolean forAll=false;
    State state=State.ERROR;
    
    File srcFile=null,destFile=null;
    
    
    
}
