package com.malba.sandbox;

import android.view.animation.Interpolator;

import com.malba.sandbox.interpolator.CombinedInterpolator;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests to ensure the combined interpolator is functioning as expected.
 */
public class CombinedInterpolatorUnitTest {
    /**
     * Static interpolation class so that we can easily test the combined interpolator.
     */
    private class StaticInterpolator implements Interpolator {
        // The value that will be returned each interpolation.
        private float mInterpolationValue;

        /**
         * @param interpolationValue The value that will be returned each interpolation.
         */
        StaticInterpolator(float interpolationValue) {
            mInterpolationValue = interpolationValue;
        }

        @Override
        public float getInterpolation(float v) {
            return mInterpolationValue;
        }
    }

    /**
     * Tests to ensure that interpolators are inserted in the correct ordering.
     */
    @Test
    public void test_insertionOrder() {
        Interpolator interpolator_0 = new StaticInterpolator(0f);
        Interpolator interpolator_1 = new StaticInterpolator(0.1f);
        Interpolator interpolator_2 = new StaticInterpolator(0.2f);
        Interpolator interpolator_3 = new StaticInterpolator(0.3f);
        Interpolator interpolator_4 = new StaticInterpolator(0.4f);

        // Create the combined interpolator, and add the children in a mischevious order.
        CombinedInterpolator combinedInterpolator = new CombinedInterpolator();

        // Test the case where we have no registered interpolators
        Assert.assertEquals( -1, combinedInterpolator.getInterpolatorIndexAtPercent(0) );

        // Start adding some interpolators.
        combinedInterpolator.addInterpolator(interpolator_3, 0.3f);

        // Test the case where we have only one registered interpolator.
        Assert.assertEquals( -1, combinedInterpolator.getInterpolatorIndexAtPercent(0.299f) );
        Assert.assertEquals( 0, combinedInterpolator.getInterpolatorIndexAtPercent(1.0f) );

        // Add the rest of the interpolators.
        combinedInterpolator.addInterpolator(interpolator_0, 0f);
        combinedInterpolator.addInterpolator(interpolator_1, 0.1f);
        combinedInterpolator.addInterpolator(interpolator_2, 0.2f);
        combinedInterpolator.addInterpolator(interpolator_4, 0.4f);

        // Check the boundaries for each interpolator.
        Assert.assertEquals(0, combinedInterpolator.getInterpolatorIndexAtPercent(0f));
        Assert.assertEquals(1, combinedInterpolator.getInterpolatorIndexAtPercent(0.1f));
        Assert.assertEquals(2, combinedInterpolator.getInterpolatorIndexAtPercent(0.2f));
        Assert.assertEquals(3, combinedInterpolator.getInterpolatorIndexAtPercent(0.3f));
        Assert.assertEquals(4, combinedInterpolator.getInterpolatorIndexAtPercent(0.4f));
    }

    /**
     * Test case making sure the index validity works as expected.
     */
    @Test
    public void test_InterpolatorIndexValidity() {
        Interpolator interpolator_0 = new StaticInterpolator(0f);
        Interpolator interpolator_1 = new StaticInterpolator(0.1f);

        CombinedInterpolator combinedInterpolator = new CombinedInterpolator();
        combinedInterpolator.addInterpolator(interpolator_0, 0f);
        combinedInterpolator.addInterpolator(interpolator_1, 0.1f);

        // Check out of bounds.
        Assert.assertFalse( combinedInterpolator.isValidInterpolatorIndexAtPercent(-1, 0) );
        Assert.assertFalse( combinedInterpolator.isValidInterpolatorIndexAtPercent(2, 0) );

        // Check too small
        Assert.assertFalse( combinedInterpolator.isValidInterpolatorIndexAtPercent(1, 0.09f) );

        // Check too big
        Assert.assertFalse( combinedInterpolator.isValidInterpolatorIndexAtPercent(0, 1f) );

        // Check just right
        Assert.assertTrue( combinedInterpolator.isValidInterpolatorIndexAtPercent(0, 0f) );
        Assert.assertTrue( combinedInterpolator.isValidInterpolatorIndexAtPercent(0, 0.09f) );
        Assert.assertTrue( combinedInterpolator.isValidInterpolatorIndexAtPercent(1, 1f) );
    }

    /**
     * Test case for when we have a single interpolator, starting at 0 percent.
     */
    @Test
    public void test_InterpolationSingle() {
        Interpolator interpolator_0 = new StaticInterpolator(0.5f);

        CombinedInterpolator combinedInterpolator = new CombinedInterpolator();
        combinedInterpolator.addInterpolator(interpolator_0, 0f);

        Assert.assertEquals(0.5f, combinedInterpolator.getInterpolation(0f));
        Assert.assertEquals(0.5f, combinedInterpolator.getInterpolation(0.5f));
        Assert.assertEquals(0.5f, combinedInterpolator.getInterpolation(1f));
    }

    /**
     * Test case for when we have several interpolators bound at different percentages.
     */
    @Test
    public void test_InterpolatorMultiple() {
        float percentage_0 = 0.25f;
        float percentage_1 = 0.5f;
        float percentage_2 = 1.0f;

        Interpolator interpolator_0 = new StaticInterpolator(percentage_0);
        Interpolator interpolator_1 = new StaticInterpolator(percentage_1);
        Interpolator interpolator_2 = new StaticInterpolator(percentage_2);

        CombinedInterpolator combinedInterpolator = new CombinedInterpolator();
        combinedInterpolator.addInterpolator(interpolator_0, 0f);
        combinedInterpolator.addInterpolator(interpolator_1, 0.33f);
        combinedInterpolator.addInterpolator(interpolator_2, 0.66f);

        // Check the interpolation percentages across the second interpolator.
        Assert.assertEquals(0.33f + percentage_1 * 0.33f, combinedInterpolator.getInterpolation(0.33f));
        Assert.assertEquals(0.33f + percentage_1 * 0.33f, combinedInterpolator.getInterpolation(0.6555f));

        // Check the interpolation percentages across the last interpolator.
        Assert.assertEquals(0.66f + percentage_2 * 0.34f, combinedInterpolator.getInterpolation(0.66f));
        Assert.assertEquals(0.66f + percentage_2 * 0.34f, combinedInterpolator.getInterpolation(1.0f));

        // Check the interpolation percentages across the first interpolator.
        // Deliberately tested last to ensure out of order interpolation works.
        Assert.assertEquals(percentage_0 * 0.33f, combinedInterpolator.getInterpolation(0));
        Assert.assertEquals(percentage_0 * 0.33f, combinedInterpolator.getInterpolation(0.2999f));
    }
}
