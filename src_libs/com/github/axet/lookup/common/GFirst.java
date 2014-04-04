package com.github.axet.lookup.common;

import java.util.Comparator;

public class GFirst implements Comparator<GPoint> {

    @Override
    public int compare(GPoint arg0, GPoint arg1) {
        int r = LessCompare.compareBigFirst(arg0.g, arg1.g);

        return r;
    }
}