/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author vestfal
 */
public class Copier {

    private static Copier instance = null;

    protected Copier() {
        // Exists only to defeat instantiation.
        items = new Vector<CopierItem>();

    }

    public static Copier getInstance() {
        if (instance == null) {
            instance = new Copier();
        }
        return instance;
    }

    public void Initialize(JLabel display, JProgressBar progressBar, CopierController cpControl, JLabel statusDisplay) {
        if (!initialized) {

            // DO initialization
            this.display = display;
            this.progressBar = progressBar;
            this.control = cpControl;
            this.statusDisplay = statusDisplay;

            initialized = true;
        }
    }

    public Vector<File> addFiles(Vector<File> files, File dir) {
        if (files == null) {
            files = new Vector<File>();
        }

        if (!dir.isDirectory()) {
            files.add(dir);
            return files;
        }

        for (File file : dir.listFiles()) {
            addFiles(files, file);
        }
        return files;
    }

    public void readItems() {
        Vector<DetectedFolderListItem> detectedFolders = DetectedFolderList.getInstance().getDetectedFolderList();

        Config cf = Config.getInstance();
        DiscSpacer discSpace = new DiscSpacer();

        for (DetectedFolderListItem detectedFolder : detectedFolders) {
            Vector<File> files = addFiles(null, detectedFolder.from);
            if (files != null) {
                for (File file : files) {

                    CopierItem newItem = new CopierItem();
                    newItem.from = file;
                    newItem.to = detectedFolder.to;
                    items.add(newItem);
                    discSpace.addToItems(file, detectedFolder.to, cf.extras.presunout);
                    
                    //System.out.println(file.getPath());
                    //System.out.println("VELIKOST: " + file.length() / 1024 / 1024);
                    //System.out.println(getGoalPath(file, detectedFolder.to, cf.extras));
                }
            }

        }

        if (items.size() != 0) {
            CopierItem last = items.get(items.size() - 1);
            lastFolderFrom = last.from.getParent();
            lastFolderTo = last.to.getAbsolutePath();
            //System.out.println(lastFolderFrom+"---"+lastFolderTo);
        }

        updateDisplay(discSpace.items);

    }

    private Long getItemsSize() {
        long totalSize = 0;
        for (CopierItem item : items) {
            totalSize += item.from.length();
        }
        return totalSize;
    }

    private File findNextAvailable(File f) {

        int index = 1;
        String newFileName = f.toString();
        int pos = newFileName.lastIndexOf("."); // Position of file extension

        // Fix when filename does not contain dot
        if (pos < (newFileName.length() - f.getName().length())) {
            pos = newFileName.length();
        }

        File newFile = null;

        while ((newFile = new File(newFileName.substring(0, pos) + "_" + index + newFileName.substring(pos))).exists()) {
            index++;
        }

        return newFile;
    }

    public void doCopy() {

        CopyDialogData dialogData = new CopyDialogData();

        if (items.size() == 0) {
            display.setText("Není co kopírovat.");
            return;
        }

        Config cf = Config.getInstance();
        long totalSize=getItemsSize();
        

        int i = 0;

        //System.out.println(totalSize);
        long copiedSpace = 0;
        int filesCount = items.size();

        
        progressBar.setMaximum((int) filesCount);// possible lossy
        progressBar.setStringPainted(true);

        long startTime=System.currentTimeMillis();
        
        for (CopierItem item : items) {
            long fileToCopySize = item.from.length();

            while (control.paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Copier.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (control.stopped) {
                    break;
                }
            }

            if (control.stopped) {
                break;
            }

            String pathTo = getGoalPath(item.from, item.to, cf.extras);

            File folderTo = new File(pathTo);
            File fileTo = new File(pathTo + item.from.getName());

            if (!folderTo.exists()) {
                folderTo.mkdirs();
            }

            //System.out.println(item.from.toPath().toString() + "->" + fileTo.toPath().toString());
            progressBar.setValue((int) i); // Possible lossy conversion
            //progressBar.update(progressBar.getGraphics());
            //progressBar.repaint();

            //statusDisplay.setText(item.from.getAbsolutePath());
            updateCopyDisplay                    (item.from,     filesCount,        i,          copiedSpace, totalSize,System.currentTimeMillis()-startTime);

            //public void updateCopyDisplay(File currentFile, int filesCount, int fileNumber, long spaceUsed, long totalSize, long elapsedTime) {

            try {
                if (cf.extras.presunout) {
                    Files.move(item.from.toPath(), fileTo.toPath());
                } else {
                    Files.copy(item.from.toPath(), fileTo.toPath());
                }

            } catch (IOException ex) {

                int n = 1;

                if (!dialogData.forAll) {

                    dialogData.srcFile = item.from;
                    dialogData.destFile = fileTo;

                    // Show dialog and wait for answer
                    CopyDialog dial = new CopyDialog(new javax.swing.JFrame(), true, dialogData);
                    dial.setVisible(true);
                }

                if (dialogData.state == dialogData.state.CANCEL) {
                    break;
                }

                if (dialogData.state == dialogData.state.MOVE) { // Přepsat
                    try {
                        if (cf.extras.presunout) {

                            Files.move(item.from.toPath(), fileTo.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        } else {
                            Files.copy(item.from.toPath(), fileTo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException ex1) {
                        JOptionPane.showMessageDialog(display.getParent(),
                                "Soubor " + item.from.toString() + " se nepodařilo zkopírovat. Přeskakuji.",
                                "Strašlivá chyba",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }

                if (dialogData.state == dialogData.state.KEEPBOTH) {
                    try {
                        if (cf.extras.presunout) {

                            Files.move(item.from.toPath(), findNextAvailable(fileTo).toPath());

                        } else {
                            Files.copy(item.from.toPath(), findNextAvailable(fileTo).toPath());
                        }
                    } catch (IOException ex1) {
                        JOptionPane.showMessageDialog(display.getParent(),
                                "Soubor " + item.from.toString() + " se nepodařilo zkopírovat. Přeskakuji.",
                                "Strašlivá chyba",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }

            lastFolderTo = fileTo.getParent();
            copiedSpace += fileToCopySize;
            i++;

        }

        statusDisplay.setText("");
        progressBar.setMaximum(100);
        progressBar.setValue(100);

        clearItems();

        DetectedFolderList.getInstance().clearItems();

    }

    public void clearItems() {
        items.clear();
        if (control.stopped){
        display.setText("Přerušeno.");
        }
        else
        {
        display.setText("VŠE HOTOVO!!");
        }

    }

    public String getGoalPath(File file, File path, CopyExtras extras) {
        String copyTo = path.toString();
        copyTo += "\\";

        //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(file.lastModified());

        if (extras.rok) {
            copyTo += date.getYear() - 100 + 2000 + "\\";
        }

        if (extras.obdobi) {
            int month = date.getMonth() + 1;
            if (month >= 3 && month <= 5) {
                copyTo += "Jaro" + "\\";
            }
            if (month >= 6 && month <= 8) {
                copyTo += "Léto" + "\\";
            }
            if (month >= 9 && month <= 11) {
                copyTo += "Podzim" + "\\";
            }
            if (month >= 12 && month <= 2) {
                copyTo += "Zima" + "\\";
            }
        }

        if (extras.mesic) {
            copyTo += (date.getMonth() + 1) + "\\";
        }

        //System.out.println(date.getDate());
        if (extras.den) {
            copyTo += date.getDate() + "." + (date.getMonth() + 1) + "." + (date.getYear() - 100 + 2000) + "\\";
        }

        return copyTo;
    }

    public void updateDisplay(Vector<DiscSpaceItem> items) {
        for (DiscSpaceItem item : items) {
            String text = "";

            text = "Disk " + item.driveLetter + ", potřeba: " + Other.bToMB(item.needed) + " MB, zbýva: " + Other.bToMB(item.remaining) + " MB";

            if (item.needed >= item.remaining) {
                text += ", CHYBI " + Other.bToMB(item.needed - item.remaining) + "MB místa";
            } else {
                text += ", OK.";
            }

            display.setText(text);
        }

    }

    public void updateCopyDisplay(File currentFile, int filesCount, int fileNumber, long spaceUsed, long totalSize, long elapsedTime) {

        String text = "<HTML>";

        text += currentFile.getAbsolutePath() + "<BR>";

        text += fileNumber + "/" + filesCount + " souborů";
        text += " (" + Other.bToMB(spaceUsed) + " MB/ " + Other.bToMB(totalSize) + " MB)" + "<BR>";

        double mbs=(spaceUsed/1048.576)/elapsedTime;
        double pols=(fileNumber*1000.0)/elapsedTime;
        text += String.format("%.02f", mbs) + " MB/s"+ " (";
        text += String.format("%.02f", pols) + " souborů/s)"+ "<BR>";
        
        long remainSize=totalSize-spaceUsed;
        double copySpeed=spaceUsed/(double)elapsedTime;
        //text += "TOTALSIZE: "+totalSize/1000000+ "<BR>";
        //text += "SPACEUSED: "+spaceUsed/1000000+ "<BR>";
        //text += "ELAPSEDTIME: "+elapsedTime+ "<BR>";
        
        //text += "COPYSPEED: "+copySpeed+ "<BR>";
        
        long remainingTime=(long) (remainSize/copySpeed);
        //text += "REMAININGTIME: "+remainingTime+ "<BR>";
    
        String remainTimeStr=String.format("%d min. %d s.", 
        TimeUnit.MILLISECONDS.toMinutes(remainingTime),
        TimeUnit.MILLISECONDS.toSeconds(remainingTime) - 
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
        );
        
        text += "Zbývá: "+remainTimeStr+ "<BR>";
        
        
        
        text += "</HTML>";
        statusDisplay.setText(text);

    }

    
    
    private Vector<CopierItem> items;

    private boolean initialized = false;
    private JLabel display;
    private JLabel statusDisplay;
    private JProgressBar progressBar;
    private CopierController control;
    String lastFolderFrom = "";
    String lastFolderTo = "";

}
