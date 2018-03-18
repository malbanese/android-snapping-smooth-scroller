package com.malba.sandbox.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import com.malba.sandbox.BR;

public abstract class InputSectionSnapperViewModel extends BaseObservable {
    private String mTitle;

    /**
     * @param title The title to display for this text.
     */
    public InputSectionSnapperViewModel(String title) {
        mTitle = title;
    }

    /**
     * Sets the value associated with this input section.
     */
    public void setValue(float value) {
        notifyPropertyChanged(BR.value);
        notifyPropertyChanged(BR.valueString);
        notifyPropertyChanged(BR.valuePercent);
    }

    /**
     * @return The value associated with this input section
     */
    @Bindable
    public abstract float getValue();

    /**
     * @return The String value of getValue()
     */
    @Bindable
    public String getValueString() {
        return String.valueOf(getValue());
    }

    /**
     * @param value Sets the string value.
     */
    public void setValueString(String value) {
        try {
            setValue(Float.parseFloat(value));
        } catch (NumberFormatException err) {
            setValue(getValue());
        }
    }

    /**
     * @param percent Sets the percent value.
     */
    public void setValuePercent(int percent) {
        setValue(percent / 100f);
    }

    /**
     * @return The 0-100 percent value of getValue()
     */
    @Bindable
    public int getValuePercent() {
        return (int)(100 * getValue());
    }

    /**
     * @return The title associated with this section.
     */
    @Bindable
    public String getTitle() {
        return mTitle;
    }
}
