package com.github.axet.lookup.common;

public class FeatureSetAuto extends FeatureSet {
    private static final long serialVersionUID = -4442575077693435100L;

    public FeatureSetAuto(ImageBinaryChannelFeature template, double threshold) {

        Feature f = new Feature(1, 1, new double[] {

        1,

        });

        FeatureK k = new FeatureK(f, template.zeroMeanIntegral);

        FeatureSet s = j(template, k, threshold);

        addAll(s);
    }

    FeatureSet j(ImageBinaryChannelFeature template, FeatureK k, double threshold) {
        FeatureSet list = new FeatureSet();

        for (RectK r : k.list) {
            list.addAll(j(template, threshold, r));
        }

        return list;
    }

    FeatureSet j(ImageBinaryChannelFeature template, double threshold, RectK r) {
        FeatureSet s = new FeatureSet();

        double j = 0;

        for (int x = r.x1; x <= r.x2; x++) {
            for (int y = r.y1; y <= r.y2; y++) {
                j += IntegralImage2.pow2(template.zeroMean.s(x, y) - r.k);
            }
        }

        if (j > threshold) {
            RectK[] rr = r.devide();
            if (rr == null) {
                s.add(r.getFeature());
            } else {
                for (RectK rrr : rr) {
                    s.addAll(j(template, threshold, rrr));
                }
            }
        } else {
            s.add(r.getFeature());
        }

        return s;
    }
}
