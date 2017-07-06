package kyle.nestedscrolldemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static kyle.nestedscrolldemo.widget.PlayerHeadZoomScrollView.DEFAULT_MAX_SCALE_TIMES;
import static kyle.nestedscrolldemo.widget.PlayerHeadZoomScrollView.DEFAULT_REPLAY_RATIO;
import static kyle.nestedscrolldemo.widget.PlayerHeadZoomScrollView.DEFAULT_SCALE_RATIO;

/**
 *
 * @author dinus
 */
public abstract class PullZoomBaseView<T extends View> extends LinearLayout {

    //    滑动放大系数，系数越大，滑动时放大程度越大
    protected float scaleRatio = DEFAULT_SCALE_RATIO;

    //    最大的放大倍数
    protected float maxScaleTimes = DEFAULT_MAX_SCALE_TIMES;

    //    回弹时间系数，系数越小，回弹越快
    protected float mReplyRatio = DEFAULT_REPLAY_RATIO;

    public static final int ZOOM_HEADER = 0;
    public static final int ZOOM_FOOTER = 1;

    protected T mWrapperView;
    protected ViewGroup mHeaderContainer;
    protected View mZoomView;

    private float mInitTouchY;
    private float mInitTouchX;
    private float mLastTouchX;
    private float mLastTouchY;

    private boolean isZoomEnable;
    private boolean isZooming;
    private boolean isPullStart;

    protected int mMode;
    private int mTouchSlop;

    private OnPullZoomListener mOnPullZoomListener;

    public PullZoomBaseView(Context context) {
        this(context, null);
    }

    public PullZoomBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMode = createDefaultPullZoomModel();

        isZoomEnable = true;
        isPullStart = false;
        isZooming = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isZoomEnable) {
            return false;
        }

        if (event.getEdgeFlags() != 0 && event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return performTouchAction(event);
    }

    private boolean performTouchAction(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // do nothing
                // the init action has been done in the function onInterceptTouchEvent
                break;

            case MotionEvent.ACTION_MOVE:
                if (isPullStart) {
                    return onPullStartActionMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isPullStart) {
                    return onPullStartActionCancel();
                }
                break;
            default:
                break;
        }
        return false;
    }

    private boolean onPullStartActionMove(MotionEvent event) {
        isZooming = true;
        mLastTouchY = event.getY();
        mLastTouchX = event.getX();

        float scrollValue = mMode == ZOOM_HEADER ?
                Math.round(Math.min(mInitTouchY - mLastTouchY, 0) * scaleRatio)
                : Math.round(Math.max(mInitTouchY - mLastTouchY, 0) * scaleRatio);
        pullZoomEvent(scrollValue);

        if (mOnPullZoomListener != null){
            mOnPullZoomListener.onPullZooming(scrollValue);
        }

        return true;
    }

    private boolean onPullStartActionCancel() {
        isPullStart = false;
        if (isZooming){
            isZooming = false;
            smoothScrollToTop();

            if (mOnPullZoomListener != null){
                final float scrollValue = mMode == ZOOM_HEADER ?
                        Math.round(Math.min(mInitTouchY - mLastTouchY, 0) * scaleRatio)
                        : Math.round(Math.max(mInitTouchY - mLastTouchY, 0) * scaleRatio);

                mOnPullZoomListener.onPullZoomEnd(scrollValue);
            }
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (!isZoomEnable){
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && isPullStart){
            return true;
        }

        performInterceptAction(event);
        return isPullStart;
    }

    private void performInterceptAction(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (isReadyZoom()){
                    onZoomReadyActionDown(event);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isReadyZoom()) {
                    onZoomReadyActionMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // do nothing
                // the reset action will be done in the function onTouchEvent
                break;
            default:
                break;
        }
    }

    private void onZoomReadyActionDown(MotionEvent event) {
        mInitTouchY = mLastTouchY = event.getY();
        mInitTouchX = mLastTouchX = event.getX();
        isPullStart = false;
    }

    private void onZoomReadyActionMove(MotionEvent event) {
        float mCurrentX = event.getX();
        float mCurrentY = event.getY();

        float xDistance = mCurrentX - mLastTouchX;
        float yDistance = mCurrentY - mLastTouchY;

        Log.i("debug", "mMode" + mMode + "yDistance " + yDistance + "xDistance " + xDistance);
        if (mMode == ZOOM_HEADER && yDistance > mTouchSlop && yDistance > Math.abs(xDistance)
                || mMode == ZOOM_FOOTER && -yDistance > mTouchSlop && -yDistance > Math.abs(xDistance)){
            mLastTouchY = mCurrentY;
            mLastTouchX = mCurrentX;

            if (mOnPullZoomListener != null){
                mOnPullZoomListener.onPullStart();
            }
            isPullStart = true;
        }
    }

    public void setModel(int mModel) {
        this.mMode = mModel;
    }

    public void setOnPullZoomListener(OnPullZoomListener mOnPullZoomListener) {
        this.mOnPullZoomListener = mOnPullZoomListener;
    }

    public void setIsZoomEnable(boolean isZoomEnable) {
        this.isZoomEnable = isZoomEnable;
    }

    public void setZoomView(View mZoomView) {
        this.mZoomView = mZoomView;
    }

    public void setHeaderContainer(ViewGroup mHeaderContainer) {
        this.mHeaderContainer = mHeaderContainer;
    }

    public boolean isZoomEnable() {
        return isZoomEnable;
    }

    public boolean isZooming() {
        return isZooming;
    }

    protected abstract T createWrapperView(Context context, AttributeSet attrs);

    protected abstract int createDefaultPullZoomModel();

    protected abstract boolean isReadyZoom();

    /**
     * @param scrollValue vertical distance scrolled in pixels
     *          if scrollValue < 0  ; scroll up
     *          if scrollValue > 0  ; scroll down
     */
    protected abstract void pullZoomEvent(float scrollValue);

    protected abstract void smoothScrollToTop();

    public interface OnPullZoomListener {
        void onPullZooming(float newScrollValue);

        void onPullStart();

        void onPullZoomEnd(float newScrollValue);
    }

    public T getWrappedView(){
        if(mWrapperView == null){
            if(getChildCount()>0){
                mWrapperView = (T) getChildAt(0);
            }
        }
        return mWrapperView;
    }
}
