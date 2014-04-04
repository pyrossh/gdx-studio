package com.github.axet.lookup.common;

/**
 * Image Energy. Squared Image Function f^2(x,y).
 * 
 * http://en.wikipedia.org/wiki/Summed_area_table
 * 
 * @author axet
 * 
 */
public class IntegralImage2 extends SArray {

    static public double pow2(double x) {
        return x * x;
    }

    public IntegralImage2() {
    }

    public IntegralImage2(SArray buf) {
        initBase(buf);

        for (int x = 0; x < cx; x++) {
            for (int y = 0; y < cy; y++) {
                step(x, y);
            }
        }
    }

    public void step(int x, int y) {
        s(x, y, pow2(base.s(x, y)) + s(x - 1, y) + s(x, y - 1) - s(x - 1, y - 1));
    }

    /**
     * Standard deviation no sqrt and no mean
     * 
     * @param i
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */

    public double dev2n(IntegralImage i, int x1, int y1, int x2, int y2) {
        double sum = i.sigma(x1, y1, x2, y2);
        int size = (x2 - x1 + 1) * (y2 - y1 + 1);
        double sum2 = sigma(x1, y1, x2, y2);
        return sum2 - pow2(sum) / size;
    }

    /**
     * Standard deviation no sqrt
     * 
     * @param i
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public double dev2(IntegralImage i, int x1, int y1, int x2, int y2) {
        int size = (x2 - x1 + 1) * (y2 - y1 + 1);
        return dev2n(i, x1, y1, x2, y2) / (size - 1);
    }

    /**
     * Standard deviation
     * 
     * @param i
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public double dev(IntegralImage i, int x1, int y1, int x2, int y2) {
        return Math.sqrt(dev2(i, x1, y1, x2, y2));
    }

    public double dev2n(IntegralImage i) {
        return dev2n(i, 0, 0, cx - 1, cy - 1);
    }

    public double dev2(IntegralImage i) {
        return dev2(i, 0, 0, cx - 1, cy - 1);
    }

    public double dev(IntegralImage i) {
        return dev(i, 0, 0, cx - 1, cy - 1);
    }
}
