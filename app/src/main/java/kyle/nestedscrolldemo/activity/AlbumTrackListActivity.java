/*
 * MoreListActivity.java
 * @include classes:MoreListActivity;interfaces:MoreListActivity
 * @version 1.0.0
 * @data 2013-12-19
 */
package kyle.nestedscrolldemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import kyle.nestedscrolldemo.R;
import kyle.nestedscrolldemo.fragment.AlbumFragment;
import kyle.nestedscrolldemo.tool.Tool;

/**
 * @author hongjunmin
 * @version 1.0.0
 * @name AlbumTrackListActivity
 * @desc 专辑内页
 */
public class AlbumTrackListActivity extends AppCompatActivity {

    public static final String TAG_DOWNLOAD_TRACKS_FRAGMENT = "downloadTracksFragment";
    private AlbumFragment mAlbumFragment;

    private boolean isLocal;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_album_detail);
        if (Tool.isStatusBarTranslucentEnable()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        showAlbumDetailFragment();
    }



    private void showAlbumDetailFragment() {
        if (mAlbumFragment == null) {
            mAlbumFragment = AlbumFragment.newInstance();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.fragment_container, mAlbumFragment);//首个Fragment不执行addToBackStack,防止点击返回键显示空白页的bug
        fragmentTransaction.commitAllowingStateLoss();
    }


    public static void launch(Context c) {
            Intent intent = new Intent(c, AlbumTrackListActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            c.startActivity(intent);
    }

}
