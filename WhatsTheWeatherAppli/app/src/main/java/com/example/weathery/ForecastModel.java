package com.example.weathery;

public class ForecastModel {

    private String temparatureDay;
    private String iconDay;
    private String timeDay;

    public ForecastModel (String temparatureDay,String iconDay,String timeDay){

        this.temparatureDay = temparatureDay;
        this.iconDay = iconDay;
        this.timeDay = timeDay;

    }

    public String getTemparatureDay() { return temparatureDay; }

    public String getIconDay() {
        return iconDay;
    }

    public String getTimeDay(){ return timeDay; }
}
