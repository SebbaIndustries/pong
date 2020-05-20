package com.sebbaindustries.pong;

/**
 * <b>This class is used for showing fps smoother than you ever </b>
 * @author sebbaindustries
 * @version 1.0
 */
public class ScalarExpSmoother {

    double data;
    double currentWeight;
    double previousWeight;

    /**
     * @param currentWeight How much smoothing do you desire?
     */
    public ScalarExpSmoother(double currentWeight) {
        this.currentWeight = currentWeight;
        previousWeight = 1 - currentWeight;
    }

    /**
     * got this from stack, so it must work :/
     * @param currentFrame fps
     * @return smoother fps
     */
    public double smooth(double currentFrame) {
        data = previousWeight * data + currentWeight * currentFrame;
        return data;
    }

}
