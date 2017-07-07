package kyle.nestedscrolldemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeRefreshHeaderLayout;

import kyle.nestedscrolldemo.R;

/**
 * Created by Aspsine on 2015/9/9.
 */
public class RefreshHeaderView extends SwipeRefreshHeaderLayout {

    View noFreshingLayout;

    View refreshingingLayout;

    private ImageView ivArrow;

    private TextView tvRefresh;

    private TextView lastRefresh;

    private TextView loadMore;

    private ProgressBar progressBar;

    private int mHeaderHeight;

    private Animation rotateUp;

    private Animation rotateDown;

    private boolean rotated = false;

    private long lastUpdateTimeInMillionSeconds;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.refresh_header_height);
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_roll);
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_reset);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        noFreshingLayout = findViewById(R.id.not_refreshing_layout);
        refreshingingLayout = findViewById(R.id.refreshing_layout);
        tvRefresh = (TextView) findViewById(R.id.tvRefresh);
        ivArrow = (ImageView) findViewById(R.id.ivArrow);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        loadMore = (TextView) findViewById(R.id.tv_load_more);
        lastRefresh = (TextView) findViewById(R.id.lastRefresh);
    }

    @Override
    public void onRefresh() {
        toggleLayout(true);
        ivArrow.clearAnimation();
        tvRefresh.setText("");
    }

    @Override
    public void onPrepare() {
        lastRefresh.setText("上次刷新:");
        Log.d("TwitterRefreshHeader", "onPrepare()");
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (y > mHeaderHeight) {
                tvRefresh.setText("松开立即刷新");
                if (!rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateUp);
                    rotated = true;
                }
            } else if (y < mHeaderHeight) {
                if (rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateDown);
                    rotated = false;
                }
                tvRefresh.setText("下拉可刷新");
            }

            toggleLayout(false);
        }
    }

    @Override
    public void onRelease() {
        Log.d("TwitterRefreshHeader", "onRelease()");
    }

    private void toggleLayout(boolean isRefreshing) {
        noFreshingLayout.setVisibility(isRefreshing ? GONE : VISIBLE);
        refreshingingLayout.setVisibility(isRefreshing ? VISIBLE : GONE);
    }

    @Override
    public void onComplete() {
        hideViews();
    }

    @Override
    public void onReset() {
        hideViews();
    }


    private void hideViews() {
        noFreshingLayout.setVisibility(GONE);
        refreshingingLayout.setVisibility(GONE);
        rotated = false;
        ivArrow.clearAnimation();
        tvRefresh.setText("");
    }

    public void setLastUpdateTimeInMillionSeconds(long lastUpdateTimeInMillionSeconds) {
        this.lastUpdateTimeInMillionSeconds = lastUpdateTimeInMillionSeconds;
    }

    public void hidelastRefresh(){
        lastRefresh.setVisibility(View.GONE);
    }
}
