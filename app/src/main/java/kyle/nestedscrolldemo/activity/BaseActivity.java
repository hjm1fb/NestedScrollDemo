package kyle.nestedscrolldemo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import kyle.nestedscrolldemo.R;
import kyle.nestedscrolldemo.tool.SystemBarTintManager;
import kyle.nestedscrolldemo.tool.UIHelper;

/**
 * Created by datong on 17/3/28.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 是否设置状态栏颜色
     */
    private boolean updateStatusBar = true;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(updateStatusBar){
            updateStatusBar();
        }
    }


    public static void initSystemBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            setTranslucentStatus(activity, true);

        }

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);

        tintManager.setStatusBarTintEnabled(true);

// 使用颜色资源

        tintManager.setStatusBarTintResource(R.color.white);

    }

    @TargetApi(19)

    private static void setTranslucentStatus(Activity activity, boolean on) {

        Window win = activity.getWindow();

        WindowManager.LayoutParams winParams = win.getAttributes();

        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {

            winParams.flags |= bits;

        } else {

            winParams.flags &= ~bits;

        }

        win.setAttributes(winParams);

    }


    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 当前Activity是否已经结束了其生命周期
     */
    public boolean isInLifeCycle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !this.isFinishing() && ! this.isDestroyed();
        } else {
            return !this.isFinishing();
        }
    }

    protected void updateStatusBar() {
        updateStatusBar(R.color.red31);
    }

    protected void updateStatusBar(@DrawableRes int colorResId) {
        UIHelper.initSystemBar(this, colorResId);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        onVisibilityChange(true);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        onVisibilityChange(false);
    }

    @CallSuper
    protected void onVisibilityChange(boolean visible){
    }

    public void setUpdateStatusBar(boolean updateStatusBar) {
        this.updateStatusBar = updateStatusBar;
    }
}
