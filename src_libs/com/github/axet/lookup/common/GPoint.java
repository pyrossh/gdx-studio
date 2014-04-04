package com.github.axet.lookup.common;

import java.awt.Point;

public class GPoint extends Point {
    private static final long serialVersionUID = -3174703028906427694L;

    public double g;

    public GPoint(int x, int y, double g) {
        super(x, y);
        
        this.g = g;
    }

    public String toString() {
        return "[x=" + x + ",y=" + y + ",g=" + g + "]";
    }
}
