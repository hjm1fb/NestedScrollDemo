package kyle.nestedscrolldemo.tool;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * * Created by hongjunmin on 17/3/16
 *
 * @author hongjunmin
 */

/**
 * UI辅助类，比如自适应drawable, 混色，EditText设置
 */
public class UIHelper {

    /**
     * 根据文字大小自适配drawable
     * 设置右侧的ico时会有bug,即文字显示不全，推荐使用Textview.setCompoundDrawablesWithIntrinsicBounds
     * 如果非要用此方法，就在setText的时候末尾加几个空格
     * @param textView
     * @param leftDrawable
     * @param topDrawable
     * @param rightDrawable
     * @param bottomDrawable
     */
    public static void ajustCompoundDrawableSizeWithText(final TextView textView, final Drawable leftDrawable, final Drawable topDrawable, final Drawable rightDrawable, final Drawable bottomDrawable) {
       //API大于11时才能自适应
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            textView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if(leftDrawable != null){
                        leftDrawable.setBounds(0, 0, (int)textView.getTextSize(), (int)textView.getTextSize());
                    }
                    if(topDrawable != null){
                        topDrawable.setBounds(0, 0, (int)textView.getTextSize(), (int)textView.getTextSize());
                    }
                    if(rightDrawable != null){
                        rightDrawable.setBounds(0, 0, (int)textView.getTextSize(), (int)textView.getTextSize());
                    }
                    if(bottomDrawable != null){
                        bottomDrawable.setBounds(0, 0, (int)textView.getTextSize(), (int)textView.getTextSize());
                    }
                    textView.setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        textView.removeOnLayoutChangeListener(this);
                    }
                }
            });
        }else {
            textView.setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
        }
    }

    /**
     * 根据文字大小自适配drawable
     * 设置右侧的ico时会有bug,即文字显示不全，推荐使用Textview.setCompoundDrawablesWithIntrinsicBounds
     * 如果非要用此方法，就在setText的时候末尾加几个空格
     * @param textView
     * @param leftDrawableResId
     * @param topDrawableResId
     * @param rightDrawableResId
     * @param bottomDrawableResId
     */
    public static void ajustCompoundDrawableSizeWithText(final TextView textView, int leftDrawableResId, int topDrawableResId, int rightDrawableResId, int bottomDrawableResId) {
        Drawable leftDrawable = null;
         Drawable topDrawable = null;
         Drawable rightDrawable = null;
         Drawable bottomDrawable = null;

        if(leftDrawableResId != 0){
            leftDrawable  = ContextCompat.getDrawable(textView.getContext(),leftDrawableResId);
        }if(topDrawableResId != 0){
            topDrawable = ContextCompat.getDrawable(textView.getContext(),topDrawableResId);
        }if(rightDrawableResId != 0){
          rightDrawable = ContextCompat.getDrawable(textView.getContext(),rightDrawableResId);
        }if(bottomDrawableResId != 0) {
           bottomDrawable = ContextCompat.getDrawable(textView.getContext(),bottomDrawableResId);
        }
        ajustCompoundDrawableSizeWithText(textView,leftDrawable,topDrawable,rightDrawable,bottomDrawable);
    }

    /**
     *
     * 简单配置TabLayout的快捷方法
     * @param tabLayout
     * @param titles 标题数组
     * @param layoutResId textView所使用的布局文件，TextView为根节点
     */
    public static void setUpTabLayout(TabLayout tabLayout, String[] titles, int layoutResId){
        if(!Tool.isEmpty(titles)){
            for(int i = 0; i<titles.length;i++){
                TextView tabTitle = (TextView) LayoutInflater.from(tabLayout.getContext())
                        .inflate(layoutResId, null);
                tabTitle.setText(titles[i]);
                tabLayout.getTabAt(i).setCustomView(tabTitle);
                tabLayout.getTabAt(i).setTag(tabTitle);
                if(i ==0){
                    tabTitle.setSelected(true);
                }
            }
        }
    }


    /**
     *
     * 实现混色，支持alpha通道
     * @param originalColor 初始颜色 格式为ARGB 比如白色为 0xFFFFFFFF
     * @param blendedColor 要混入的颜色 格式为ARGB
     * @param blendRatio 混入的比例
     * @return 混后的颜色，格式为ARGB
     */
    public static int getBlendedColor(int originalColor, int blendedColor,float blendRatio) {
        int newColor = 0;
        int originalAlpha = Color.alpha(originalColor);
        int originalR = Color.red(originalColor);
        int originalG = Color.green(originalColor);
        int originalB = Color.blue(originalColor);
        int blendedAlpha = Color.alpha(blendedColor);
        int blendedR = Color.red(blendedColor);
        int blendedG = Color.green(blendedColor);
        int blendedB = Color.blue(blendedColor);
        int newAlpha = (int) (originalAlpha*(1-blendRatio) +blendedAlpha*blendRatio);
        int newR = (int) (originalR*(1-blendRatio) +blendedR*blendRatio);
        int newG = (int) (originalG*(1-blendRatio) +blendedG*blendRatio);
        int newB = (int) (originalB*(1-blendRatio) +blendedB*blendRatio);
        newColor = Color.argb(newAlpha, newR, newG, newB);
        return newColor;
    }


    public static void showToastWithoutRepetition(final Toast toast) {
        if (toast != null && (toast.getView() == null || !toast.getView().isShown()))
            toast.show();
    }

    /**
     * 将输入框的enter事件捆绑到另一个View（通常是按钮）的click事件上。
     * 使用场景：
     * 比如在搜索页编辑搜索词后，点击键盘上的enter相当于点击搜索按钮
     *
     * @param et 用于输入的EditText
     * @param actionView 与输入对应的动作视图，比如登录 搜索
     */
    public static void bindViewClickToEditText(@NonNull EditText et, @NonNull final View actionView) {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isEnterKeyEvent = null!=event && KeyEvent.KEYCODE_ENTER==event.getKeyCode();
                boolean isKeyboardDone = false;
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_SEARCH:
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_GO:
                    case EditorInfo.IME_ACTION_SEND:
                    case EditorInfo.IME_ACTION_UNSPECIFIED:
                        isKeyboardDone = true;
                    default:
                        break;
                }
                if (isEnterKeyEvent || isKeyboardDone) {
                    actionView.performClick();
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            try {
                InputMethodManager immService = ((InputMethodManager) activity.getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE));
                immService.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开软键盘
     * @param view  与软键盘对应的View，比如EditTextView
     * @param activity
     */
    public static void showSoftInput(Activity activity, View view) {
        if (view != null && activity != null && !activity.isFinishing()) {
            try {
                InputMethodManager immService = ((InputMethodManager) activity.getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE));
                immService.showSoftInput(view,
                        InputMethodManager.SHOW_IMPLICIT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void initSystemBar(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            // 使用颜色资源
            tintManager.setStatusBarTintResource(colorId);
        }
    }

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


    public static void slide(boolean slideIn, final View view,final ValueAnimator slideInAni,final ValueAnimator slideOutAni) {
        view.clearAnimation();
        if (slideIn) {
            slideInAni.removeAllUpdateListeners();
            slideInAni.removeAllListeners();
            slideInAni.setTarget(view);
            slideInAni.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          slide(false, view,slideInAni,slideOutAni);
                        }
                    }, 3000);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            slideInAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatorValue = Integer.valueOf(animation.getAnimatedValue() + "");
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = animatorValue;
                    view.setLayoutParams(params);
                }
            });
            slideInAni.start();
        } else {
            slideOutAni.setTarget(view);
            slideOutAni.removeAllUpdateListeners();
            slideOutAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatorValue = Integer.valueOf(animation.getAnimatedValue() + "");
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = animatorValue;
                    view.setLayoutParams(params);
                }
            });
            slideOutAni.start();
        }
    }
}
