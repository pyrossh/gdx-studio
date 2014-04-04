package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Inteface for ImageBinaryGrayFeature and ImageBinaryRGBFeature classes
 * 
 * @author axet
 * 
 */
public interface ImageBinaryFeature {

    public List<ImageBinaryChannelFeature> getFeatureChannels();

    public int getWidth();

    public int getHeight();

    public int size();

    public BufferedImage getImage();

}
