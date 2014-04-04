package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;

public abstract class ImageBinaryScale {

    public ImageBinary image;
    public BufferedImage scaleBuf;
    public ImageBinary scaleBin;

    // scale
    public double s = 0;
    // blur kernel size
    public int k = 0;

    public void rescale(int s, int k) {
        rescale(project(s), k);
    }

    public double project(int s) {
        double m = (double) Math.min(image.getWidth(), image.getHeight());
        double q = m / s;

        q = Math.ceil(q);

        q = 1 / q;

        return q;
    }

    public void rescale(double s, int k) {
        this.s = s;
        this.k = k;

        rescale();
    }

    abstract public void rescale();
}
