package com.github.axet.lookup.common;

import java.awt.Rectangle;
import java.util.Comparator;

public class GFirstLeftRight implements Comparator<GPoint> {
    ImageBinary image;
    int wh;
    int hh;

    public GFirstLeftRight(ImageBinary image) {
        this.image = image;
        wh = image.getWidth() / 2;
        hh = image.getHeight() / 2;
    }

    @Override
    public int compare(GPoint arg0, GPoint arg1) {
        int r = 0;

        Rectangle r1 = new Rectangle(arg0.x - wh, arg0.y - hh, image.getWidth(), image.getHeight());
        Rectangle r2 = new Rectangle(arg1.x - wh, arg1.y - hh, image.getWidth(), image.getHeight());

        if (!r1.intersects(r2)) {
            if (r == 0)
                r = LessCompare.compareSmallFirst(arg0.y, arg1.y);
            if (r == 0)
                r = LessCompare.compareSmallFirst(arg0.x, arg1.x);
        }

        if (r == 0)
            r = LessCompare.compareBigFirst(arg0.g, arg1.g);

        return r;
    }
}