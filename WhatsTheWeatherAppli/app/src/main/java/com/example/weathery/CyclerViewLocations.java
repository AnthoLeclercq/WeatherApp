package com.example.weathery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CyclerViewLocations extends RecyclerView.Adapter<CyclerViewLocations.ViewHolder> {

    private Context context;
    private ArrayList<LocationsModel> locationsArrayList;

    public CyclerViewLocations(Context context, ArrayList<LocationsModel> locationsArrayList){

        this.context = context;
        this.locationsArrayList = locationsArrayList;
    }

    @NonNull
    @Override
    public CyclerViewLocations.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.locations_list,
                        parent,false);
        return new CyclerViewLocations.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CyclerViewLocations.ViewHolder holder, int position) {

        LocationsModel model = locationsArrayList.get(position);

        holder.locations.setText(model.getNameLocation());
    }

    @Override
    public int getItemCount() {
        return locationsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView locations;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locations = itemView.findViewById(R.id.nameLocation);

        }
    }
}
