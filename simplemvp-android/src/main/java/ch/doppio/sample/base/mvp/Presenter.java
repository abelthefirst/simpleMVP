package ch.doppio.sample.base.mvp;

import android.app.Activity;

/**
 * Marker interface for presenters.
 */
public interface Presenter<T extends PresenterView> {

    /**
     * This callback is invoked as a part of the corresponding activity's onResume() execution.
     * Please follow {@link Activity#onResume()} guidelines here as well.
     */
    void onAttach(final T view);

    /**
     * This callback is invoked as a part of the corresponding activity's onPause() execution.
     * Please follow {@link Activity#onPause()} guidelines here as well.
     */
    void onDetach();

}