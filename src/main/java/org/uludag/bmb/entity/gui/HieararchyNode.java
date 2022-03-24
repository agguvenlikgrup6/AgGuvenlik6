package org.uludag.bmb.entity.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HieararchyNode {
    public List<HieararchyNode> childs;
    public String data;
    public String incrementalPath;

    public HieararchyNode(String nodeValue, String incrementalPath) {
        childs = new ArrayList<HieararchyNode>();
        data = nodeValue;
        this.incrementalPath = incrementalPath;
    }

    public void addElement(String currentPath, String[] list) {
        if (list[0] == null || list[0].equals(""))
            list = Arrays.copyOfRange(list, 1, list.length);

        HieararchyNode currentChild = new HieararchyNode(list[0], currentPath + "/" + list[0]);
        if (list.length == 1) {
            childs.add(currentChild);
            return;
        } else {
            int index = childs.indexOf(currentChild);
            if (index == -1) {
                childs.add(currentChild);
                currentChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
            } else {
                HieararchyNode nextChild = childs.get(index);
                nextChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        HieararchyNode cmpObj = (HieararchyNode) obj;
        return incrementalPath.equals(cmpObj.incrementalPath) && data.equals(cmpObj.data);
    }

    @Override
    public String toString() {
        return data;
    }
}