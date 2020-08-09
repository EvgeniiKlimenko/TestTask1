/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask1;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author brokenhead
 */
public class RowWrapper {

    private RowWrapper head;
    private boolean isTopHead;
    private final ArrayList<String> rawItself;

    public RowWrapper(ArrayList<String> newRow) {
        isTopHead = false;
        rawItself = newRow;
        head = null;
    }

    public RowWrapper getHead() {
        return head;
    }

    public void setAsTopHead() {
        isTopHead = true;
    }

    /**
     * Used when merge groups
     */
    public void dropTopHeadStatus() {
        isTopHead = false;
    }

    public void setHead(RowWrapper newHead) {
        head = newHead;
    }

    public boolean isTopHead() {
        return isTopHead;
    }

    public boolean isFullyMatch(ArrayList<String> rowToCheck) {
        int matchCount = 0;
        for (int i = 0; i < rawItself.size(); i++) {
            if (rawItself.get(i).equals(rowToCheck.get(i))) { //this row's first value equals other row's first value
                matchCount++;
            }
        }
        return matchCount == rowToCheck.size(); // matchCount == 3 means fully match of rows
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        for (int i = 0; i < rawItself.size(); i++) {
            if (!rawItself.get(i).equals(DoWork.EMPTY_VALUE)) {
                strb.append(rawItself.get(i));
            }

            if (i != rawItself.size() - 1) {
                strb.append(";");
            }
        }
        strb.append("\n");
        return strb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.rawItself);
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
        final RowWrapper other = (RowWrapper) obj;
        if (!Objects.equals(this.rawItself, other.rawItself)) {
            return false;
        }
        return true;
    }

}
