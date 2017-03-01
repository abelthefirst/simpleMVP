package ch.doppio.sample.base.mvp;

import android.app.Activity;
import android.support.annotation.Nullable;

/**
 * Marker interface for views.
 */
public interface PresenterView {

    void finish();

    @Nullable
    Activity getActivity();

}