/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask1;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author brokenhead
 */
public class Group {

    private final List<LinkedList<Long>> rowList = new LinkedList();

    public Group(LinkedList<Long> arrr) {
        this.addRow(arrr);
    }

    public Integer getGroupSize() {
        return rowList.size();
    }

    public void addRow(LinkedList<Long> arr) {
        rowList.add(arr);
    }

    public void addAllRows(List<LinkedList<Long>> arr) {
        rowList.addAll(arr);
    }

    public List<LinkedList<Long>> getRowList() {
        return rowList;
    }

    public boolean containsRow(LinkedList<Long> rowToCheck) {
        boolean fullyEqual = false;
        int matchCount = 0;
        for (LinkedList<Long> str : rowList) {
            if (rowToCheck.size() == str.size()) {
                for (int i = 0; i < str.size(); i++) {
                    if (str.get(i).equals(rowToCheck.get(i))){
                        matchCount++;
                    }
                }
                if(matchCount == rowToCheck.size()) {
                   fullyEqual = true; 
                }
            }
        }
        return fullyEqual;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.rowList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if (!Objects.equals(this.rowList, other.rowList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        for (LinkedList<Long> ib : rowList) {
            for (int i = 0; i < ib.size(); i++) {
                strb.append(ib.get(i)).append(";");
            }
            strb.append("\n");
        }
        return strb.toString();
    }

}
