package com.mbzshajib.mining.processor.uncertain.model;

import com.mbzshajib.mining.exception.DataNotValidException;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 * Author - Md. Badi-Uz-Zaman Shajib
 * Email  - mbzshajib@gmail.com
 * GitHub - https://github.com/mbzshajib
 * date: 9/21/2015
 * time: 3:58 PM
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

    private void addNewTableItem(String id) {
        HeaderTableItem headerTableItem = new HeaderTableItem(id, windowSize);
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

    public void slideWindowAndUpdateTable() {
        for (HeaderTableItem item : headerTableItems) {
            item.slideWindowAndUpdateItem();
        }
    }

}
