package kyle.nestedscrolldemo.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import kyle.nestedscrolldemo.R;
import kyle.nestedscrolldemo.widget.SimpleRVAdapter;


/**
 * * Created by hongjunmin on 16/10/25
 *
 * @author hongjunmin
 */

public class AlbumTracksFragment extends AbstractBaseFragment{

    private RecyclerView tracks;

    public static AlbumTracksFragment newInstance() {
        return new AlbumTracksFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_album_tracks;
    }

    @Override
    protected void findViews() {
        tracks = (RecyclerView) findViewById(R.id.tracks);
    }

    @Override
    protected void init() {
        tracks.setLayoutManager(new LinearLayoutManager(getContext()));
        tracks.setAdapter(new SimpleRVAdapter(new String[]{"节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC",
                "节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC","节目ABC"}));
    }

}
