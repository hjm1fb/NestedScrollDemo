package kyle.nestedscrolldemo.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import kyle.nestedscrolldemo.R;

/**
 * * Created by hongjunmin on 16/10/26
 *
 * @author hongjunmin
 */

public class AlbumDetailInfoFragment extends AbstractBaseFragment {

    RecyclerView relatedAlbums;
    // head view
    LinearLayout tagLayout;
    TextView albumIntro;
    View toggleExpandIntro;
    int recommendMaxLines;

    public AlbumDetailInfoFragment() {

    }

    public static AlbumDetailInfoFragment newInstance() {
        AlbumDetailInfoFragment albumDetailFragment = new AlbumDetailInfoFragment();
        return albumDetailFragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_album_detail_info;
    }

    @Override
    protected void findViews() {
    }

    @Override
    protected void init() {
    }

}
