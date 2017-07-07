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

import com.aspsine.swipetoloadlayout.SwipeLoadMoreFooterLayout;

import kyle.nestedscrolldemo.R;

/**
 * Created by Aspsine on 2015/9/9.
 */
public class RefreshFootView extends SwipeLoadMoreFooterLayout {

    View noFreshingLayout;

    View refreshingingLayout;

    private ImageView ivArrow;

    private TextView tvRefresh;

    private TextView loadMore;

    private ProgressBar progressBar;

    private int mHeaderHeight;

    private Animation rotateUp;

    private Animation rotateDown;

    private int mFooterHeight;

    private boolean rotated = false;

    public RefreshFootView(Context context) {
        this(context, null);
    }

    public RefreshFootView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHeaderHeight = getResources().getDimensionPixelOffset(R.dimen.refresh_header_height);
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_reset);
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_roll);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.refresh_foot_height);
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
    }

    public void onLoadMore() {
        toggleLayout(true);
        ivArrow.clearAnimation();
        tvRefresh.setText("");
    }

    @Override
    public void onPrepare() {
        Log.d("TwitterRefreshHeader", "onPrepare()");
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            toggleLayout(false);
            if (-y >= mFooterHeight) {
                tvRefresh.setText("松开立即加载更多");
                if (!rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateDown);
                    rotated = true;
                }
            } else {
                if (rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateUp);
                    rotated = false;
                }
                tvRefresh.setText("上拉可以加载更多");
            }
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



    private void hideViews(){
        noFreshingLayout.setVisibility(GONE);
        refreshingingLayout.setVisibility(GONE);
        rotated = false;
        ivArrow.clearAnimation();
        tvRefresh.setText("");
    }
}
