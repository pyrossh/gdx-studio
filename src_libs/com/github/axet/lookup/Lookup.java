package com.github.axet.lookup;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import com.github.axet.lookup.proc.CannyEdgeDetector;

public class Lookup {

    public static class NotFound extends RuntimeException {
        private static final long serialVersionUID = 5393563026702192412L;

        public NotFound() {
            super("NotFound");
        }
    }

    // convert

    public BufferedImage convert(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
        return bufferedImage;
    }

    // filter

    static public BufferedImage edgeImageDouble(BufferedImage b) {
        // b = Lookup.filterResizeDoubleCanvas(b);

        b = Lookup.edge(b);

        b = Lookup.filterRemoveCanvas(b);

        return b;
    }

    static public BufferedImage edgeImageCrop(BufferedImage b) {
        b = filterDoubleCanvas(b);

        b = Lookup.filterRemoveCanvas(b);

        return b;
    }

    static public BufferedImage filterSimply(BufferedImage bi) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        float n = 1f / 25f;
        Kernel kernel = new Kernel(5, 5, new float[] { n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n });

        ConvolveOp op = new ConvolveOp(kernel);
        op.filter(bi, buff);

        return buff;
    }

    public BufferedImage filterResize(BufferedImage bi) {
        int cx = bi.getWidth() / 7;
        int cy = bi.getHeight() / 7;
        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bi, 0, 0, cx, cy, null);
        g.dispose();

        return resizedImage;
    }

    static public BufferedImage filterResizeDouble(BufferedImage bi) {
        int cx = bi.getWidth() * 4;
        int cy = bi.getHeight() * 4;
        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bi, 0, 0, cx, cy, null);
        g.dispose();
        return resizedImage;
    }

    static public BufferedImage scale(BufferedImage bi, double s, int blurKernel) {
        bi = filterGausBlur(bi, blurKernel);

        int cx = (int) (bi.getWidth() * s);
        int cy = (int) (bi.getHeight() * s);

        Image src = bi.getScaledInstance(cx, cy, Image.SCALE_SMOOTH);

        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(src, 0, 0, cx, cy, null);
        g.dispose();

        return resizedImage;
    }

    static public BufferedImage scalePower(BufferedImage bi, double s) {
        double m = 1 / s;

        int cx = (int) (bi.getWidth() / m) + 1;
        int cy = (int) (bi.getHeight() / m) + 1;

        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        for (int x = 0; x < bi.getWidth(); x += m) {
            for (int y = 0; y < bi.getHeight(); y += m) {
                resizedImage.setRGB((int) (x / m), (int) (y / m), bi.getRGB(x, y));
            }
        }
        return resizedImage;
    }

    static public BufferedImage filterDoubleCanvas(BufferedImage bi) {
        int cx = bi.getWidth() * 2;
        int cy = bi.getHeight() * 2;

        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        Graphics g = resizedImage.getGraphics();
        g.drawImage(bi, cx / 4, cy / 4, bi.getWidth(), bi.getHeight(), null);
        g.dispose();

        return resizedImage;
    }

    static public BufferedImage filterRemoveDoubleCanvas(BufferedImage bi) {
        int cx = bi.getWidth() / 2;
        int cy = bi.getHeight() / 2;

        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bi, 0, 0, cx, cy, cx / 2, cy / 2, cx / 2 + cx, cy / 2 + cy, null);
        g.dispose();

        return resizedImage;
    }

    static public BufferedImage filterRemoveBorder(BufferedImage bi, int border) {
        int cx = bi.getWidth() - border * 2;
        int cy = bi.getHeight() - border * 2;

        BufferedImage resizedImage = new BufferedImage(cx, cy, bi.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bi, 0, 0, cx, cy, border, border, border + cx, border + cy, null);
        g.dispose();

        return resizedImage;
    }

    static public BufferedImage filterRemoveCanvas(BufferedImage bi) {
        int x1 = bi.getWidth();
        int x2 = 0;

        int y1 = bi.getHeight();
        int y2 = 0;

        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                if ((bi.getRGB(x, y) & 0xffffff) != 0) {
                    if (x1 > x)
                        x1 = x;
                    if (x2 < x)
                        x2 = x;
                    if (y1 > y)
                        y1 = y;
                    if (y2 < y)
                        y2 = y;
                }
            }
        }

        int cx = x2 - x1;
        int cy = y2 - y1;

        BufferedImage dest = new BufferedImage(cx, cy, bi.getType());
        Graphics g = dest.getGraphics();
        g.drawImage(bi, 0, 0, (int) dest.getWidth(), (int) dest.getHeight(), x1, y1, x1 + dest.getWidth(),
                y1 + dest.getHeight(), null);
        g.dispose();

        return dest;
    }
    
    static public BufferedImage filterBlur(BufferedImage bi, int blurKernel) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        float[] f = new float[blurKernel * blurKernel];
        float n = 1 / (float) f.length;
        for (int i = 0; i < f.length; i++)
            f[i] = n;

        Kernel kernel = new Kernel(blurKernel, blurKernel, f);

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        op.filter(bi, buff);

        return buff;
    }

    static public BufferedImage filterGausBlur(BufferedImage bi, int blurKernel) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        GaussianFilter op = new GaussianFilter(blurKernel);
        op.filter(bi, buff);

        return buff;
    }

    static public BufferedImage filterBlur3(BufferedImage bi) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        float n = 1f / 9f;
        Kernel kernel = new Kernel(3, 3, new float[] { n, n, n, n, n, n, n, n, n });

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        op.filter(bi, buff);

        return buff;
    }

    static public BufferedImage filterBlur5(BufferedImage bi) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        float n = 1f / 25f;
        Kernel kernel = new Kernel(5, 5, new float[] { n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n });

        ConvolveOp op = new ConvolveOp(kernel);
        op.filter(bi, buff);

        return buff;
    }

    static public BufferedImage filterBlur8(BufferedImage bi) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        float n = 1f / 64f;
        Kernel kernel = new Kernel(8, 8, new float[] { n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n, n, n, n, n, n, });

        ConvolveOp op = new ConvolveOp(kernel);
        op.filter(bi, buff);

        return buff;
    }

    static public BufferedImage filterBlur10(BufferedImage bi) {
        BufferedImage buff = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());

        float n = 1f / 100f;
        Kernel kernel = new Kernel(10, 10, new float[] { n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n, n,
                n, n, n, n, n, n, n, n, n, n, n });

        ConvolveOp op = new ConvolveOp(kernel);
        op.filter(bi, buff);

        return buff;
    }

    static public BufferedImage toGray(BufferedImage bi) {
        BufferedImage out = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(bi, out);

        return out;
    }

    static public BufferedImage edge(BufferedImage bi) {
        CannyEdgeDetector detector = new CannyEdgeDetector();

        detector.setLowThreshold(3f);
        detector.setHighThreshold(3f);
        detector.setGaussianKernelWidth(2);
        detector.setGaussianKernelRadius(1f);

        detector.setSourceImage(bi);
        detector.process();

        return detector.getEdgesImage();
    }

}
