package com.mbzshajib.mining.processor.uncertain.model;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email  - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/21/2015
 * @time: 3:58 PM
 * ****************************************************************
 */


public class HeaderTable {
    private int windowSize;
    private List<HeaderTableItem> headerTableItems;

    public HeaderTable(int windowSize) {
        this.windowSize = windowSize;
        headerTableItems = new ArrayList<HeaderTableItem>();
    }

    public void updateHeaderTable(UNode uNode) throws DataNotValidException {
        HeaderTableItem item = null;
        if ((item = findHeaderTableItemById(uNode.getId())) == null) {
            addNewTableItem(uNode.getId());
            HeaderTableItem headerTableItem = findHeaderTableItemById(uNode.getId());
            headerTableItem.updateHeaderData(uNode);

        } else {
            item.updateHeaderData(uNode);
        }
    }
    public void slideHeaderTable() {
        for (HeaderTableItem item : headerTableItems) {
            List<UNode> nodes = item.getNodeList();
            int counter = nodes.size();
            for (int i = 0; i < counter; i++) {
                UNode node = nodes.get(i);
                boolean isEmpty = true;
                for (UData data : node.getUncertainDataList()) {
                    if (data.getItemProbability() > 0) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty) {
                    nodes.remove(node);
                    counter = counter - 1;
                    i = -1;

                }
            }
        }
    }

    private void addNewTableItem(String id) {
        HeaderTableItem headerTableItem = new HeaderTableItem(id);
        headerTableItems.add(headerTableItem);
    }

    private HeaderTableItem findHeaderTableItemById(String id) {
        HeaderTableItem result = null;
        for (HeaderTableItem item : headerTableItems) {
            if (item.getItemId().equalsIgnoreCase(id)) {
                result = item;
            }
        }
        return result;
    }

    public String traverse() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.NEW_LINE);
        stringBuilder.append("HEADER TABLE");
        stringBuilder.append(Constant.NEW_LINE);
        for (HeaderTableItem item : headerTableItems) {
            stringBuilder.append(item.traverse());
            stringBuilder.append(Constant.NEW_LINE);
        }
        return stringBuilder.toString();
    }

}
