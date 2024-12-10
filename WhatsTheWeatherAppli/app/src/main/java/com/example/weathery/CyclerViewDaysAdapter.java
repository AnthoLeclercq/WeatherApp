package com.example.weathery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CyclerViewDaysAdapter extends RecyclerView.Adapter<CyclerViewDaysAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ForecastDaysModel> forecastDaysModelArrayList;

    public CyclerViewDaysAdapter(Context context, ArrayList<ForecastDaysModel> forecastDaysModelArrayList){

        this.context = context;
        this.forecastDaysModelArrayList = forecastDaysModelArrayList;
    }

    @NonNull
    @Override
    public CyclerViewDaysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_horizo_weather_days_list,
                        parent,false);
        return new CyclerViewDaysAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CyclerViewDaysAdapter.ViewHolder holder, int position) {

        ForecastDaysModel model = forecastDaysModelArrayList.get(position);

        holder.forecastDayTemp.setText(model.getTemparatureDay());
        String url = "https://openweathermap.org/img/wn/"+model.getIconDay()+"@2x.png";
        Picasso.get().load(url).into(holder.iconWeatherForecastDay);
        holder.forecastDayDay.setText(model.getTimeDay());
        holder.forecastHoursDay.setText(model.getHoursDay());
    }

    @Override
    public int getItemCount() {
        return forecastDaysModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        //Views in RecyclerView
        private ImageView iconWeatherForecastDay;
        private TextView forecastDayTemp, forecastDayDay, forecastHoursDay;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconWeatherForecastDay = itemView.findViewById(R.id.iv_day_icon);
            forecastDayTemp = itemView.findViewById(R.id.forecast_day_temp);
            forecastDayDay = itemView.findViewById(R.id.forecast_day_day);
            forecastHoursDay = itemView.findViewById(R.id.forecast_day_hours);
        }
    }
}
