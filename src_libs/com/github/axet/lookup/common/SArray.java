package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;

import com.github.axet.lookup.Capture;

/**
 * Double array wrapper class. Holds a double[] array and provides basic
 * methods.
 * 
 * @author axet
 * 
 */
public class SArray {
    public SArray base;

    public int cx;
    public int cy;

    public double s[];

    public SArray() {
    }

    public SArray(int cx, int cy) {
        if (cx <= 0 || cy <= 0)
            throw new RuntimeException("wrong dimenssinons");

        this.cx = cx;
        this.cy = cy;
        this.s = new double[cx * cy];
    }

    public void initBase(SArray buf) {
        base = buf;

        cx = buf.cx;
        cy = buf.cy;

        s = new double[cx * cy];
    }

    public double s(int x, int y) {
        if (x < 0)
            return 0;
        if (y < 0)
            return 0;
        return s[y * cx + x];
    }

    public void s(int x, int y, double v) {
        if (x < 0)
            throw new RuntimeException("bad dim");
        if (y < 0)
            throw new RuntimeException("bad dim");
        s[y * cx + x] = v;
    }

    /**
     * Math Sigma (sum of the blocks)
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public double sigma(int x1, int y1, int x2, int y2) {
        double a = s(x1 - 1, y1 - 1);
        double b = s(x2, y1 - 1);
        double c = s(x1 - 1, y2);
        double d = s(x2, y2);
        return a + d - b - c;
    }

    public double sigma() {
        return sigma(0, 0, cx - 1, cy - 1);
    }

    public double mean(int x1, int y1, int x2, int y2) {
        double size = (x2 - x1 + 1) * (y2 - y1 + 1);
        return sigma(x1, y1, x2, y2) / size;
    }

    public double mean() {
        return mean(0, 0, cx - 1, cy - 1);
    }

    public void printDebug() {
        for (int y = 0; y < cy; y++) {
            for (int x = 0; x < cx; x++) {
                System.out.print(s(x, y));
                System.out.print("\t");
            }
            System.out.println("");
        }
    }

    public BufferedImage getImage() {
        int[] ss = new int[s.length];
        for (int i = 0; i < ss.length; i++) {
            int g = (int) s[i];

            int argb = 0xff000000 | (g << 16) | (g << 8) | (g);

            ss[i] = argb;
        }

        BufferedImage image = new BufferedImage(cx, cy, BufferedImage.TYPE_INT_ARGB);
        image.getWritableTile(0, 0).setDataElements(0, 0, image.getWidth(), image.getHeight(), ss);
        return image;
    }

    public void writeDesktop() {
        Capture.writeDesktop(getImage());
    }

}
