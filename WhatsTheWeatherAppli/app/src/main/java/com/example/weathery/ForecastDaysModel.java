package com.example.weathery;

public class ForecastDaysModel {

    private String temparatureDay;
    private String iconDay;
    private String timeDay;
    private String hoursDay;

    public ForecastDaysModel (String temparatureDay,String iconDay,  String timeDay, String hoursDay){
        this.temparatureDay = temparatureDay;
        this.iconDay = iconDay;
        this.timeDay = timeDay;
        this.hoursDay = hoursDay;
    }

    public String getTemparatureDay() {
        return temparatureDay;
    }

    public String getIconDay() {
        return iconDay;
    }

    public String getTimeDay() {
        return timeDay;
    }

    public String getHoursDay() {
        return hoursDay;
    }
}
