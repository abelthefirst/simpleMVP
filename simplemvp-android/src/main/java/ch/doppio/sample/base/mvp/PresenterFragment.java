package ch.doppio.sample.base.mvp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public abstract class PresenterFragment<T extends Presenter> extends Fragment implements PresenterView {

    protected T mPresenter;

    @Override
    public void finish() {
        final Activity activity = getActivity();
        if(activity != null) {
            activity.finish();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = onCreatePresenter();
    }

    @Override
    public void onPause() {
        super.onPause();

        mPresenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.onAttach(this);
    }

    /**
     * Instantiate and return a new {@link BasePresenter} for this fragment.
     *
     * @return A new {@link BasePresenter} instance
     */
    protected abstract T onCreatePresenter();

}