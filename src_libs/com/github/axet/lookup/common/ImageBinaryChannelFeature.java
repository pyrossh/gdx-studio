package com.github.axet.lookup.common;

import java.util.List;

/**
 * Same as ImageBinaryChannel but with addition of Integral Image over Zero Mean
 * Image matrix.
 * 
 * 
 * @author axet
 * 
 */
public class ImageBinaryChannelFeature extends ImageBinaryChannel {
    public List<FeatureK> k;

    public IntegralImage zeroMeanIntegral;

    public ImageBinaryChannelFeature() {
        integral = new IntegralImage();
        integral2 = new IntegralImage2();
    }

    public ImageBinaryChannelFeature(SArray template, FeatureSet list) {
        super(template);

        zeroMeanIntegral = new IntegralImage(zeroMean);

        init(list);
    }

    public ImageBinaryChannelFeature(SArray template, double threshold) {
        super(template);

        zeroMeanIntegral = new IntegralImage(zeroMean);

        FeatureSet list = new FeatureSetAuto(this, threshold);

        init(list);
    }

    void init(FeatureSet list) {
        k = list.k(zeroMeanIntegral);
    }
}