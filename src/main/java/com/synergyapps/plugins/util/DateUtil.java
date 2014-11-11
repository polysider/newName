package com.synergyapps.plugins.util;

import org.apache.log4j.Logger;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtil
{
    private static final Logger LOGGER = Logger.getLogger(DateUtil.class);

    static String[] MONTH_NAMES = new String[12];

    static
    {
        String[] longMonthNames = new DateFormatSymbols().getMonths();
        for (int i = 0; i < 12; i++)
        {
            MONTH_NAMES[i] = longMonthNames[i].substring(0, 3);
        }
    }

    public static Date parseDate(int year, int month, int day, int hour, int minute, int second)
    {
        try
        {
            String dateString = String.format("%d-%d-%d %d:%d:%d", year, month, day, hour, minute, second);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Error during date parsing with params: " +
                    year + "," + month + "," + day + "," + hour + "," + minute + "," + second);
        }
    }

    public static Date parseDate(int year, int month, int day)
    {
        try
        {
            String dateString = String.format("%d-%d-%d", year, month, day);
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Error during date parsing with params: " + year + "," + month + "," + day);
        }
    }

    public static String parseDate(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;    //month numbers starting from 0
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static Date parseDateFromUi(String dateString)
    {
        try
        {
            return new SimpleDateFormat("dd/MMM/yy").parse(dateString);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Error during date parsing with param: " + dateString);
        }
    }

    public static Date parseDateForWorklog(String dateString)
    {
        try
        {
            return new SimpleDateFormat("dd-MMM-yyyy HH:mm").parse(dateString);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("Error during date parsing with param: " + dateString);
        }
    }

    /*
     *  Get the day of week from the Date based on specific locale.
     */
    public static String determineDayOfWeekName(Date date)
    {
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date).substring(0, 2);
    }

    public static Date removeTime(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int calculateDateIndexInList(Date startDate, Date endDate, Date date)
    {
        double start = DateUtil.removeTime(startDate).getTime();
        double end = DateUtil.removeTime(endDate).getTime();
        double current = DateUtil.removeTime(date).getTime();

        if (start <= current && current <= end)
        {
            return (int) ((current - start) / (24.0 * 3600 * 1000));
        }
        else
        {
            throw new IllegalArgumentException("Wrong incoming params during date index calculation");
        }
    }

    public static int determineDaysCountInRange(Date startDate, Date endDate)
    {
        if (startDate.getTime() > endDate.getTime())
        {
            throw new IllegalArgumentException("Start date must be before end date!");
        }

        long startTime = DateUtil.removeTime(startDate).getTime();
        long endTime = DateUtil.removeTime(endDate).getTime();

        return 1 + (int) ((endTime - startTime) / (24.0 * 3600 * 1000));
    }

    // Day/Month
    public static String datePrettyPrint(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);

        return dayOfMonth + "/" + MONTH_NAMES[month];
    }

    public static String datePrettyPrintForWorklog(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        return dayOfMonth + "-" + MONTH_NAMES[month] + "-" + year;
    }

    public static List<UiDate> dateRangePrettyPrint(Date startDate, Date endDate)
    {
        if (startDate.getTime() > endDate.getTime())
        {
            throw new IllegalArgumentException("Start date must be before end date!");
        }

        List<UiDate> result = new ArrayList<UiDate>();
        long startTime = DateUtil.removeTime(startDate).getTime();
        long endTime = DateUtil.removeTime(endDate).getTime();
        long currentTime = startTime;
        while (currentTime <= endTime)
        {
            Date date = new Date(currentTime);
            UiDate uiDate = new UiDate();
            uiDate.setDayAndMonth(datePrettyPrint(date));
            uiDate.setDayOfWeekName(determineDayOfWeekName(date));
            uiDate.setDayForWorklog(datePrettyPrintForWorklog(date));   //16/Mar/14 12:00 AM
            result.add(uiDate);

            currentTime += 24 * 3600 * 1000;
        }

        return result;
    }

    public static Date[] determineBoundsOfWeek(Date date, DayOfWeek startDayOfWeek)
    {
        return determineBoundsOfWeeksRange(date, 1, startDayOfWeek);
    }

    public static Date[] determineBoundsOfWeeksRange(Date date, int weeksCount, DayOfWeek startDayOfWeek)
    {
        if (weeksCount < 1)
        {
            weeksCount = 1;
            LOGGER.warn("Wrong weeks count - so set to 1, was: " + weeksCount);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(removeTime(date));

        // "calculate" the start date of the week
        Calendar first = (Calendar) cal.clone();
        first.add(Calendar.DAY_OF_WEEK, startDayOfWeek.getValue() - first.get(Calendar.DAY_OF_WEEK));

        // and add six days to the end date
        Calendar last = (Calendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);

        first.add(Calendar.DAY_OF_YEAR, -7 * (weeksCount - 1));

        return new Date[]{ first.getTime(), last.getTime() };
    }

    public static Date[] determineBoundsOfWeeksRange(TimeInterval timeInterval, DayOfWeek startDayOfWeek)
    {
        Date date = new Date();
        switch (timeInterval)
        {
            case CURRENT_WEEK:
                return determineBoundsOfWeek(date, startDayOfWeek);

            case LAST_WEEK:
                Date[] dates1 = determineBoundsOfWeeksRange(date, 2, startDayOfWeek);
                Date[] dates2 = determineBoundsOfWeek(date, startDayOfWeek);
                return new Date[]{ dates1[0], dates2[0] };

            case LAST_2_WEEKS:
                return determineBoundsOfWeeksRange(date, 2, startDayOfWeek);

            case LAST_4_WEEKS:
                return determineBoundsOfWeeksRange(date, 4, startDayOfWeek);

            default:
                return determineBoundsOfWeek(date, startDayOfWeek);
        }
    }
}
