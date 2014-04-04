package com.github.axet.lookup.common;

/**
 * Range Color object. Can hold range color value. (minimum and maximum of
 * specified color) in the int-rgb value
 * 
 * @author axet
 * 
 */
public class RangeColor {
    public int min;
    public int max;

    public RangeColor(RangeColor range) {
        this.min = range.min;
        this.max = range.max;
    }

    /**
     * create range from mid
     * 
     * @param mid
     * @param diff
     */
    public RangeColor(int rgb, float f) {
        rgb &= 0x00ffffff;

        int diff = (int) (255 * f);

        int r = (rgb & 0xff0000) >> 16;
        int g = (rgb & 0x00ff00) >> 8;
        int b = (rgb & 0x0000ff) >> 0;

        int rm = r - diff;
        if (rm < 0)
            rm = 0;
        int gm = g - diff;
        if (gm < 0)
            gm = 0;
        int bm = b - diff;
        if (bm < 0)
            bm = 0;

        this.min = rm << 16 | gm << 8 | bm;

        int rb = r + diff;
        if (rb > 255)
            rb = 255;
        int gb = g + diff;
        if (gb > 255)
            gb = 255;
        int bb = b + diff;
        if (bb > 255)
            bb = 255;

        this.max = rb << 16 | gb << 8 | bb;
    }

    public RangeColor(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * if rgb1 > rgb2 return true
     * 
     * @param rgb1
     * @param rgb2
     * @return
     */
    public static boolean isGr(int rgb1, int rgb2) {
        int r1 = rgb1 & 0xff0000;
        int g1 = rgb1 & 0x00ff00;
        int b1 = rgb1 & 0x0000ff;

        int r2 = rgb2 & 0xff0000;
        int g2 = rgb2 & 0x00ff00;
        int b2 = rgb2 & 0x0000ff;

        return (r1 > r2) || (g1 > g2) || (b1 > b2);
    }

    public boolean inRange(RangeColor r) {
        return inRange(r.min) || inRange(r.max);
    }

    public boolean inRange(int rgb) {
        int r = rgb & 0xff0000;
        int g = rgb & 0x00ff00;
        int b = rgb & 0x0000ff;

        int rl = min & 0xff0000;
        int gl = min & 0x00ff00;
        int bl = min & 0x0000ff;

        int rh = max & 0xff0000;
        int gh = max & 0x00ff00;
        int bh = max & 0x0000ff;

        return (r >= rl && r <= rh) && (g >= gl && g <= gh) && (b >= bl && b <= bh);
    }

    public int getDistance(int rgb) {
        int r1 = (rgb & 0xff0000) >> 16;
        int g1 = (rgb & 0x00ff00) >> 8;
        int b1 = (rgb & 0x0000ff) >> 0;

        int rl = (min & 0xff0000) >> 16;
        int gl = (min & 0x00ff00) >> 8;
        int bl = (min & 0x0000ff) >> 0;

        int rh = (max & 0xff0000) >> 16;
        int gh = (max & 0x00ff00) >> 8;
        int bh = (max & 0x0000ff) >> 0;

        if ((r1 >= rl && r1 <= rh) && (g1 >= gl && g1 <= gh) && (b1 >= bl && b1 <= bh))
            return 0;

        int total = 0;

        if (r1 < rl)
            total = rl - r1;

        if (r1 > rh)
            total += r1 - rh;

        if (g1 < gl)
            total += gl - g1;

        if (g1 > gh)
            total += g1 - gh;

        if (b1 < bl)
            total += bl - b1;

        if (b1 > bh)
            total += b1 - bh;

        return total;
    }

    public void merge(RangeColor color) {
        merge(color.min);
        merge(color.max);
    }

    public void merge(int rgb) {
        int r1 = (rgb & 0xff0000) >> 16;
        int g1 = (rgb & 0x00ff00) >> 8;
        int b1 = (rgb & 0x0000ff) >> 0;

        int rl = (min & 0xff0000) >> 16;
        int gl = (min & 0x00ff00) >> 8;
        int bl = (min & 0x0000ff) >> 0;

        int rh = (max & 0xff0000) >> 16;
        int gh = (max & 0x00ff00) >> 8;
        int bh = (max & 0x0000ff) >> 0;

        rl = Math.min(rl, r1);
        gl = Math.min(gl, g1);
        bl = Math.min(bl, b1);

        rh = Math.max(rh, r1);
        gh = Math.max(gh, g1);
        bh = Math.max(bh, b1);

        min = (rl << 16) | (gl << 8) | (bl);
        max = (rh << 16) | (gh << 8) | (bh);
    }

    int av(int l, int h) {
        return l + (h - l) / 2;
    }

    public int average() {
        int rl = (min & 0xff0000) >> 16;
        int gl = (min & 0x00ff00) >> 8;
        int bl = (min & 0x0000ff) >> 0;

        int rh = (max & 0xff0000) >> 16;
        int gh = (max & 0x00ff00) >> 8;
        int bh = (max & 0x0000ff) >> 0;

        return (av(rl, rh) << 16) | (av(gl, gh) << 8) | (av(bl, bh));
    }

    public void extend(int a, float f) {
        RangeColor cc = new RangeColor(a, f);
        merge(cc.min);
        merge(cc.max);
    }
}
