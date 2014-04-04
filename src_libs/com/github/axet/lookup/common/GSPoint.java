package com.github.axet.lookup.common;

public class GSPoint extends GPoint {
    private static final long serialVersionUID = -3174703028906427694L;

    public double gg;

    public GSPoint(int x, int y, double gg, double g) {
        super(x, y, g);

        this.gg = gg;
    }

    public GSPoint(GPoint g, double gg) {
        super(g.x, g.y, g.g);

        this.gg = gg;
    }

    public String toString() {
        return "[x=" + x + ",y=" + y + ",gg=" + gg + ",g=" + g + "]";
    }
}
