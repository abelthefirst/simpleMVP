package ch.doppio.sample.base.mvp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Basic view implementation.<br />
 * This class should be used in conjunction with {@link BasePresenter} in order to apply MVP pattern.
 */
public abstract class PresenterActivity<T extends Presenter> extends AppCompatActivity
        implements PresenterView {

    protected T mPresenter;

    private boolean mAttached = false;

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(!mAttached) {
            mAttached = true;
            mPresenter.onAttach(this);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPresenter();
    }

    /**
     * Instantiate and return a new {@link BasePresenter} for this activity.
     *
     * @return A new {@link BasePresenter} instance
     */
    protected abstract T onCreatePresenter();

    @Override
    protected void onPause() {
        super.onPause();

        mAttached = false;
        mPresenter.onDetach();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mAttached) {
            mAttached = true;
            mPresenter.onAttach(this);
        }
    }

    private void getPresenter() {
        final PresenterHolder<T> presenterHolder = PresenterHolder.getInstance(getFragmentManager());
        T presenter = presenterHolder.getPresenter();
        if(presenter == null) {
            presenter = onCreatePresenter();
            presenterHolder.setPresenter(presenter);
        }
        mPresenter = presenter;
    }

}