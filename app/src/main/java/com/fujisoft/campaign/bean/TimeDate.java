package com.fujisoft.campaign.bean;

import com.fujisoft.campaign.wheelview.DateUtils;

import java.io.Serializable;

public class TimeDate implements Serializable {


    private static final long serialVersionUID = 1L;
    public int year;
    public int month;
    public int day;
    public int week;

    public TimeDate(int year, int month, int day) {
        if (month > 12) {
            month = 1;
            year++;
        } else if (month < 1) {
            month = 12;
            year--;
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public TimeDate() {
        this.year = DateUtils.getYear();
        this.month = DateUtils.getMonth();
        this.day = DateUtils.getCurrentMonthDay();
    }

    public static TimeDate modifiDayForObject(TimeDate date, int day) {
        TimeDate modifiDate = new TimeDate(date.year, date.month, day);
        return modifiDate;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

}  