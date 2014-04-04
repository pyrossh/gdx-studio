package com.github.axet.lookup.common;

import java.awt.image.BufferedImage;

public class FontSymbol {
    public FontFamily fontFamily;
    public String fontSymbol;
    public ImageBinaryGrey image;

    public FontSymbol(FontFamily ff, String fs, BufferedImage i) {
        this.fontFamily = ff;
        this.fontSymbol = fs;
        this.image = new ImageBinaryGrey(i);
    }
}
