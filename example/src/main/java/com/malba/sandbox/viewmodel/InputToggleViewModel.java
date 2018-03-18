package com.malba.sandbox.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.malba.sandbox.BR;

public abstract class InputToggleViewModel extends BaseObservable {
    private String mTitle;

    /**
     * @param title The title to display for this text.
     */
    public InputToggleViewModel(String title) {
        mTitle = title;
    }

    /**
     * Sets the value associated with this input section.
     */
    public void setValue(boolean value) {
        notifyPropertyChanged(BR.value);
    }

    /**
     * @return The value associated with this input section
     */
    @Bindable
    public abstract boolean getValue();

    /**
     * @return The title associated with this section.
     */
    @Bindable
    public String getTitle() {
        return mTitle;
    }
}
