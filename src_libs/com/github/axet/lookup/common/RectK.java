package com.github.axet.lookup.common;

/**
 * Haar like Feature rect with it's value k.
 * 
 * @author axet
 * 
 */
public class RectK implements Comparable<RectK> {
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    // base rect cx size
    public int cxBase;
    // base rect cy size
    public int cyBase;

    // sum of the pixels in the area
    public double k;

    public RectK(int x, int y) {
        x1 = x;
        y1 = y;
        x2 = x;
        y2 = y;
    }

    public RectK(int x1, int y1, int x2, int y2, int cx, int cy) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        this.cxBase = cx;
        this.cyBase = cy;
    }

    public int getWidth() {
        return x2 - x1 + 1;
    }

    public int getHeight() {
        return y2 - y1 + 1;
    }

    public int size() {
        return (x2 - x1 + 1) * (y2 - y1 + 1);
    }

    public boolean equal(RectK k) {
        return x1 == k.x1 && x2 == k.x2 && y1 == k.y1 && y2 == k.y2;
    }

    @Override
    public int compareTo(RectK k) {
        int r = 0;

        if (r == 0)
            r = new Integer(x1).compareTo(k.x1);

        if (r == 0)
            r = new Integer(y1).compareTo(k.y1);

        if (r == 0)
            r = new Integer(x2).compareTo(k.x2);

        if (r == 0)
            r = new Integer(y2).compareTo(k.y2);

        return r;
    }

    public RectK[] devide() {
        int w = getWidth();
        int h = getHeight();
        if (w > h) {
            w = w / 2;

            RectK r1 = new RectK(x1, y1, x1 + w - 1, y1 + h - 1, cxBase, cyBase);

            int r2x1 = r1.x2 + 1;
            int r2y1 = r1.y1;
            int r2cx = getWidth() - r1.getWidth();
            int r2cy = getHeight();

            RectK r2 = new RectK(r2x1, r2y1, r2x1 + r2cx - 1, r2y1 + r2cy - 1, cxBase, cyBase);

            if (r1.getWidth() <= 0 || r1.getHeight() <= 0)
                throw null;
            if (r2.getWidth() <= 0 || r2.getHeight() <= 0)
                throw null;

            return new RectK[] { r1, r2 };
        } else {
            h = h / 2;

            RectK r1 = new RectK(x1, y1, x1 + w - 1, y1 + h - 1, cxBase, cyBase);

            int r2x1 = r1.x1;
            int r2y1 = r1.y2 + 1;
            int r2cx = getWidth();
            int r2cy = getHeight() - r1.getHeight();

            RectK r2 = new RectK(r2x1, r2y1, r2x1 + r2cx - 1, r2y1 + r2cy - 1, cxBase, cyBase);

            if (r1.getWidth() <= 0 || r1.getHeight() <= 0)
                return null;
            if (r2.getWidth() <= 0 || r2.getHeight() <= 0)
                throw null;

            return new RectK[] { r1, r2 };
        }
    }

    public Feature getFeature() {
        SArray s = new SArray(cxBase, cyBase);

        int c = 0;

        for (int x = 0; x < s.cx; x++) {
            for (int y = 0; y < s.cy; y++) {
                boolean test = x >= x1 && x <= x2 && y >= y1 && y <= y2;
                int v = test ? 1 : 0;
                s.s(x, y, v);

                c += v;
            }
        }

        // for debug purpose
        if (c == 0)
            throw new RuntimeException("empty feature");

        return new Feature(s);
    }
}
