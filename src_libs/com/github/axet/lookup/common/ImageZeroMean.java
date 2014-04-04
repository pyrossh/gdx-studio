package com.github.axet.lookup.common;

/**
 * Zero Mean Image is an image where every pixel in the image substracted by
 * mean value of the image. Mean value of the image is sum of all pixels values
 * devided by number of pixels.
 * 
 * @author axet
 * 
 */
public class ImageZeroMean extends SArray {

    double m;

    public ImageZeroMean() {
    }

    public ImageZeroMean(SArray s1) {
        init(s1);

        for (int x = 0; x < cx; x++) {
            for (int y = 0; y < cy; y++) {
                step(x, y);
            }
        }
    }

    public void init(SArray s1) {
        base = s1.base;

        cx = s1.cx;
        cy = s1.cy;

        s = new double[cx * cy];

        m = s1.mean();
    }

    public void step(int x, int y) {
        s(x, y, base.s(x, y) - m);
    }
}
