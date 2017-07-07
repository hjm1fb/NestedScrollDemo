package kyle.nestedscrolldemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import kyle.nestedscrolldemo.R;

public class MainActivity extends AppCompatActivity {

    TextView nestedScrollWithViewPager;
    TextView nestedScrollWithRefreshView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        init();

    }

    private void findViews() {
        nestedScrollWithViewPager = (TextView) findViewById(R.id.nestedScrollWithViewPager);
        nestedScrollWithRefreshView = (TextView)findViewById(R.id.nestedScrollWithRefreshView);
    }

    private void init() {
        nestedScrollWithViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumTrackListActivity.launch(v.getContext());
            }
        });
        nestedScrollWithRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifyArticlesActivity.launch(v.getContext());
            }
        });
    }


}
