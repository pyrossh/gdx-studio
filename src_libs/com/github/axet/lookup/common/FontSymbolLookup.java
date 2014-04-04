package com.github.axet.lookup.common;

import java.awt.Rectangle;



public class FontSymbolLookup {
    public int x;
    public int y;
    public FontSymbol fs;
    public double g;

    public FontSymbolLookup(FontSymbol fs, int x, int y, double g) {
        this.fs = fs;
        this.x = x;
        this.y = y;
        this.g = g;
    }

    public int size() {
        return fs.image.getHeight() * fs.image.getWidth();
    }

    public boolean cross(FontSymbolLookup f) {
        Rectangle r = new Rectangle(x, y, fs.image.getWidth(), fs.image.getHeight());
        Rectangle r2 = new Rectangle(f.x, f.y, f.fs.image.getWidth(), f.fs.image.getHeight());

        return r.intersects(r2);
    }

    // Changed this part from original code
    public boolean yCross(FontSymbolLookup f) {
    	IntRange r1 = new IntRange(y, y + fs.image.getHeight());

        IntRange r2 = new IntRange(f.y, f.y + f.fs.image.getHeight());

        return r1.overlapsRange(r2);
    }

    public int getWidth() {
        return fs.image.getWidth();
    }
    
    public boolean containsInteger(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public int getHeight() {
        return fs.image.getHeight();
    }
}


class IntRange{
	private final int min;
	private final int max;
	
	IntRange(int number1, int number2){
		if (number2 < number1) {
			this.min = number2;
			this.max = number1;
		} else {
			this.min = number1;
			this.max = number2;
		}
	}
	
	public int getMinimumInteger() {
	         return min;
	}
	
	public boolean containsInteger(int value) {
		return value >= min && value <= max;
	}
	
	public boolean overlapsRange(IntRange range) {
		return range.containsInteger(min) ||
				range.containsInteger(max) || 
			containsInteger(range.getMinimumInteger());
		}
}