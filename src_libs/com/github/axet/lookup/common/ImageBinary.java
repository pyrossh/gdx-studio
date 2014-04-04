package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Inteface for ImageBinaryGray and ImageBinaryRGB classes
 * 
 * @author axet
 * 
 */

public interface ImageBinary {

    public List<ImageBinaryChannel> getChannels();

    public int getWidth() ;

    public int getHeight() ;
    
    public int size();
    
    public BufferedImage getImage();

}
