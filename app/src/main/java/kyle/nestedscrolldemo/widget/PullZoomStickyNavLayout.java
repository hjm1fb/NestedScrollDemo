package kyle.nestedscrolldemo.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * @author dinus
 */
public class PullZoomStickyNavLayout extends PullZoomBaseView<StickyNavLayout> {

    private int originalHeaderHeight;
    private Interpolator smoothToTopInterpolator;
    private ZoomBackRunnable mZoomBackRunnable;
    //    图片原本的高
    private int originalImageViewHeight = 0;

    public PullZoomStickyNavLayout(Context context) {
        this(context, null);
    }

    public PullZoomStickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        originalHeaderHeight = 0;
        smoothToTopInterpolator = createDefaultInterpolator();
        mZoomBackRunnable = new ZoomBackRunnable();
    }

    private Interpolator createDefaultInterpolator() {
        return new DecelerateInterpolator(2.0f);
    }

    @SuppressWarnings("ResourceType")
    @Override
    protected StickyNavLayout createWrapperView(Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    protected int createDefaultPullZoomModel() {
        return ZOOM_HEADER;
    }

    @Override
    protected boolean isReadyZoom() {
        if (mMode == ZOOM_HEADER) {
            return isFirstItemCompletelyVisible();
        } /*else if (mMode == ZOOM_FOOTER) {
            return isLastItemCompletelyVisible();
        }*/

        return false;
    }

    @Override
    protected void pullZoomEvent(float scrollValue) {
        scrollValue = Math.abs(scrollValue);
        float scaleTimes = (getHeaderHeight()+scrollValue)/getHeaderHeight();
        if (scaleTimes > maxScaleTimes  || scaleTimes <0) return;

        if (mZoomBackRunnable != null && !mZoomBackRunnable.isFinished()) {
            mZoomBackRunnable.abortAnimation();
        }

        if (mHeaderContainer != null) {
            ViewGroup.LayoutParams layoutParams = mHeaderContainer.getLayoutParams();
            layoutParams.height = (int) (scrollValue+ getHeaderHeight());
            mHeaderContainer.setLayoutParams(layoutParams);
            zoomImageView(scrollValue);
        }

        /*if (mMode == ZOOM_FOOTER) {
            mWrapperView.scrollToPosition(mWrapperView.getAdapter().getItemCount() - 1);
        }*/
    }

    private void zoomImageView(float scrollValue) {
        scrollValue = Math.abs(scrollValue);
        if(originalImageViewHeight <= 0 && mZoomView instanceof ViewGroup){
            ViewGroup vg = (ViewGroup)mZoomView;
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (vg.getChildAt(i) instanceof ImageView) {
                    originalImageViewHeight = vg.getChildAt(i).getMeasuredHeight();
                    break;
                }
            }
        }
        if(mZoomView instanceof ViewGroup){
            ViewGroup vg = (ViewGroup)mZoomView;
            for(int i = 0; i< vg.getChildCount(); i++){
                if(vg.getChildAt(i) instanceof ImageView){
                    vg.getChildAt(i).getLayoutParams().height = (int) (originalImageViewHeight +scrollValue);
                }else {
                    vg.getChildAt(i).setTranslationY(scrollValue);
                }
            }
        }
    }

    @Override
    protected void smoothScrollToTop() {
        mZoomBackRunnable.startAnimation(mHeaderContainer.getHeight() / getHeaderHeight()*mReplyRatio);
    }

    /*public void setAdapter(RecyclerView.Adapter adapter) {
        mWrapperView.setAdapter(adapter);
    }*/

/*
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mWrapperView.setLayoutManager(manager);
    }
*/

    public void setSmoothToTopInterpolator(Interpolator sSmoothToTopInterpolator) {
        this.smoothToTopInterpolator = sSmoothToTopInterpolator;
    }

    private boolean isFirstItemCompletelyVisible() {
        if (getWrappedView() != null) {
          return getWrappedView().getScrollY()<=0;
        }
        return false;
    }

    private boolean checkFirstItemCompletelyVisible(RecyclerView.LayoutManager mLayoutManager) {
        int firstVisiblePosition = ((RecyclerView.LayoutParams) mLayoutManager.getChildAt(0).getLayoutParams()).getViewPosition();
        if (firstVisiblePosition == 0) {
            final View firstVisibleChild = getWrappedView().getChildAt(0);
            if (firstVisibleChild != null) {
                return firstVisibleChild.getTop() >= getWrappedView().getTop();
            }
        }
        return false;
    }

   /* private boolean isLastItemCompletelyVisible() {
        if (mWrapperView != null) {
            RecyclerView.Adapter adapter = mWrapperView.getAdapter();
            RecyclerView.LayoutManager mLayoutmanager = mWrapperView.getLayoutManager();

            if (null == adapter || adapter.getItemCount() == 0) {
                return true;
            } else if (null == mLayoutmanager || mLayoutmanager.getItemCount() == 0){
                return false;
            } else {
                return checkLastItemCompletelyVisible(mLayoutmanager);
            }
        }

        return false;
    }*/

   /* private boolean checkLastItemCompletelyVisible(RecyclerView.LayoutManager mLayoutmanager) {
        int lastVisiblePosition = mLayoutmanager.getChildCount() - 1;
        int currentLastVisiblePosition = ((RecyclerView.LayoutParams) mLayoutmanager.getChildAt(lastVisiblePosition).getLayoutParams()).getViewPosition();
        if (currentLastVisiblePosition == mLayoutmanager.getItemCount() - 1) {
            final View lastVisibleChild = mWrapperView.getChildAt(lastVisiblePosition);
            if (lastVisibleChild != null) {
                if (mHeaderContainer != null && originalHeaderHeight <= 0) {
                    originalHeaderHeight = mHeaderContainer.getMeasuredHeight();
                }
                return lastVisibleChild.getBottom() <= mWrapperView.getBottom();
            }
        }
        return false;
    }*/

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private class ZoomBackRunnable implements Runnable {
        protected float mDuration;
        protected boolean mIsFinished = true;
        protected float mScale;
        protected long mStartTime;

        ZoomBackRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            if (mZoomView != null && (!mIsFinished) && (mScale > 1.0f)) {
                // fix PullToZoomView bug  ---dinus
                // should not convert the System.currentTimeMillis() to float
                // otherwise the value of (System.currentTimeMillis() - mStartTime) will still be zero
                float zoomBackProgress = (System.currentTimeMillis() - mStartTime) / mDuration;
                ViewGroup.LayoutParams localLayoutParams = mHeaderContainer.getLayoutParams();

                if (zoomBackProgress > 1.0f) {
                    localLayoutParams.height = getHeaderHeight();
                    mHeaderContainer.setLayoutParams(localLayoutParams);
                    zoomImageView(0);
                    mIsFinished = true;
                    return;
                }

                float currentSacle = mScale - (mScale - 1.0F) * smoothToTopInterpolator.getInterpolation(zoomBackProgress);
                localLayoutParams.height = (int) (currentSacle * getHeaderHeight());
                mHeaderContainer.setLayoutParams(localLayoutParams);
                zoomImageView((currentSacle - 1.0F)* originalImageViewHeight);
                post(this);
            }
        }

        public void startAnimation(float animationDuration) {
            if (mZoomView != null) {
                mStartTime = System.currentTimeMillis();
                mDuration = animationDuration;
                mScale = (float) mHeaderContainer.getHeight() / getHeaderHeight();
                mIsFinished = false;
                post(this);
            }
        }
    }

    public int getHeaderHeight() {
        if (mHeaderContainer != null && originalHeaderHeight <= 0) {
            originalHeaderHeight = mHeaderContainer.getMeasuredHeight();
        }
        return originalHeaderHeight;
    }
}
