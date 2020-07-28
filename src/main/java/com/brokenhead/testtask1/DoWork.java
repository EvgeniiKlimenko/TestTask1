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

/**
 *
 *
 * @author brokenhead
 */
public class DoWork {

    public final String INPUT_FILE_PATH = "FilesF/lng.csv";
    public final String OUTPUT_FILE_PATH = "FilesF/AppOut.txt";
    private final HashMap<Long, Group> megaMap = new HashMap();
    private final char QUAT = '"';
    private final char SEMI_COL = ';';

    public void doApp() {
            File requestedFile = new File(INPUT_FILE_PATH);
            File resultFile = new File(OUTPUT_FILE_PATH);
        try {
            FileReader fr = new FileReader(requestedFile);
            try(BufferedReader br = new BufferedReader(fr)) {
                String lineRead; 
                while ((lineRead = br.readLine()) != null) {
                    LinkedList<Long> currentRow = new LinkedList();
                    lineRead = br.readLine();
                    if (!lineRead.contains(";")) {
                        System.out.println("------ skip string. no ;");
                        continue;
                    }
                    //Parse string:
                    int st = 0, en = 0;
                    for (int i = 0; i < lineRead.length(); i++) {
                        //found quat and not end of string
                        if (lineRead.charAt(i) == QUAT && i != lineRead.length() - 1) {
                            //end of a value
                            if (lineRead.charAt(i + 1) == SEMI_COL && lineRead.charAt(i - 1) != QUAT) {
                                en = i;
                                try {
                                    currentRow.add(Long.parseLong(lineRead, st, en, 10));
                                } catch (NumberFormatException numex) {
                                    System.out.println("Bad raw value: " + lineRead);
                                }
                            } else { // "645674876... Beginning of the value
                                st = i + 1;
                            }
                        } else if (lineRead.charAt(i) == QUAT
                                && // quat and end of string: ...6545"
                                i == lineRead.length() - 1
                                && lineRead.charAt(i - 1) != QUAT) {
                            en = i;
                            currentRow.add(Long.parseLong(lineRead, st, en, 10));
                        }
                    }
                    searchInGroups(currentRow);
                }
            } catch (IOException iox) {
                System.out.println("IOException!");
            }
        } catch (FileNotFoundException exc) {
            System.out.println("FileNotFoundException!");
        }

        // Get distinct values using Set; sort values; get reversed order:
        HashSet<Group> setGrp = new HashSet(megaMap.values());
        List<Group> valuesOut = new LinkedList(setGrp);
        Comparator<Group> comp = (grp1, grp2) -> grp1.getGroupSize().compareTo(grp2.getGroupSize());
        valuesOut.sort(comp);
        Collections.reverse(valuesOut);
        // Count groups
        int count = 0;
        for (Group grp : valuesOut) {
            int size = grp.getGroupSize();
            if (size > 1) {
                count++;
            }
        }
        // Write result to file
        /**/
        try(PrintWriter writer = new PrintWriter(resultFile, "UTF-8");) {
            writer.println("Groups with more than 1 element: " + count + "\n");
            int groupIndex = 0;
            for (Group grp : valuesOut) {
                if(grp.getGroupSize() > 1){
                    writer.println("Group" + groupIndex);
                    writer.println(grp.toString());
                    groupIndex++;
                }
            }
        } catch (IOException iox) {
            System.out.println("IOExceptiom on write!");
        }
        
    }

    private void searchInGroups(LinkedList<Long> currentRow) {
        List<Group> matchedGroups = new LinkedList();
        List<Integer> matchedIndexes = new LinkedList();
        for (int i = 0; i < currentRow.size(); i++) {
            if (megaMap.containsKey(currentRow.get(i))) {
                Group foundGroup = megaMap.get(currentRow.get(i));
                if(matchedGroups.contains(foundGroup) || foundGroup.containsRow(currentRow)) {
                        continue;
                }
                matchedGroups.add(foundGroup);
                matchedIndexes.add(i);
            }
        }
        if (matchedGroups.isEmpty()) {         // no groups found
            Group grp = new Group(currentRow);
            for (int i = 0; i < currentRow.size(); i++) {
                megaMap.put(currentRow.get(i), grp);
            }
        } else if (matchedGroups.size() == 1) { // one group found
            Group grp = matchedGroups.get(0);
            grp.addRow(currentRow);
            saveUnmatched(matchedIndexes, currentRow, grp);
        } else {                        //more than one found
            Group firstGroup = matchedGroups.get(0);
            if(matchedGroups.size() == 1){
                firstGroup.addRow(currentRow); 
                saveUnmatched(matchedIndexes, currentRow, firstGroup);
            } else {
                mergeGroups(matchedGroups, firstGroup);
            }
        }
    
    }

    private void saveUnmatched(List<Integer> matchedIndexes, LinkedList<Long> currentRow, Group firstGroup) {
        for (int i = 0; i < matchedIndexes.size(); i++) {
            for (int j = 0; j < currentRow.size(); j++) {
                if (matchedIndexes.get(i) != j) {
                    megaMap.put(currentRow.get(j), firstGroup);
                }
            }
        }
    }

    private void mergeGroups(List<Group> matchedGroups, Group firstGrp) {
        for (int i = 1; i < matchedGroups.size(); i++) {
            List<LinkedList<Long>> rowList = matchedGroups.get(i).getRowList();
            firstGrp.addAllRows(rowList);
            rowList.forEach(lnLs -> {
                for (int k = 0; k < lnLs.size(); k++) {
                    megaMap.put(lnLs.get(k), firstGrp);
                }
            });
        }
    }
    
}
