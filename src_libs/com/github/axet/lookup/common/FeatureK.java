package com.github.axet.lookup.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.github.axet.lookup.Capture;

public class FeatureK {
    public Feature f;
    public List<RectK> list;
    public double k;

    IntegralImage template;

    public FeatureK(RectK k) {
        list.add(k);
    }

    public FeatureK(Feature f, IntegralImage template) {
        this.template = template;
        this.f = f;

        Set<RectK> list = new TreeSet<RectK>();
        for (int x = 0; x < f.cx; x++) {
            for (int y = 0; y < f.cy; y++) {
                RectK k = rectNearFill(x, y);
                list.add(k);
            }
        }
        this.list = Arrays.asList(list.toArray(new RectK[] {}));

        double dx = template.cx / (double) f.cx;
        double dy = template.cy / (double) f.cy;

        for (RectK k : this.list) {
            int w = k.x2 - k.x1 + 1;
            int h = k.y2 - k.y1 + 1;

            k.x1 *= dx;
            k.y1 *= dy;
            k.x2 = (int) (k.x1 + w * dx - 1);
            k.y2 = (int) (k.y1 + h * dy - 1);

            k.cxBase = template.cx;
            k.cyBase = template.cy;

            k.k = template.mean(k.x1, k.y1, k.x2, k.y2);

            this.k += k.k;
        }

        // this.list = fillFeature(3);
    }

    /**
     * debug. make feature exact as template image
     * 
     * @param step
     * @return
     */
    List<RectK> fillFeature(int step) {
        List<RectK> list = new ArrayList<RectK>();

        int cx = template.cx;
        int cy = template.cy;

        for (int x = 0; x < cx; x += step) {
            for (int y = 0; y < cy; y += step) {
                RectK k = new RectK(x, y);
                k.x2 += step - 1;
                k.y2 += step - 1;

                if (k.x2 >= cx)
                    k.x2 = cx - 1;

                if (k.y2 >= cy)
                    k.y2 = cy - 1;

                if (k.getWidth() == 0)
                    continue;

                if (k.getHeight() == 0)
                    continue;

                k.cxBase = template.cx;
                k.cyBase = template.cy;

                k.k = template.mean(k.x1, k.y1, k.x2, k.y2);

                list.add(k);
            }
        }

        return list;
    }

    RectK rectNearFill(int x, int y) {
        RectK k = near(x, y);
        k = fill(k);
        return k;
    }

    int limX(int i) {
        if (i < 0)
            return 0;

        if (i >= f.cx)
            return f.cx - 1;

        return i;
    }

    int limY(int i) {
        if (i < 0)
            return 0;

        if (i >= f.cy)
            return f.cy - 1;

        return i;
    }

    RectK near(int x, int y) {
        if (test(x, y))
            return new RectK(x, y);

        int m = Math.max(f.cx, f.cy);
        for (int r = 1; r < m; r++) {
            int xx;
            int xxm;
            int yy;
            int yym;

            // top - bottom
            xx = limX(x - r);
            yym = limY(y + r);
            for (yy = limY(y - r); yy <= yym; yy++) {
                if (test(xx, yy))
                    return new RectK(xx, yy);
            }
            // left - right
            yy = limY(y + r);
            xxm = limX(x + r);
            for (xx = limX(x - r); xx <= xxm; xx++) {
                if (test(xx, yy))
                    return new RectK(xx, yy);
            }
            // down - top
            xx = limX(x + r);
            yym = limY(y - r);
            for (yy = limY(y + r); yy >= yym; yy--) {
                if (test(xx, yy))
                    return new RectK(xx, yy);
            }
            // right - left
            yy = limY(y - r);
            xxm = limX(x - r);
            for (xx = limX(x + r); xx >= xxm; xx--) {
                if (test(xx, yy))
                    return new RectK(xx, yy);
            }
        }

        throw new RuntimeException("no 1 found");
    }

    boolean test(int x, int y) {
        if (x >= f.cx)
            return false;
        if (y >= f.cy)
            return false;

        if (f.s(x, y) == 1)
            return true;

        return false;
    }

    RectK fill(RectK k) {
        while (test(k.x1 - 1, k.y1)) {
            k.x1--;
        }

        while (test(k.x1, k.y1 - 1)) {
            k.y1--;
        }

        while (test(k.x2 + 1, k.y1)) {
            k.x2++;
        }

        while (test(k.x2, k.y2 + 1)) {
            k.y2++;
        }

        k.cxBase = f.cx;
        k.cyBase = f.cy;

        return k;
    }

    public void devide(RectK k) {
        ;
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(template.cx, template.cy, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, template.cx, template.cy);

        g.setColor(new Color(0, 0, 0));
        for (RectK k : list) {
            g.fillRect(k.x1, k.y1, k.getWidth(), k.getHeight());
        }
        return image;
    }

    public void writeDesktop() {
        Capture.writeDesktop(getImage());
    }
}
