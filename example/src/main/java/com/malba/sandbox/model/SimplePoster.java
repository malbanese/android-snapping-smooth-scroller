package com.malba.sandbox.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.malba.sandbox.BR;

/**
 * Simple data model class to use in testing.
 */
public class SimplePoster extends BaseObservable {
    // The title to display.
    private String mTitle;

    /**
     * Creates a new poster instance.
     * @param title The title to display.
     */
    public SimplePoster(String title) {
        mTitle = title;
    }

    /**
     * @return The title of this poster.
     */
    @Bindable
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title The title of this poster.
     */
    public void setTitle(String title) {
        mTitle = title;
        notifyPropertyChanged(BR.title);
    }
}
