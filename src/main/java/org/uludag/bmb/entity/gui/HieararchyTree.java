package org.uludag.bmb.entity.gui;

public class HieararchyTree {

    public HieararchyNode root;

    public HieararchyTree(HieararchyNode root) {
        this.root = root;
    }

    public void addElement(String elementValue) {
        String[] list = elementValue.split("/");
        root.addElement(root.incrementalPath, list);
    }
}