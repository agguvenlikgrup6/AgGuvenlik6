package org.uludag.bmb.entity;

public class PathTree {

    public PathNode root;

    public PathTree(PathNode root) {
        this.root = root;
    }

    public void addElement(String elementValue) {
        String[] list = elementValue.split("/");
        root.addElement(root.incrementalPath, list);
    }
}