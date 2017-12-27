package com.malba.sandbox.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.malba.sandbox.BR;

public class Movie extends BaseObservable {
    private String title;

    public Movie(String title) {
        this.title = title;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }
}
