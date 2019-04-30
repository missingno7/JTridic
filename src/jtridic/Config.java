/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 *
 * @author vestfal
 */
public class Config {

    private static Config instance = null;

    protected Config() {
        // Exists only to defeat instantiation.
        items = new Vector<ConfigItem>();
        extras = new CopyExtras();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void Initialize(JCheckBox cDen, JCheckBox cMesic, JCheckBox cRok, JCheckBox cObdobi, JCheckBox cPresunout, JTextField typ) {
        if (!initialized) {
            // DO initialization

            this.cRok = cRok;
            this.cMesic = cMesic;
            this.cDen = cDen;
            this.cObdobi = cObdobi;
            this.cPresunout = cPresunout;
            this.typ = typ;
            initialized = true;
        }
    }

    public Vector<ConfigItem> getConfigItems() {
        return (Vector<ConfigItem>) items.clone();
    }

    public void LoadConfig(String fileName) {
        // Load configured devices
        //ClearConfig();
        configFileName = fileName;

        File cfg = new File(fileName);
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(cfg));

            String readLine = "";

            //System.out.println("Reading file using Buffered Reader");
            while ((readLine = bReader.readLine()) != null) {
                //System.out.println(readLine);

                Vector<String> line = Other.splitToListByQuotationMarks(readLine);

                if (line.size() == 0) {
                    continue; // Skip empty lines
                }
                if (line.size() != 3) {
                    System.out.println("Chyba konfigurace, načteno " + line.size() + " položek na řádku, očekáváno 5.");
                    continue;
                }

                ConfigItem newItem = new ConfigItem();
                newItem.readInFolders = Other.splitToListByCommas(line.get(0));
                newItem.contains = line.get(1);
                newItem.copyTo = line.get(2);
                // newItem.extras = new CopyExtras(line.get(3), line.get(4));
                items.add(newItem);

                //System.out.println(newItem.toString());
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void LoadCheckConfig(String fileName) {
        // Load configured devices
        chkboxFileName = fileName;

        File cfg = new File(fileName);
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(cfg));

            String readLine = "";

            readLine = bReader.readLine();

            extras = new CopyExtras(readLine, typ.getText());
            updateCheckboxes(extras);

            //System.out.println(newItem.toString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateCheckboxes(CopyExtras exs) {

        //System.out.println(exs.toString());
        cRok.setSelected(exs.rok);
        cMesic.setSelected(exs.mesic);
        cDen.setSelected(exs.den);
        cObdobi.setSelected(exs.obdobi);
        cPresunout.setSelected(exs.presunout);
    }

    public void ClearConfig() {
        items.clear();
    }

    public void setRok(boolean newRok) {
        extras.rok = newRok;
        checkboxConfigUpdate();
    }

    public void setMesic(boolean newMesic) {
        extras.mesic = newMesic;
        checkboxConfigUpdate();
    }

    public void setDen(boolean newDen) {
        extras.den = newDen;
        checkboxConfigUpdate();
    }

    public void setObdobi(boolean newObdobi) {
        extras.obdobi = newObdobi;
        checkboxConfigUpdate();
    }

    public void setPresunout(boolean newPresunout) {
        extras.presunout = newPresunout;
        checkboxConfigUpdate();
    }

    private void checkboxConfigUpdate() {
        //extras.toFileString();
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(chkboxFileName), "utf-8"));
            writer.write(extras.toFileString());
        } catch (IOException ex) {
            // Report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {/*ignore*/
            }
        }

    }

    private Vector<ConfigItem> items;
    private boolean initialized = false;
    public CopyExtras extras;
    private JCheckBox cRok, cMesic, cDen, cObdobi, cPresunout;
    private JTextField typ;
    String configFileName, chkboxFileName;

}
