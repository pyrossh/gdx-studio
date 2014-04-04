package com.github.axet.lookup.common;

import java.util.ArrayList;

public class FontFamily extends ArrayList<FontSymbol> {
    private static final long serialVersionUID = 3279037448543102425L;

    public String name;

    public FontFamily(String name) {
        this.name = name;
    }
}
