/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask1;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * @author brokenhead
 */
public class Group {
    private List<LinkedList<Long>> stringList = new LinkedList();
    
    public Group(LinkedList<Long> arrr){
        this.addString(arrr);
    }
    
    public Integer getGroupSize(){
        return stringList.size();
    }
    
    public void addString(LinkedList<Long> arr){
        stringList.add(arr);
    }
    
    public void addAllStrings(List<LinkedList<Long>> arr){
        stringList.addAll(arr);
    }

    public List<LinkedList<Long>> getStringList() {
        return stringList;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.stringList);
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
        if (!Objects.equals(this.stringList, other.stringList)) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        for(LinkedList<Long> ib : stringList) {
            for(int i = 0; i < ib.size(); i++) {
                strb.append(ib.get(i)).append(";");
            }
            strb.append("\n");
        }
        return strb.toString();
    }
    
    
}
