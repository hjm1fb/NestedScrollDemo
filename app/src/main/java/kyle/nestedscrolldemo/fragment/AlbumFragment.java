package kyle.nestedscrolldemo.fragment;

/**
 * * Created by hongjunmin on 16/10/21
 *
 * @author hongjunmin
 */

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import kyle.nestedscrolldemo.R;
import kyle.nestedscrolldemo.tool.Tool;
import kyle.nestedscrolldemo.widget.PullZoomStickyNavLayout;
import kyle.nestedscrolldemo.widget.StickyNavLayout;

import static kyle.nestedscrolldemo.R.id.naviBack;

public class AlbumFragment extends AbstractBaseFragment {


    private static final int REQUEST_LOGIN_FOR_TRACK = 2;

    private static final int REQUEST_LOGIN_FOR_FOLLOW = 50001;

    private int screenHeightPixels;

    private LayoutInflater layoutInflater;

    private Context context;


    private ImageView fakeNaviBack;
    private PagerSlidingTabStrip fragmentsTab;
    private ViewPager viewPager;
    private StickyNavLayout stickyNav;
    private ViewGroup actionLayout;
    private LinearLayout fakeActionLayout;
    private TextView actionLayoutTitle;
    private View statusBarBackGround;

    private AlbumPageFragmentsAdapter fragmentsPagerAdapter;

    private ImageView blurAlbumImage;

    private int actionLayoutHeight;


    private DownloadManager downloadManager;

    private StickyNavLayout.OnScrollChangeListener onScrollChangeListener = new StickyNavLayout.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int maxScrollY) {
            float scrollPercent = 0;
            //条件验证
            if (scrollY >= 0 && maxScrollY > 0 && maxScrollY >= scrollY) {
                scrollPercent = scrollY * 1f / maxScrollY;
                //改变actionLayout透明度
                ViewCompat.setAlpha(actionLayout, scrollPercent);
                //改变fakeActionLayout透明度
                ViewCompat.setAlpha(fakeActionLayout, 1 - scrollPercent);
                //设置渐变色，并防止双层显示不美观
                int gradualColor = Tool.getBlended(Color.WHITE, Color.BLACK,scrollPercent);
                fakeNaviBack.setColorFilter(gradualColor);
                if(Tool.isStatusBarTranslucentEnable()){
                    //为了更充分的浸入式体验，连乘处理以够降低黑色状态栏显示的程度，从而底部图片得到更多的展示，滑到底时仍完全显示黑色状态栏
                    ViewCompat.setAlpha(statusBarBackGround, scrollPercent*scrollPercent*scrollPercent);
                }
            }
        }
    };




    public AlbumFragment() {

    }

    public static AlbumFragment newInstance() {
        AlbumFragment albumFragment = new AlbumFragment();
        return albumFragment;
    }

    protected void findViews() {
        //stickyNav
        blurAlbumImage = (ImageView)findViewById(R.id.blurAlbumImage);
        stickyNav = (StickyNavLayout) findViewById(R.id.stickyNav);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentsTab = (PagerSlidingTabStrip) findViewById(R.id.fragmentsTab);
        PullZoomStickyNavLayout pullZoomView = (PullZoomStickyNavLayout) findViewById(R.id.pullZoomView);
        pullZoomView.setHeaderContainer((ViewGroup) findViewById(R.id.topView));
        pullZoomView.setZoomView(findViewById(R.id.topView));
        //actionLayout
        actionLayout = (ViewGroup) findViewById(R.id.actionLayout);
        actionLayoutTitle = (TextView) actionLayout.findViewById(R.id.title);
        actionLayoutTitle.setText("沉浸式嵌套滑动 and 渐变");
        statusBarBackGround = actionLayout.findViewById(R.id.statusBarBackGround);
        //fakeActionLayout
        fakeActionLayout = (LinearLayout) findViewById(R.id.fakeActionLayout);
        fakeActionLayout.findViewById(R.id.barRoot).setBackgroundColor(fakeActionLayout.getResources().getColor(R.color.transparent));
        fakeNaviBack = (ImageView) fakeActionLayout.findViewById(naviBack);
        fakeNaviBack.setImageResource(R.drawable.white_ico);
        fragmentsTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fakeNaviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        stickyNav.setOnScrollChangeListener(onScrollChangeListener);
    }

    @Override
    protected void init() {
        //无论是否有数据，返回键可以点击
            loadData();
            findViews();
            //沉浸式状态栏
            blurAlbumImage.post(new Runnable() {
                @Override
                public void run() {
                    actionLayoutHeight = actionLayout.getHeight();
                    try {
                    if (Tool.isStatusBarTranslucentEnable()) {
                        //已经在AlbumTrackListActivity getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        int statusBarHeight = Tool.getStatusBarHeight(getActivity());
                        //返回按钮
                        findViewById(R.id.contentInfoLayout).setPadding(0, Tool.getStatusBarHeight(getActivity()), 0, 0);
                        //模糊图
                        RelativeLayout.LayoutParams blurAlbumImageLayoutParams = (RelativeLayout.LayoutParams) blurAlbumImage.getLayoutParams();
                        blurAlbumImageLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        blurAlbumImageLayoutParams.height = Tool.getPixelsFromResource(actionLayout.getResources(), R.dimen.album_fragment_blur_image_height) + statusBarHeight;
                        blurAlbumImage.setLayoutParams(blurAlbumImageLayoutParams);
                        //tab
                     /*   LinearLayout.LayoutParams fragmentsTabLayoutParams = (LinearLayout.LayoutParams) fragmentsTab.getLayoutParams();
                        fragmentsTabLayoutParams.setMargins(0, statusBarHeight, 0, 0);
                        fragmentsTab.setLayoutParams(fragmentsTabLayoutParams);*/
                        //虽然actionLayout设置了padding 但直到绘制下一帧actionLayout的getHeight()才会返回含padding的值，
                        fakeActionLayout.setPadding(0, statusBarHeight, 0, 0);
                        stickyNav.setActionLayoutHeight(actionLayoutHeight + statusBarHeight);
                        ViewGroup.LayoutParams statusBarBgLayoutParams = statusBarBackGround.getLayoutParams();
                        statusBarBgLayoutParams.height = statusBarHeight;
                        statusBarBackGround.setLayoutParams(statusBarBgLayoutParams);
                    } else {
                        stickyNav.setActionLayoutHeight(actionLayoutHeight);
                    }
                    }catch (Exception e){
                     e.printStackTrace();
                    }
                }
            });

            //init data
    }

    private void loadData() {
            doLoadDefaultData();
    }

    private void doLoadDefaultData() {
        initData();
        actionLayout.setAlpha(0);
        blurAlbumImage.setVisibility(View.VISIBLE);
    }



    private void initData() {
            List<Fragment> fragments = new ArrayList<>();
            fragments.add(AlbumTracksFragment.newInstance());
            fragments.add(AlbumDetailInfoFragment.newInstance());
            fragmentsPagerAdapter = new AlbumPageFragmentsAdapter(getActivity().getSupportFragmentManager(), fragments);
            viewPager.setAdapter(fragmentsPagerAdapter);
            fragmentsTab.setViewPager(viewPager);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_album;
    }

    private static class AlbumPageFragmentsAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments;
        int trackCount;

        public AlbumPageFragmentsAdapter(FragmentManager fm,
                                         List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "节目";
            } else {
                return "详情";
            }
        }
    }
}