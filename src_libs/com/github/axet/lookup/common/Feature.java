package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;

/**
 * Haar Like feature
 * 
 * @author axet
 * 
 */
public class Feature extends SArray {

    public Feature(int cx, int cy, double[] i) {
        this.cx = cx;
        this.cy = cy;
        this.s = i;
    }

    public Feature(SArray s) {
        this.cx = s.cx;
        this.cy = s.cy;
        this.s = s.s;
    }

    public BufferedImage getImage() {
        int[] ss = new int[s.length];
        for (int i = 0; i < ss.length; i++) {
            int argb = s[i] == 1 ? 0xffffffff : 0xff000000;

            ss[i] = argb;
        }

        BufferedImage image = new BufferedImage(cx, cy, BufferedImage.TYPE_INT_ARGB);
        image.getWritableTile(0, 0).setDataElements(0, 0, image.getWidth(), image.getHeight(), ss);
        return image;
    }
}
