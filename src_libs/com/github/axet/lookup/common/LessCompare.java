package com.github.axet.lookup.common;

/**
 * Java Comparator has compare method with 'int compare(o1, o2)' result value.
 * You should read ita as less algorithm, who is less?
 * 
 * o1 < o2
 * 
 * -1 points to low value for o1. 0 points for middle / equals. 1 points for o2
 * low value
 * 
 * if you need opposite (desk order) return opposite values.
 * 
 * @author axet
 * 
 */
public class LessCompare {

    static public int compareBigFirst(double o1, double o2, double val) {
        if (Math.abs(Math.abs(o1) - Math.abs(o2)) < val)
            return 0;

        return compareBigFirst(o1, o2);
    }

    // desc algorithm (high comes at first [0])
    static public int compareBigFirst(int o1, int o2) {
        return new Integer(o2).compareTo(new Integer(o1));
    }

    // desc algorithm (high comes at first [0])
    static public int compareBigFirst(float o1, float o2) {
        return new Float(o2).compareTo(new Float(o1));
    }

    static public int compareBigFirst(double o1, double o2) {
        return new Double(o2).compareTo(new Double(o1));
    }

    // asc algorithm (low comes at first [0])
    static public int compareSmallFirst(int o1, int o2) {
        return new Integer(o1).compareTo(new Integer(o2));
    }

    static public int compareBigFirst(int o1, int o2, int val) {
        if (Math.abs(Math.abs(o1) - Math.abs(o2)) < val)
            return 0;

        return compareBigFirst(o1, o2);
    }

    static public int compareSmallFirst(double o1, double o2) {
        return new Double(o1).compareTo(new Double(o2));
    }

}
