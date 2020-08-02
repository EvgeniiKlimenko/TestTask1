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

    private final List<LinkedList<String>> rowList = new LinkedList();

    public Group(LinkedList<String> arrr) {
        this.addRow(arrr);
    }

    public Integer getGroupSize() {
        return rowList.size();
    }

    public void addRow(LinkedList<String> arr) {
        rowList.add(arr);
    }

    public void addAllRows(List<LinkedList<String>> arr) {
        rowList.addAll(arr);
    }

    public List<LinkedList<String>> getRowList() {
        return rowList;
    }

    public boolean containsRow(LinkedList<String> rowToCheck) {
        for (LinkedList<String> row : rowList) {
            int matchCount = 0;
            if (rowToCheck.size() == row.size()) {
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).equals(rowToCheck.get(i))) {
                        matchCount++;
                    }
                }
                if (matchCount == rowToCheck.size()) {
                    return true;
                }
            }
        }
        return false;
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
        for (LinkedList<String> row : rowList) {
            for (int i = 0; i < row.size(); i++) {
               if(!row.get(i).equals(DoWork.EMPTY_VALUE))
                   strb.append(row.get(i));
               
               if(i != row.size() - 1)
                   strb.append(";");
            }
            strb.append("\n");
        }   
        return strb.toString();
    }

}
