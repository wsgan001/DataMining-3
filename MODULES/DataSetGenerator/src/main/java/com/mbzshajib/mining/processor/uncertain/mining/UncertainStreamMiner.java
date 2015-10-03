package com.mbzshajib.mining.processor.uncertain.mining;

import com.mbzshajib.mining.exception.DataNotValidException;
import com.mbzshajib.mining.processor.uncertain.model.HTableItemInfo;
import com.mbzshajib.mining.processor.uncertain.model.HeaderTable;
import com.mbzshajib.mining.processor.uncertain.model.UNode;
import com.mbzshajib.mining.processor.uncertain.model.UncertainTree;
import com.mbzshajib.mining.util.Constant;
import com.mbzshajib.mining.util.Utils;
import com.mbzshajib.utility.model.ProcessingError;
import com.mbzshajib.utility.model.Processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * *****************************************************************
 * Copyright  2015.
 *
 * @author - Md. Badi-Uz-Zaman Shajib
 * @email - mbzshajib@gmail.com
 * @gitHub - https://github.com/mbzshajib
 * @date: 9/28/2015
 * @time: 7:46 PM
 * ****************************************************************
 */

public class UncertainStreamMiner implements Processor<UncertainStreamMineInput, UncertainStreamMineOutput> {
    public static final String TAG = UncertainStreamMiner.class.getCanonicalName();

    @Override
    public UncertainStreamMineOutput process(UncertainStreamMineInput uncertainStreamMineInput) throws ProcessingError {
        UncertainTree uncertainTree = uncertainStreamMineInput.getUncertainTree();
//        printBeforeMining(uncertainTree);
        UNode rootNode = uncertainTree.getRootNode();
        HeaderTable headerTable = uncertainTree.getHeaderTable();
        double minSupport = uncertainStreamMineInput.getMinSupport();
        List<HTableItemInfo> HTableItemInfoList = headerTable.getHeaderItemInfo();
        removeDataBelowSupport(minSupport, HTableItemInfoList);
        sortByPrefix(HTableItemInfoList);
        try {
            startMining(uncertainTree, HTableItemInfoList);
        } catch (DataNotValidException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startMining(UncertainTree uncertainTree, List<HTableItemInfo> hTableItemInfoList) throws DataNotValidException {
        UncertainTree copiedTree = uncertainTree.copy();
        //TODO:LOOP
        mine(copiedTree.getRootNode(), copiedTree.getHeaderTable().getWindowSize(), "7");

//        for (int index = HTableItemInfoList.size() - 1; index >= 0; index--) {
//            HTableItemInfo HTableItemInfo = HTableItemInfoList.get(index);
//            List<UNode> nodeList = headerTable.getAllNodesOfHeaderTableItem(HTableItemInfo.getItemId());
//            mine(rootNode, "1");
//
//        }
    }

    private void mine(UNode rootNode, int windowSize, String id) throws DataNotValidException {
        constructConditionalTree(rootNode, id);
        HeaderTable headerTable = generateHeaderTableForCondTree(rootNode, windowSize);
        System.out.println(rootNode.traverse());
        rootNode.removeNodeIfChildOfAnyDepthById("4");
        System.out.println(rootNode.traverse());
//        System.out.println();
//        System.out.println();
//        System.out.println(headerTable.traverse());
        //TODO: Mine SUbTree;
    }

    private HeaderTable generateHeaderTableForCondTree(UNode rootNode, int windowSize) throws DataNotValidException {
        List<UNode> distinctList = rootNode.getDistinctNodes();

        HeaderTable headerTable = new HeaderTable(windowSize);
        headerTable.updateHeaderTableFromDistinctList(distinctList);
        return headerTable;
    }


    private UNode constructConditionalTree(UNode node, String id) {
        if (node.getId().equalsIgnoreCase(id)) {
            node.setChildNodeList(new ArrayList<UNode>());
            return node;
        } else {
            if (node.getChildNodeList() == null || node.getChildNodeList().isEmpty()) {
                return null;
            } else {
                UNode returnNode = null;
                int count = node.getChildNodeList().size();
                for (int index = 0; index < count; index++) {
                    UNode child = node.getChildNodeList().get(index);
                    UNode foundNode = constructConditionalTree(child, id);
                    if (foundNode == null) {
                        node.removeChildNode(child);
                        index--;
                        count--;
                    } else {
                        returnNode = foundNode;
                    }
                }
                return returnNode;
            }
        }
    }

    private void removeDataBelowSupport(double minSupport, List<HTableItemInfo> HTableItemInfoList) {
        removePrefixDataBelowSupport(HTableItemInfoList, minSupport);
        removeProbabilityDataBelowSupport(HTableItemInfoList, minSupport);
    }

    private void removePrefixDataBelowSupport(List<HTableItemInfo> HTableItemInfoList, double minSupport) {
        for (int index = 0; index < HTableItemInfoList.size(); index++) {
            HTableItemInfo HTableItemInfo = HTableItemInfoList.get(index);
            if (HTableItemInfo.getItemPrefixValue() < minSupport) {
                HTableItemInfoList.remove(HTableItemInfo);
            }
        }
    }

    private void removeProbabilityDataBelowSupport(List<HTableItemInfo> HTableItemInfoList, double minSupport) {
        for (int index = 0; index < HTableItemInfoList.size(); index++) {
            HTableItemInfo HTableItemInfo = HTableItemInfoList.get(index);
            if (HTableItemInfo.getItemProbabilityValue() < minSupport) {
                HTableItemInfoList.remove(HTableItemInfo);
            }
        }
    }

    private void sortByID(List<HTableItemInfo> sortedFilteredById) {
        Collections.sort(sortedFilteredById, new Comparator<HTableItemInfo>() {
            @Override
            public int compare(HTableItemInfo one, HTableItemInfo two) {
                int idOne = Integer.parseInt(one.getItemId());
                int idTwo = Integer.parseInt(two.getItemId());
                int diff = idTwo - idOne;
                return diff;
            }
        });

    }

    private void sortByPrefix(List<HTableItemInfo> sortedFilteredById) {
        Collections.sort(sortedFilteredById, new Comparator<HTableItemInfo>() {
            @Override
            public int compare(HTableItemInfo one, HTableItemInfo two) {
                double diff = two.getItemPrefixValue() - one.getItemPrefixValue();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

    }

    private void sortByProbability(List<HTableItemInfo> sortedFilteredById) {
        Collections.sort(sortedFilteredById, new Comparator<HTableItemInfo>() {
            @Override
            public int compare(HTableItemInfo one, HTableItemInfo two) {
                double diff = two.getItemProbabilityValue() - one.getItemProbabilityValue();
                if (diff == 0) {
                    return 0;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

    }

    @Deprecated
    private UNode constructConditionalTreeOld(UNode root, String id) {
        int count = root.getChildNodeList().size();
        for (int index = 0; index < count; index++) {
            UNode child = root.getChildNodeList().get(index);
            UNode foundNode = constructConditionalTree(child, id);
            if (foundNode == null) {
                root.removeChildNode(child);
                index--;
                count--;
            }
        }
        return root;
    }

    private void printBeforeMining(UncertainTree uncertainTree) {

        Utils.log(Constant.MULTI_STAR);
        Utils.log(TAG, "Tree For Mining");
        Utils.log(Constant.MULTI_STAR);
        Utils.log(TAG, uncertainTree.getTraversedString());
        Utils.log(Constant.MULTI_STAR);
        Utils.log(TAG, "Mining Started");
        Utils.log(Constant.MULTI_STAR);
    }
}