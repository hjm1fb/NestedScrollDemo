package kyle.nestedscrolldemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import kyle.nestedscrolldemo.R;
import kyle.nestedscrolldemo.tool.Tool;
import kyle.nestedscrolldemo.widget.AppBarStateChangeListener;
import kyle.nestedscrolldemo.widget.RefreshFootView;
import kyle.nestedscrolldemo.widget.RefreshHeaderView;
import kyle.nestedscrolldemo.widget.SimpleRVAdapter;

import static kyle.nestedscrolldemo.R.id.naviBack;

public class ClassifyArticlesActivity extends BaseActivity implements OnRefreshListener,OnLoadMoreListener {

    private ShimmerRecyclerView rvArticles;
    private RefreshHeaderView refreshHeadView;
    private RefreshFootView refreshFootView;
    private SwipeToLoadLayout swipeToLoadLayout;
    private TextView tvHeadTitle;
    private TextView title;
    private AppBarLayout appBar;
    private SimpleRVAdapter simpleRVAdapter = new SimpleRVAdapter(new String[]{});
    private View naviback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_articles);
        findViews();
        init();
    }

    private void findViews() {
        title = (TextView) findViewById(R.id.title);
        tvHeadTitle = (TextView) findViewById(R.id.tv_head_title);
        rvArticles = (ShimmerRecyclerView) findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        refreshHeadView = (RefreshHeaderView) swipeToLoadLayout.getChildAt(0);
        refreshFootView = (RefreshFootView) swipeToLoadLayout.getChildAt(2);
        refreshHeadView.setBackgroundResource(R.color.gray_d3);
        refreshFootView.setBackgroundResource(R.color.gray_d3);
        appBar = (AppBarLayout)findViewById(R.id.app_bar_classify);
        naviback = findViewById(naviBack);
    }

    private void init() {
        naviback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifyArticlesActivity.this.finish();
            }
        });
        /*actionBar.showBottomLine(true);*/
        title.setText("时文精选");
        tvHeadTitle.setText("时文精选");
        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(simpleRVAdapter);
        rvArticles.showShimmerAdapter();
        //进入首页并有缓存数据
        loadArticles(true);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        rvArticles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }

            }
        });
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                swipeToLoadLayout.setRefreshEnabled(state == State.EXPANDED ? true : false);
            }
        });
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float rate = (float) Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange();
                //当appBar中的标题消失时才显示顶部的标题
                float alpha = rate<= getResources().getFraction(R.fraction.classify_articles_title_show_rate,1,1) ? 0 : (float) Math.pow(rate,2);
                //noinspection Range
                title.setAlpha(alpha);
            }
        });
    }


    public static void launch(Context context) {
            Intent intent = new Intent(context, ClassifyArticlesActivity.class);
            context.startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        loadArticles(false);
    }

    /**
     * @param after true表示下拉更新，false表示上拉加载更多
     */
    private void loadArticles(final boolean after) {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
                swipeToLoadLayout.setRefreshing(false);
                if (Tool.isEmpty(simpleRVAdapter.getDataSource())) {
                    rvArticles.hideShimmerAdapter();
                }
                if(after){
                    simpleRVAdapter.setDataSource(new String[]{"节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC",
                            "节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC"});
                }else {
                    ArrayList<String> newList = new ArrayList(Arrays.asList(simpleRVAdapter.getDataSource()));
                    newList.add("节目ABC "+System.currentTimeMillis());
                    String[] newArray = new String[newList.size()];
                    newList.toArray(newArray);
                    simpleRVAdapter.setDataSource(newArray);
                    rvArticles.smoothScrollBy(0, Tool.dp2px(getResources(), rvArticles.getResources().getInteger(R.integer.discovery_load_more_offset)));
                }
                simpleRVAdapter.notifyDataSetChanged();
            }
        },1000);
    }


    @Override
    public void onRefresh() {
        loadArticles(true);
    }
}
