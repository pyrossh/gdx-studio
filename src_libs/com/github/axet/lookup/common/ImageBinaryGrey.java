package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

/**
 * Container for ImageBinary class for each channel (one gray channel here)
 * 
 * @author axet
 *
 */

public class ImageBinaryGrey implements ImageBinary {

    public GrayImage gi;
    public ImageBinaryChannel gray;

    List<ImageBinaryChannel> list;

    public ImageBinaryGrey(BufferedImage img) {
        gi = new GrayImage();
        gray = new ImageBinaryChannel();

        list = Arrays.asList(new ImageBinaryChannel[] { gray });

        this.gi.init(img);
        this.gray.initBase(gi);

        for (int x = 0; x < this.gi.cx; x++) {
            for (int y = 0; y < this.gi.cy; y++) {
                this.gi.step(x, y);
                this.gray.step(x, y);
            }
        }

        gray.zeroMean = new ImageZeroMean(gray.integral);
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

    public BufferedImage getImage() {
        return gi.buf;
    }

    @Override
    public List<ImageBinaryChannel> getChannels() {
        return list;
    }

}
