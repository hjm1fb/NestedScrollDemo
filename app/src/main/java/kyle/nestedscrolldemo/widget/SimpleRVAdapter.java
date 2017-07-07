package kyle.nestedscrolldemo.widget;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kyle.nestedscrolldemo.R;

/**
 * SimpleRVAdapter to quickly get started with simple Lists in Recyclerview
 *
 * Usage:
 *
 * RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
 * rv.setLayoutManager(new LinearLayoutManager(getContext()));
 * rv.setAdapter(new SimpleRVAdapter(new String[] {"1", "2", "3", "4", "5", "6", "7"}));
 *
 * @author Sheharyar Naseer
 */
public class SimpleRVAdapter extends RecyclerView.Adapter<SimpleRVAdapter.SimpleViewHolder> {
    private String[] dataSource;
    public SimpleRVAdapter(String[] dataArgs){
        dataSource = dataArgs;
    }
    
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = new TextView(parent.getContext());
        SimpleViewHolder viewHolder = new SimpleViewHolder(view);
        return viewHolder;
    }
    
    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.textView.setText(dataSource[position]);
        holder.textView.setPadding(30,30,30,30);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,1);
        holder.textView.setLayoutParams(layoutParams);
        holder.textView.setGravity(Gravity.CENTER);
        holder.textView.setBackgroundResource(R.color.green_A0);
    }

    @Override
    public int getItemCount() {
        return dataSource.length;
    }

    public String[] getDataSource() {
        return dataSource;
    }

    public void setDataSource(String[] dataSource) {
        this.dataSource = dataSource;
    }
}