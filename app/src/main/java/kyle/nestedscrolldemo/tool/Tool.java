package kyle.nestedscrolldemo.tool;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.Window;

/**
 * * Created by hongjunmin on 17/7/5
 *
 * @author hongjunmin
 */

public class Tool {

    private static int statusBarHeight;

    public static int getBlended(int originalColor, int blendedColor,float blendRatio) {
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

    /**
     * 判断当前设备的系统版本是否支持状态栏透明化，4.4以上才支持
     */
    public static boolean isStatusBarTranslucentEnable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }


    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度 单位是px,在华为 SCL-AL00上的高度是50px
     * 此方法需要在view的runnable当中执行，比如
     * blurAlbumImage.post(new Runnable() {
     * @Override public void run() {
     * int statusBarHeight = Tool.getStatusBarHeight(getActivity());
     * }
     * });
     */
    public static int getStatusBarHeight(Activity activity) {
        if (statusBarHeight <= 0) {
            Rect rect = new Rect();
            Window window = activity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            statusBarHeight = rect.top;
        }
        if (statusBarHeight <= 0) {
            return Tool.dp2px(activity.getResources(), 16);
        }
        return statusBarHeight;
    }

    /**
     * @return int
     * @name dip to pixel
     * @desc dip to pixel
     */
    public static int dp2px(Resources resources, float dpValue) {
        final float scale = resources.getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getPixelsFromResource(Resources resources,int dimensionResourceId){
        return (int)(resources.getDimension(dimensionResourceId)+0.5);
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }
}
