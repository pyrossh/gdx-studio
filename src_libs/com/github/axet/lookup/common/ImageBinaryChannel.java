package com.github.axet.lookup.common;

/**
 * Minimal Sum-Tables required for any image for NCC or FNCC algorithm.
 * 
 * 1) Base Image Array
 * 
 * 2) Integral Image
 * 
 * 3) Integral ^ 2 Image (Image Energy)
 * 
 * 4) Zero Mean Image (image where each pixel substracted with image mean value)
 * 
 * @author axet
 *
 */
public class ImageBinaryChannel {
    public SArray gi;
    public IntegralImage integral;
    public IntegralImage2 integral2;
    public ImageZeroMean zeroMean;

    public ImageBinaryChannel() {
        integral = new IntegralImage();
        integral2 = new IntegralImage2();
    }

    public ImageBinaryChannel(SArray img) {
        gi = img;
        integral = new IntegralImage();
        integral2 = new IntegralImage2();

        this.integral.initBase(gi);
        this.integral2.initBase(gi);

        for (int x = 0; x < this.gi.cx; x++) {
            for (int y = 0; y < this.gi.cy; y++) {
                step(x,y);
            }
        }

        zeroMean = new ImageZeroMean(integral);
    }
    
    public void step(int x,int y) {
        integral.step(x, y);
        integral2.step(x, y);
    }

    public void initBase(SArray img) {
        gi = img;
        integral.initBase(img);
        integral2.initBase(img);
    }

    public double dev2n() {
        return integral2.dev2n(integral);
    }

    public double dev2() {
        return integral2.dev2(integral);
    }

    public double dev() {
        return integral2.dev(integral);
    }

    public double dev2n(int x1, int y1, int x2, int y2) {
        return integral2.dev2n(integral, x1, y1, x2, y2);
    }

    public double dev2(int x1, int y1, int x2, int y2) {
        return integral2.dev2(integral, x1, y1, x2, y2);
    }

    public double dev(int x1, int y1, int x2, int y2) {
        return integral2.dev(integral, x1, y1, x2, y2);
    }

    public int getWidth() {
        return gi.cx;
    }

    public int getHeight() {
        return gi.cy;
    }

    public int size() {
        return gi.cx * gi.cy;
    }

}
