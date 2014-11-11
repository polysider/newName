package com.synergyapps.plugins.reports;

import com.synergyapps.plugins.util.UiDate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class DaysList extends ArrayList<UiDate>
{
    List<UiDate> daysList;

    public DaysList(List<UiDate> daysList)
    {
        super(daysList);
        Assert.isTrue(!daysList.isEmpty(), "Wrong dayList parameter!");
        this.daysList = daysList;
    }

    public String getDayListPrettyPrint()
    {
        String result = "";
        for (UiDate uiDate : daysList)
        {
            result += ",'" + uiDate.getDayForWorklog() + "'";
        }
        return "[" + result.substring(1) + "]";
    }

    public UiDate getFirstDay()
    {
        return daysList.get(0);
    }

    public UiDate getLastDay()
    {
        return daysList.get(daysList.size() - 1);
    }
}
