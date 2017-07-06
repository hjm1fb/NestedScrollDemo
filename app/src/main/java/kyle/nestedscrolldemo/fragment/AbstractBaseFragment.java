package kyle.nestedscrolldemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * * Created by hongjunmin on 16/10/21
 *为了优化Fragment,简化操作,新建了这个Fragment,参照车听宝项目
 * @author hongjunmin
 */

public abstract class AbstractBaseFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();
    protected View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResourceId(),null);
            findViews();
            init();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
    protected abstract int getLayoutResourceId();

    protected abstract void findViews();
    protected abstract void init();

    protected View findViewById(int resId) {
        return mRootView.findViewById(resId);
    }

}
