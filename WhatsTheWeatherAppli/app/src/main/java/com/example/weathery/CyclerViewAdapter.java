package com.example.weathery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CyclerViewAdapter extends RecyclerView.Adapter<CyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ForecastModel> forecastModelArrayList;

    public CyclerViewAdapter(Context context,ArrayList<ForecastModel> forecastModelArrayList){

        this.context = context;
        this.forecastModelArrayList = forecastModelArrayList;
    }

    @NonNull
    @Override
    public CyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_horizo_weather_list,
                        parent,false);
        return new CyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CyclerViewAdapter.ViewHolder holder, int position) {

        ForecastModel model = forecastModelArrayList.get(position);

        holder.forecastTemp.setText(model.getTemparatureDay());
        String url = "https://openweathermap.org/img/wn/"+model.getIconDay()+"@2x.png";
        Picasso.get().load(url).into(holder.iconWeatherForecast);
        holder.forecastTime.setText(model.getTimeDay());
    }

    @Override
    public int getItemCount() {
        return forecastModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        //Views in RecyclerView
        private ImageView iconWeatherForecast;
        private TextView forecastTemp,forecastTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconWeatherForecast = itemView.findViewById(R.id.iv_icon);
            forecastTemp = itemView.findViewById(R.id.forecast_temp);
            forecastTime = itemView.findViewById(R.id.forecast_time);
        }
    }
}
