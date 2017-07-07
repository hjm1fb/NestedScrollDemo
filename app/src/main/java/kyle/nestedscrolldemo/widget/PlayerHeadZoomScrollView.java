package kyle.nestedscrolldemo.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import kyle.nestedscrolldemo.R;


/**
 * 下拉缩放上方布局,松开回弹的scrollView
 */

public class PlayerHeadZoomScrollView extends ScrollView {

    //    最大的放大倍数
    public static final float DEFAULT_MAX_SCALE_TIMES = 1.8f;

    //    滑动放大系数，系数越大，滑动时放大程度越大
    public static final float DEFAULT_SCALE_RATIO = 1.5f;

    //    回弹时间系数，系数越小，回弹越快
    public static final float DEFAULT_REPLAY_RATIO = 0.8f;

    public PlayerHeadZoomScrollView(Context context) {
        super(context);
    }

    public PlayerHeadZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerHeadZoomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ZoomDirection zoomDirection = ZoomDirection.VERTICAL;

    //    用于记录下拉位置
    private float y = 0f;
    //    zoomView原本的宽高
    private int zoomViewWidth = 0;
    private int zoomViewHeight = 0;

    //    是否正在放大
    private boolean mScaling = false;

    //    放大的view，默认为第一个子view
    private View zoomView;
    public void setZoomView(View zoomView) {
        this.zoomView = zoomView;
    }

    //    滑动放大系数，系数越大，滑动时放大程度越大
    private float scaleRatio = DEFAULT_SCALE_RATIO;
    //    最大的放大倍数
    private float maxScaleTimes = DEFAULT_MAX_SCALE_TIMES;

    //    回弹时间系数，系数越小，回弹越快
    private float mReplyRatio = DEFAULT_REPLAY_RATIO;
    public void setmReplyRatio(float mReplyRatio) {
        this.mReplyRatio = mReplyRatio;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        不可过度滚动，否则上移后下拉会出现部分空白的情况
        setOverScrollMode(OVER_SCROLL_NEVER);
//        获得默认第一个view
        if (getChildAt(0) != null && getChildAt(0) instanceof ViewGroup && zoomView == null) {
            ViewGroup vg = (ViewGroup) getChildAt(0);
            if (vg.getChildCount() > 0) {
                zoomView = vg.getChildAt(0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (zoomViewWidth <= 0 || zoomViewHeight <=0) {
            zoomViewWidth = zoomView.getMeasuredWidth();
            zoomViewHeight = zoomView.getMeasuredHeight();
        }
        if (zoomView == null || zoomViewWidth <= 0 || zoomViewHeight <= 0) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!mScaling) {
                    if (getScrollY() == 0) {
                        y = ev.getY();//滑动到顶部时，记录位置
                    } else {
                        break;
                    }
                }
                int distance = (int) ((ev.getY() - y)* scaleRatio);
                if (distance < 0) break;//若往下滑动
                mScaling = true;
                setZoom(distance);
                return true;
            case MotionEvent.ACTION_UP:
                mScaling = false;
                replyView();
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**放大view*/
    private void setZoom(float s) {
        float scaleTimes = (float) ((zoomViewHeight+s)/(zoomViewHeight*1.0));
//        如超过最大放大倍数，直接返回
        if (scaleTimes > maxScaleTimes || scaleTimes <0) return;
        setZoomParamLayout(s);
    }

    /**回弹*/
    private void replyView() {
         float distance = 0;
        if(ZoomDirection.HORIZONTAL.equals(zoomDirection) || ZoomDirection.CIRCLE.equals(zoomDirection)){
            distance = zoomView.getMeasuredWidth() - zoomViewWidth;
        }
        if(ZoomDirection.VERTICAL.equals(zoomDirection) || ZoomDirection.CIRCLE.equals(zoomDirection)){
            distance = zoomView.getMeasuredHeight() - zoomViewHeight;
        }
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(distance, 0.0F).setDuration((long) (distance * mReplyRatio));
        Interpolator interpolator = new DecelerateInterpolator();
        anim.setInterpolator(interpolator);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setZoom((Float) animation.getAnimatedValue());
            }
        });
        anim.start();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener!=null) onScrollListener.onScroll(l,t,oldl,oldt);
    }

    private OnScrollListener onScrollListener;
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**滑动监听*/
    public  interface OnScrollListener{
        void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }

    private int lastScrollY = 0;

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY>=0) {
            float persent = scrollY * 1f / (mByWhichView.getTop() + mByWhichView.getMeasuredHeight());
            int alpha = (int) (255 * persent);
            if (alpha > 255){
                alpha = 255;
            }
            Log.d("dhc", "alpha "+alpha);
            int color = Color.argb(alpha,255,255,255);
            changeView.setBackgroundColor(color);

            int statusColor = Color.argb(alpha,0,0,0);
            statusBgView.setBackgroundColor(statusColor);

            int tmp = 255 - alpha;
            int textColor = Color.argb(255,tmp,tmp,tmp);
            tvTitle.setTextColor(textColor);
            lastScrollY = scrollY;

            if (tmp > 125){
                ivMore.setImageResource(R.drawable.xml_player_more);
                ivSwipe.setImageResource(R.drawable.xml_player_back);
            }else{
                ivMore.setImageResource(R.drawable.xml_player_more_black);
                ivSwipe.setImageResource(R.drawable.xml_player_back_black);
            }
        }
    }

    private View changeView;

    private View mByWhichView;

    private TextView tvTitle;

    private ImageView ivMore;

    private ImageView ivSwipe;

    private View statusBgView;

    public void setNeedChangeView(View view){
        changeView = view;
    }

    public void setStatusBgView(View statusBgView){
        this.statusBgView = statusBgView;
    }

    public void setupByWhichView(View view) {
        mByWhichView = view;
    }

    public void setTextView(TextView textView){
        tvTitle = textView;
    }

    public void setMoreView(ImageView imageView){
        ivMore = imageView;
    }
    public void setSwipeView(ImageView imageView){
        ivSwipe = imageView;
    }


    enum ZoomDirection {

        HORIZONTAL(),
        VERTICAL(),
        CIRCLE();

    }


    public ZoomDirection getZoomDirection() {
        return zoomDirection;
    }

    public void setZoomDirection(ZoomDirection zoomDirection){
        this.zoomDirection = zoomDirection;
    }

    private void setZoomParamLayout(float s) {
        ViewGroup.LayoutParams layoutParams = zoomView.getLayoutParams();
        if(ZoomDirection.HORIZONTAL.equals(zoomDirection) || ZoomDirection.CIRCLE.equals(zoomDirection)){
            layoutParams.width = (int) (zoomViewWidth + s);
            //        设置控件水平居中
            ((MarginLayoutParams) layoutParams).setMargins(-(layoutParams.width - zoomViewWidth) / 2, 0, 0, 0);
        }
        if(ZoomDirection.VERTICAL.equals(zoomDirection) || ZoomDirection.CIRCLE.equals(zoomDirection)){
            layoutParams.height = (int)(zoomViewHeight*((zoomViewWidth+s)/zoomViewWidth));
        }
        zoomView.setLayoutParams(layoutParams);
    }
}