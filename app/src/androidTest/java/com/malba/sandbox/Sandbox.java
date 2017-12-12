package com.malba.sandbox;

import android.view.animation.PathInterpolator;
import com.malba.sandbox.interpolator.TransformingInterpolator;

import org.junit.Test;


/**
 * Created by malba on 11/24/2017.
 */

public class Sandbox {
    @Test
    public void startSandbox(){
        TransformingInterpolator easeOutQuart = new TransformingInterpolator( new PathInterpolator(0.165f, 0.84f, 0.44f, 1f) );
        TransformingInterpolator easeInOutQuart = new TransformingInterpolator( new PathInterpolator(0.77f, 0f, 0.175f, 1f) );

        System.out.println("easeOutQuart " + easeOutQuart.findMaxVelocityPercent());
        System.out.println("easeInOutQuart " + easeInOutQuart.findMaxVelocityPercent());
    }
}
