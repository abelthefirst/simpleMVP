package ch.doppio.sample.base.mvp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

public class PresenterHolder<T extends Presenter> extends Fragment {

    private static final String TAG = "PRESENTER_HOLDER";

    private T mPresenter;

    static <T extends Presenter> PresenterHolder<T> getInstance(final FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if(fragment == null) {
            fragment = new PresenterHolder<>();

            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(fragment, TAG);
            fragmentTransaction.commit();
        }

        return (PresenterHolder<T>) fragment;
    }

    public PresenterHolder() {
        setRetainInstance(true);
    }

    T getPresenter() {
        return mPresenter;
    }

    void setPresenter(final T presenter) {
        mPresenter = presenter;
    }

}