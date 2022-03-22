package org.uludag.bmb.entity;

public class MXMTree {

    MXMNode root;
    MXMNode commonRoot;

    public MXMTree(MXMNode root) {
        this.root = root;
        commonRoot = null;
    }

    public void addElement(String elementValue) {
        String[] list = elementValue.split("/");
        root.addElement(root.incrementalPath, list);

    }

    public void printTree() {
        getCommonRoot();
        commonRoot.printNode(0);
    }

    public void getCommonRoot() {
        if (commonRoot == null) {
            MXMNode current = root;
            while (current.leafs.size() <= 0) {
                current = current.childs.get(0);
            }
            commonRoot = current;
        }
    }
}