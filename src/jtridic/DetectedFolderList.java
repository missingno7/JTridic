/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtridic;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.io.File;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author vestfal
 */
public class DetectedFolderList {

    private static DetectedFolderList instance = null;

    protected DetectedFolderList() {
        // Exists only to defeat instantiation.
        items = new Vector<DetectedFolderListItem>();
    }

    public static DetectedFolderList getInstance() {
        if (instance == null) {
            instance = new DetectedFolderList();
        }
        return instance;
    }

    public void Initialize(JList<String> list) {
        if (!initialized) {

            // DO initialization
            this.list = list;
            initialized = true;
        }
    }

    public void detectFolders() {
        Config cfg = Config.getInstance();
        Vector<ConfigItem> cfItems = cfg.getConfigItems();

        for (ConfigItem cfItem : cfItems) {
            System.out.println(cfItem.toString());
            for (String rInFolder : cfItem.readInFolders) {
                File[] directories = new File(rInFolder).listFiles(File::isDirectory);
                if (directories != null) {
                    for (File directory : directories) {
                        if (directory.toString().contains(cfItem.contains)) {
                            System.out.println(directory.toString());

                            // Add detected item into list
                            DetectedFolderListItem newItem = new DetectedFolderListItem();
                            newItem.from = directory;
                            newItem.to = new File(cfItem.copyTo);
                            //newItem.extras = cfItem.extras.clone();
                            addItem(newItem);

                        }
                    }
                }
            }
        }
        updateField();
    }

    public void addItem(DetectedFolderListItem newItem) {
        items.add(newItem);

        //updateField();
    }

    public void clearItems() {
        items.clear();
        list.removeAll();
        Copier cp = Copier.getInstance();
        cp.clearItems();
        cp.readItems();
        updateField();

    }

    public void updateField() {
        // Update field

        DefaultListModel listModel = new DefaultListModel();

        for (DetectedFolderListItem item : items) {
            listModel.addElement(item.from + " -> " + item.to);
        }

        list.setModel(listModel);

    }

    public void removeAt(int index) {
        items.remove(index);
        Copier cp = Copier.getInstance();
        cp.clearItems();
        cp.readItems();
        updateField();
    }

    public Vector<DetectedFolderListItem> getDetectedFolderList() {
        return (Vector<DetectedFolderListItem>) items.clone();
    }

    private Vector<DetectedFolderListItem> items;
    private boolean initialized = false;
    private JList<String> list;
}
