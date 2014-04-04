package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;

/**
 * BufferedImage to 3 arrays converter (rgb arrays)
 * 
 * @author axet
 * 
 */
public class RGBImage {
    public BufferedImage buf;

    int cx;
    int cy;

    public SArray r;
    public SArray g;
    public SArray b;

    public RGBImage() {
    }

    public RGBImage(BufferedImage buf) {
        init(buf);

        for (int x = 0; x < cx; x++) {
            for (int y = 0; y < cy; y++) {
                step(x, y);
            }
        }
    }

    public void init(BufferedImage buf) {
        this.buf = buf;

        cx = buf.getWidth();
        cy = buf.getHeight();

        r = new SArray(cx, cy);
        g = new SArray(cx, cy);
        b = new SArray(cx, cy);
    }

    public void step(int x, int y) {
        int argb = buf.getRGB(x, y);

        // int a = (argb & 0xff000000) >> 24;
        int r = (argb & 0x00ff0000) >> 16;
        int g = (argb & 0x0000ff00) >> 8;
        int b = (argb & 0x000000ff);

        this.r.s(x, y, r);
        this.g.s(x, y, g);
        this.b.s(x, y, b);
    }
}
