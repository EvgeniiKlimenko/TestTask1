/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * 1444 105036
 *
 * @author brokenhead
 */
public class DoWork {

    private final static String INPUT_FILE_PATH = "FilesF/lng-big.csv";
    private final static String OUTPUT_FILE_PATH = "FilesF/AppOut.txt";
    private HashMap<String, LinkedList<RowWrapper>> megaMap = new HashMap();    
    private final static String QUOTE = "\"";
    private final static String SEMI_COL = ";";
    public final static String EMPTY_VALUE = "none"; // represents this: "";

    public void doApp() {
        File requestedFile = new File(INPUT_FILE_PATH);
        File resultFile = new File(OUTPUT_FILE_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(requestedFile))) {
            String lineRead;
            while ((lineRead = br.readLine()) != null) {
                //Parse string:
                ArrayList<String> currentRow = new ArrayList();
                String[] valuesAr = lineRead.split(SEMI_COL);
                if (valuesAr.length != 3) {
                    continue;
                }
                boolean hasParseError = false;
                for (int i = 0; i < valuesAr.length; i++) {
                    if (valuesAr[i].indexOf(QUOTE, 1) != (valuesAr[i].length() - 1)) { // "6546"5645645"  
                        hasParseError = true;
                        break;
                    }
                    if (valuesAr[i].length() < 3) {    // ""; empty value
                        currentRow.add(EMPTY_VALUE);
                        continue;
                    }
                    currentRow.add(valuesAr[i].replaceAll(QUOTE, ""));
                }
                if (!hasParseError) {
                    searchInGroups(currentRow);
                }
            }
        } catch (FileNotFoundException exc) {
            System.out.println("FileNotFoundException!");
        } catch (IOException iox) {
            System.out.println("IOException!");
        }
        // Get distinct values using Set; sort values; get reversed order:
        // HashMap<topHeads, listOfValues>
        // This map contains only topHeads rows as a key!
        HashMap<RowWrapper, LinkedList<RowWrapper>> outputMap = new HashMap();
        HashSet<LinkedList<RowWrapper>> onlyWrappersList = new HashSet(megaMap.values());
        // clean up!
        megaMap.clear();
        megaMap = null;
        System.gc();
        
        for (LinkedList<RowWrapper> group : onlyWrappersList) {
            RowWrapper localHead = group.get(0);
            if (localHead.isTopHead() && !outputMap.containsKey(localHead)) {   // topHead && notInMap
                outputMap.put(localHead, group);
            } else { // not topHead
                RowWrapper topHead = findTopHead(localHead);
                if(outputMap.containsKey(topHead)) {
                    LinkedList<RowWrapper> rowsList = outputMap.get(topHead);
                    rowsList.addAll(group);
                } else {
                    outputMap.put(topHead, group);
                }
            }
        }
        
        Comparator<LinkedList<RowWrapper>> comp = (grp1, grp2) -> {
            Integer int1 = grp1.size();
            Integer int2 = grp2.size();
            return int1.compareTo(int2);
        };
        
        onlyWrappersList.clear();
        LinkedList<LinkedList<RowWrapper>> valuesOut = new LinkedList(outputMap.values());
        // clean up!
        outputMap.clear();
        outputMap = null;
        // Sort and reverse 
        valuesOut.sort(comp);
        Collections.reverse(valuesOut);

        // Count groups
        int count = 0;
        for (LinkedList<RowWrapper> grp : valuesOut) {
            int size = grp.size();
            if (size > 1) {
                count++;
            }
        }
        System.out.println("Groups with more than one values: " + count);

        // Write result to file      
        try (PrintWriter writer = new PrintWriter(resultFile, "UTF-8");) {
            writer.println("Groups with more than 1 element: " + count + "\n");
            int groupIndex = 0;
            for (LinkedList<RowWrapper> grp : valuesOut) {
                if (grp.size() > 1) {
                    writer.println("\nGroup" + groupIndex);
                    for (RowWrapper row : grp) {
                        writer.print(row.toString());
                    }
                    groupIndex++;
                }
            }
        } catch (IOException iox) {
            System.out.println("IOException on write!");
        }
        /**/
    }

    private void searchInGroups(ArrayList<String> currentRow) {
        LinkedList<LinkedList<RowWrapper>> wrappersLists = new LinkedList();
        List<Integer> matchedIndexes = new LinkedList();
        for (int i = 0; i < currentRow.size(); i++) {
            String currentVal = currentRow.get(i);
            if (!currentVal.equals(EMPTY_VALUE) && megaMap.containsKey(currentVal)) {
                LinkedList<RowWrapper> found = megaMap.get(currentVal); //get group(wrappers list)
                for (RowWrapper rwr : found) { // check every row for fully match with current row
                    if (rwr.isFullyMatch(currentRow)) { // duplicate found
                        return;
                    }
                }
                if (!wrappersLists.contains(found)) { //check for wrappersLists is already contains this list
                    wrappersLists.add(found);
                    matchedIndexes.add(i);
                }
            }
        }
        if (wrappersLists.isEmpty()) {         // no matches found
            RowWrapper newRow = new RowWrapper(currentRow);
            LinkedList<RowWrapper> lst = new LinkedList();
            newRow.setAsTopHead(); // first element in list is always a head. Local or Top
            lst.add(newRow);
            for (int i = 0; i < currentRow.size(); i++) {
                String val = currentRow.get(i);
                if (!val.equals(EMPTY_VALUE)) {
                    megaMap.put(val, lst);
                }
            }
        } else if (wrappersLists.size() == 1) { // one group found
            RowWrapper newRow = new RowWrapper(currentRow);
            LinkedList<RowWrapper> lst = wrappersLists.get(0);
            newRow.setHead(lst.getFirst()); // point to Local head
            lst.addLast(newRow);            // place newRow to the end
            saveUnmatched(matchedIndexes, currentRow, lst);
        } else {                                //more than one found
            RowWrapper newRow = new RowWrapper(currentRow);
            LinkedList<RowWrapper> firstGroup = wrappersLists.get(0);
            newRow.setHead(firstGroup.getFirst());
            firstGroup.addLast(newRow); // add a new row to the first group that has been found
            saveUnmatched(matchedIndexes, currentRow, firstGroup); 

            mergeGroups(wrappersLists, firstGroup);
        }
    }

    private void saveUnmatched(List<Integer> matchedIndexes, ArrayList<String> currentRow, LinkedList<RowWrapper> lst) {
        for (int j = 0; j < currentRow.size(); j++) {
            if (!matchedIndexes.contains(j) && !currentRow.get(j).equals(EMPTY_VALUE)) {
                megaMap.put(currentRow.get(j), lst);
            }
        }
    }

    private void mergeGroups(LinkedList<LinkedList<RowWrapper>> matchedGroups, LinkedList<RowWrapper> firstGroup) {
        RowWrapper topHeadFirst = findTopHead(firstGroup.get(0));
        for (int i = 1; i < matchedGroups.size(); i++) {
            //printGroup(matchedGroups.get(i));

            RowWrapper topHeadOther = findTopHead(matchedGroups.get(i).getFirst()); // pass local head to method
            if (topHeadFirst.equals(topHeadOther)) { //different lists already have the same topHead
                continue;
            } else {
                topHeadOther.dropTopHeadStatus();
                topHeadOther.setHead(topHeadFirst);
            }
        }
    }

    private RowWrapper findTopHead(RowWrapper localHead) {
        RowWrapper topHead = localHead;
        while (!topHead.isTopHead()) {
            topHead = topHead.getHead();
        }
        return topHead;
    }

}
