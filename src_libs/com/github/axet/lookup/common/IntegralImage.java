package com.github.axet.lookup.common;

/**
 * Sum Table. Integral sum.
 * 
 * http://en.wikipedia.org/wiki/Summed_area_table
 * 
 * @author axet
 * 
 */
public class IntegralImage extends SArray {

    public IntegralImage() {
    }

    public IntegralImage(SArray buf) {
        initBase(buf);

        for (int x = 0; x < cx; x++) {
            for (int y = 0; y < cy; y++) {
                step(x, y);
            }
        }
    }

    public void step(int x, int y) {
        s(x, y, base.s(x, y) + s(x - 1, y) + s(x, y - 1) - s(x - 1, y - 1));
    }

}
