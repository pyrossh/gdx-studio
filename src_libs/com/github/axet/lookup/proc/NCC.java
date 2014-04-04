package com.github.axet.lookup.proc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.axet.lookup.Lookup.NotFound;
import com.github.axet.lookup.common.GFirst;
import com.github.axet.lookup.common.GPoint;
import com.github.axet.lookup.common.ImageBinary;
import com.github.axet.lookup.common.ImageBinaryChannel;
import com.github.axet.lookup.common.ImageBinaryRGB;
import com.github.axet.lookup.common.ImageMultiplySum;

/**
 * 
 * http://www.fmwconcepts.com/imagemagick/similar/index.php
 * 
 * 1) mean && stddev
 * 
 * 2) image1(x,y) - mean1 && image2(x,y) - mean2
 * 
 * 3) [3] = (image1(x,y) - mean)(x,y) * (image2(x,y) - mean)(x,y)
 * 
 * 4) [4] = mean([3])
 * 
 * 5) [4] / (stddev1 * stddev2)
 * 
 * Normalized cross correlation algorithm
 * 
 * @author axet
 * 
 */
public class NCC {

    static public GPoint lookup(BufferedImage i, BufferedImage t, float m) {
        List<GPoint> list = lookupAll(i, t, m);

        if (list.size() == 0)
            throw new NotFound();

        Collections.sort(list, new GFirst());

        return list.get(0);
    }

    static public List<GPoint> lookupAll(BufferedImage i, BufferedImage t, float m) {
        ImageBinaryRGB imageBinary = new ImageBinaryRGB(i);
        ImageBinaryRGB templateBinary = new ImageBinaryRGB(t);

        return lookupAll(imageBinary, templateBinary, m);
    }

    static public GPoint lookup(ImageBinary image, ImageBinary template, float m) {
        List<GPoint> list = lookupAll(image, template, m);

        if (list.size() == 0)
            throw new NotFound();

        Collections.sort(list, new GFirst());

        return list.get(0);
    }

    static public List<GPoint> lookupAll(ImageBinary image, ImageBinary template, float m) {
        return lookupAll(image, 0, 0, image.getWidth() - 1, image.getHeight() - 1, template, m);
    }

    static public GPoint lookup(ImageBinary image, int x1, int y1, int x2, int y2, ImageBinary template, float m) {
        List<GPoint> list = lookupAll(image, x1, y1, x2, y2, template, m);

        if (list.size() == 0)
            throw new NotFound();

        Collections.sort(list, new GFirst());

        return list.get(0);
    }

    static public List<GPoint> lookupAll(ImageBinary image, int x1, int y1, int x2, int y2, ImageBinary template,
            float m) {
        List<GPoint> list = new ArrayList<GPoint>();

        for (int x = x1; x <= x2 - template.getWidth() + 1; x++) {
            for (int y = y1; y <= y2 - template.getHeight() + 1; y++) {
                GPoint g = lookup(image, template, x, y, m);
                if (g != null)
                    list.add(g);
            }
        }

        return list;
    }

    static double numerator(ImageBinaryChannel image, ImageBinaryChannel template, int xx, int yy) {
        ImageMultiplySum m = new ImageMultiplySum(image.zeroMean, xx, yy, template.zeroMean);
        return m.sum;
    }

    static double denominator(ImageBinaryChannel image, ImageBinaryChannel template, int xx, int yy) {
        double di = image.dev2n(xx, yy, xx + template.getWidth() - 1, yy + template.getHeight() - 1);
        double dt = template.dev2n();
        return Math.sqrt(di * dt);
    }

    static public GPoint lookup(ImageBinary image, ImageBinary template, int x, int y, float m) {
        List<ImageBinaryChannel> ci = image.getChannels();
        List<ImageBinaryChannel> ct = template.getChannels();

        int ii = Math.min(ci.size(), ct.size());

        double g = Double.MAX_VALUE;

        for (int i = 0; i < ii; i++) {
            double gg = gamma(ci.get(i), ct.get(i), x, y);

            if (gg < m)
                return null;

            g = Math.min(g, gg);
        }

        return new GPoint(x, y, g);
    }

    static public double gamma(ImageBinary image, ImageBinary template, int x, int y) {
        List<ImageBinaryChannel> ci = image.getChannels();
        List<ImageBinaryChannel> ct = template.getChannels();

        int ii = Math.min(ci.size(), ct.size());

        double g = 0;

        for (int i = 0; i < ii; i++) {
            g += gamma(ci.get(i), ct.get(i), x, y);
        }

        g /= ii;

        return g;
    }

    static public double gammaMin(ImageBinary image, ImageBinary template, int x, int y) {
        List<ImageBinaryChannel> ci = image.getChannels();
        List<ImageBinaryChannel> ct = template.getChannels();

        int ii = Math.min(ci.size(), ct.size());

        double g = Double.MAX_VALUE;

        for (int i = 0; i < ii; i++) {
            g = Math.min(g, gamma(ci.get(i), ct.get(i), x, y));
        }

        return g;
    }

    static public double gamma(ImageBinaryChannel image, ImageBinaryChannel template, int xx, int yy) {
        double d = denominator(image, template, xx, yy);

        if (d == 0)
            return -1;

        double n = numerator(image, template, xx, yy);

        return (n / d);
    }

}
