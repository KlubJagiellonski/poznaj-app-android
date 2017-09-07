package pl.poznajapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.poznajapp.R;
import pl.poznajapp.model.Feature;

/**
 * Created by Rafał Gawlik on 06.09.17.
 */

public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.ViewHolder> {

    private List<Feature> pointList;
    private final OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onDetailsClick(Feature feature, int position);
        void onMoveClick(Feature feature, int position);
    }

    public PointListAdapter(Context context, List<Feature> pointList, OnItemClickListener listener) {
        this.pointList = pointList;
        this.context = context;
        this.listener = listener;
    }

    public void setPointList(List<Feature> pointList){
        this.pointList = pointList;
    }

    @Override
    public PointListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_point_list, parent, false);

        return new PointListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PointListAdapter.ViewHolder holder, final int position) {
        if(pointList.get(position) != null)
            if(pointList.get(position).getProperties() != null)
                if(pointList.get(position).getProperties().getTitle() != null) {
                    holder.tv.setText(pointList.get(position).getProperties().getTitle());

                    holder.tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onMoveClick(pointList.get(position), position);
                        }
                    });

                    holder.iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onDetailsClick(pointList.get(position), position);
                        }
                    });
                }

    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.row_point_list_tv);
            iv = (ImageView) itemView.findViewById(R.id.row_point_details_iv);
        }
    }
}
