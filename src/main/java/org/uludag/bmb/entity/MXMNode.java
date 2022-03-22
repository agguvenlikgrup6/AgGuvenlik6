package org.uludag.bmb.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MXMNode {

    public List<MXMNode> childs;
    public List<MXMNode> leafs;
    public String data;
    public String incrementalPath;

    public MXMNode(String nodeValue, String incrementalPath) {
        childs = new ArrayList<MXMNode>();
        leafs = new ArrayList<MXMNode>();
        data = nodeValue;
        this.incrementalPath = incrementalPath;
    }

    public boolean isLeaf() {
        return childs.isEmpty() && leafs.isEmpty();
    }

    public void addElement(String currentPath, String[] list) {
        while (list[0] == null || list[0].equals(""))
            list = Arrays.copyOfRange(list, 1, list.length);

        MXMNode currentChild = new MXMNode(list[0], currentPath + "/" + list[0]);
        if (list.length == 1) {
            leafs.add(currentChild);
            return;
        } else {
            int index = childs.indexOf(currentChild);
            if (index == -1) {
                childs.add(currentChild);
                currentChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
            } else {
                MXMNode nextChild = childs.get(index);
                nextChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        MXMNode cmpObj = (MXMNode) obj;
        return incrementalPath.equals(cmpObj.incrementalPath) && data.equals(cmpObj.data);
    }

    public void printNode(int increment) {
        for (int i = 0; i < increment; i++) {
            System.out.print(" ");
        }
        System.out.println(incrementalPath + "/");
        for (MXMNode n : childs)
            n.printNode(increment + 2);
        for (MXMNode n : leafs)
            n.printNode(increment + 2);
    }

    @Override
    public String toString() {
        return data;
    }
}