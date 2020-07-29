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
 * "A1";"";"" is correct
 *
 * @author brokenhead
 */
public class DoWork {

    private final static String INPUT_FILE_PATH = "FilesF/lng.csv";
    private final static String OUTPUT_FILE_PATH = "FilesF/AppOut.txt";
    private final HashMap<Long, Group> megaMap = new HashMap();
    private final static String QUOTE = "\"";
    private final static String SEMI_COL = ";";
    public final static Long EMPTY_VALUE = new Long(-1); // represents this: "";

    public void doApp() {
        File requestedFile = new File(INPUT_FILE_PATH);
        File resultFile = new File(OUTPUT_FILE_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(requestedFile))) {
            int count = 0;
            String lineRead;
            while ((lineRead = br.readLine()) != null) {
                //Parse string:
                LinkedList<Long> currentRow = new LinkedList();
                String[] valuesAr = lineRead.split(SEMI_COL);
                
                if(valuesAr.length != 3) {
                    continue;
                }
                for(int i = 0; i<valuesAr.length; i++) {
                    if(valuesAr[i].indexOf(QUOTE, 1) != (valuesAr[i].length()-1)) { // "6546"5645645"    
                        continue;
                    }
                    if(valuesAr[i].length() < 4) {    // ""; empty value
                        currentRow.add(EMPTY_VALUE);
                        continue;
                    }
                    currentRow.add(Long.parseLong(valuesAr[i].replaceAll(QUOTE, "")));
                }
                searchInGroups(currentRow);
                count++;
            }
        } catch (FileNotFoundException exc) {
            System.out.println("FileNotFoundException!");
        } catch (IOException iox) {
            System.out.println("IOException!");
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
        System.out.println("Groups with more than one values: " + count);
        
        // Write result to file        
        try (PrintWriter writer = new PrintWriter(resultFile, "UTF-8");) {
            writer.println("Groups with more than 1 element: " + count + "\n");
            int groupIndex = 0;
            for (Group grp : valuesOut) {
                if (grp.getGroupSize() > 1) {
                    writer.println("Group" + groupIndex);
                    writer.println(grp.toString());
                    groupIndex++;
                }
            }
        } catch (IOException iox) {
            System.out.println("IOException on write!");
        }
    }

    private void searchInGroups(LinkedList<Long> currentRow) {
        List<Group> matchedGroups = new LinkedList();
        List<Integer> matchedIndexes = new LinkedList();
        for (int i = 0; i < currentRow.size(); i++) {
            if (!currentRow.get(i).equals(EMPTY_VALUE) && megaMap.containsKey(currentRow.get(i))) {
                Group foundGroup = megaMap.get(currentRow.get(i));
                if (matchedGroups.contains(foundGroup) || foundGroup.containsRow(currentRow)) {
                    continue;
                }
                matchedGroups.add(foundGroup);
                matchedIndexes.add(i);
            }
        }
        if (matchedGroups.isEmpty()) {         // no groups found
            Group grp = new Group(currentRow);
            for (int i = 0; i < currentRow.size(); i++) {
                if (!currentRow.get(i).equals(EMPTY_VALUE)) {
                    megaMap.put(currentRow.get(i), grp);
                }
            }
        } else if (matchedGroups.size() == 1) { // one group found
            Group grp = matchedGroups.get(0);
            grp.addRow(currentRow);
            saveUnmatched(matchedIndexes, currentRow, grp);
        } else {                        //more than one found
            Group firstGroup = matchedGroups.get(0);
            mergeGroups(matchedGroups, firstGroup);
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
            rowList.forEach(row -> {
                for (int k = 0; k < row.size(); k++) {
                    megaMap.put(row.get(k), firstGrp);  // override key with new value
                }
            });
        }
    }

}
