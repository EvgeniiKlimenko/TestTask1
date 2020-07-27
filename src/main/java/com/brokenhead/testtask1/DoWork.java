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
import java.util.Collection;
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
        try {
            File requestedFile = new File(INPUT_FILE_PATH);
            FileReader fr = new FileReader(requestedFile);
            BufferedReader br = new BufferedReader(fr);
            try {
                int count = 0;
                while (count < 1000007) {
                    LinkedList<Long> numNum = new LinkedList();
                    String lineRead = br.readLine();
                    if (!lineRead.contains(";")) {
                        System.out.println("------ skip string " + count +". no ;");
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
                                    numNum.add(Long.parseLong(lineRead, st, en, 10));
                                } catch (NumberFormatException numex) {
                                    System.out.println("Bad string value: " + lineRead);
                                }
                            } else { // "645674876... Beginning of the value
                                st = i + 1;
                            }
                        } else if (lineRead.charAt(i) == QUAT &&  // quat and end of string: ...6545"
                                   i == lineRead.length() - 1 && 
                                   lineRead.charAt(i - 1) != QUAT) {
                            en = i;
                            numNum.add(Long.parseLong(lineRead, st, en, 10));
                        }
                    }
                    searchInGroups(numNum);
                    count++;
                }
            } catch (IOException iox) {
                System.out.println("IOExceptiom Damn!");
            }
        } catch (FileNotFoundException exc) {
            System.out.println("FileNotFound Damn!");
        }
        
        // Get distinct values using Set; sort values; get reversed order:
        HashSet<Group> setGrp = new HashSet(megaMap.values());
        List<Group> valuesOut = new LinkedList(setGrp);
        Comparator<Group> comp = (grp1, grp2) -> grp1.getGroupSize().compareTo(grp2.getGroupSize());
        valuesOut.sort(comp);
        Collections.reverse(valuesOut);
        
        int count = 0;
        System.out.println("List size " + valuesOut.size());
        for(Group grp : valuesOut) {
            int size = grp.getGroupSize();
            if(size > 1){
                count++;
            }
        }
        
    }

    
    private void searchInGroups(LinkedList<Long> numNum) {
        List<Group> matchedGroups = new LinkedList();
        List<Integer> matchedIndexes = new LinkedList();
        for (int i = 0; i < numNum.size(); i++) {
            if (megaMap.containsKey(numNum.get(i))) {
                matchedGroups.add(megaMap.get(numNum.get(i)));
                matchedIndexes.add(i);
            }
        }
        if (matchedGroups.isEmpty()) {         // no groups found
            Group grp = new Group(numNum);
            for (int i = 0; i < numNum.size(); i++) {
                megaMap.put(numNum.get(i), grp);
            }
        } else if (matchedGroups.size() == 1) { // one group found
            Group grp = matchedGroups.get(0);
            grp.addString(numNum);
            saveUnmatched(matchedIndexes, numNum, grp);

        } else {                      // more than one found. Merge to first
            Group grp = matchedGroups.get(0);
            grp.addString(numNum);
            saveUnmatched(matchedIndexes, numNum, grp);
            mergeGroups(matchedGroups, grp);
        }
    }

    
    private void saveUnmatched(List<Integer> matchedIndexes, LinkedList<Long> values, Group grp) {
        for (int i = 0; i < matchedIndexes.size(); i++) {
            for (int j = 0; j < values.size(); j++) {
                if (matchedIndexes.get(i) != j) {
                    megaMap.put(values.get(j), grp);
                }
            }
        }
    }

    private void mergeGroups(List<Group> matchedGroups, Group firstGrp) {
        for (int i = 1; i < matchedGroups.size(); i++) {
            List<LinkedList<Long>> stringList = matchedGroups.get(i).getStringList();
            firstGrp.addAllStrings(stringList);
            stringList.forEach(lnLs -> {
                for (int k = 0; k < lnLs.size(); k++) {
                    megaMap.put(lnLs.get(k), firstGrp);
                }
            });
        }
    }
    
    
}
