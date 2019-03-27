package com.mdelacalle.komootsandbox.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mdelacalle.komootsandbox.R;
import com.mdelacalle.komootsandbox.activities.Map3DActivity;
import com.mdelacalle.komootsandbox.model.Tour;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class ToursAdapter extends RealmRecyclerViewAdapter<Tour,ToursAdapter.ViewHolder> {


    private Activity mParentActivity;

    public ToursAdapter(@Nullable OrderedRealmCollection<Tour> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public ToursAdapter(@Nullable OrderedRealmCollection<Tour> data, boolean autoUpdate, boolean updateOnModification) {
        super(data, autoUpdate, updateOnModification);
    }

    public void addParentActivity(Activity parentActivity){
        mParentActivity = parentActivity;
    }
    
    @NonNull
    @Override
    public ToursAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tour, parent, false);
        ViewHolder vh = new ViewHolder(layout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ToursAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(int position){
            Tour tour = getData().get(position);
            ((TextView)itemView.findViewById(R.id.tour_name)).setText(tour.getName());



            Glide.with(this.itemView)
                    .load(tour.getMap_image().getSrcFixed())
                    .into((ImageView) itemView.findViewById(R.id.tour_image));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mParentActivity.findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    Intent intent = new Intent( mParentActivity, Map3DActivity.class);
                    intent.putExtra("Tour",tour.getId());
                    mParentActivity.startActivity(intent);
                }
            });


        }
    }
}
