package com.github.axet.lookup.common;

/**
 * Multiply matrix Pixel by Pixel and sum all pixels
 * 
 * @author axet
 * 
 */

public class ImageMultiplySum {

    public ImageMultiply s = new ImageMultiply();

    public double sum = 0;

    public ImageMultiplySum(SArray image, int xx, int yy, SArray template) {
        s.init(image, xx, yy, template);

        for (int x = 0; x < template.cx; x++) {
            for (int y = 0; y < template.cy; y++) {
                s.step(x, y);

                sum += s.s(x, y);
            }
        }
    }
}
