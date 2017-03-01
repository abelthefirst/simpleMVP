package ch.doppio.sample.base.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;

/**
 * Basic presenter implementation.
 */
public abstract class BasePresenter<T extends PresenterView> implements Presenter<T> {

    private final T mEmptyView;

    protected T mView;

    protected BasePresenter() {
        mEmptyView = onCreateEmptyView();
        mView = mEmptyView;
    }

    @CallSuper
    @Override
    public void onAttach(final T view) {
        mView = view;
    }

    @CallSuper
    @Override
    public void onDetach() {
        mView = mEmptyView;
    }

    /**
     * Instantiate and return an empty implementation of a view,<br />
     * which is going to be used when a real view is unavailable.
     *
     * @return An empty view implementation.
     */
    protected abstract T onCreateEmptyView();

    protected void startView(final Class<?extends Activity> clazz) {
        startView(clazz, new Bundle());
    }

    protected void startView(final Class<?extends Activity> clazz, final Bundle extras) {
        final Context context = mView.getActivity();
        if(context != null) {
            final Intent intent = new Intent(context, clazz);
            intent.putExtras(extras);
            context.startActivity(intent);
        }
    }

    protected void startView(final Class<?extends Activity> clazz, final int requestCode) {
        final Activity activity = mView.getActivity();
        if(activity != null) {
            final Intent intent = new Intent(activity, clazz);
            activity.startActivityForResult(intent, requestCode);
        }
    }

}