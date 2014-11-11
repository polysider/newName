package com.synergyapps.plugins.util;

public class TextUtil
{
    public static final TextUtil INSTANCE = new TextUtil();

    private TextUtil()
    {
    }

    public static String worklogPrettyPrint(Integer item)
    {
        if (item == null)
        {
            return "";
        }
        return determineWorklogTime(item);
    }

    static String determineWorklogTime(int totalSeconds)
    {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds - 3600 * hours) / 60;
        int seconds = totalSeconds - 3600 * hours - 60 * minutes;

        String result = "";
        if (hours != 0)
        {
            result += hours + "h";
        }
        if (minutes != 0)
        {
            result += minutes + "m";
        }
        if (seconds != 0)
        {
            result += seconds + "s";
        }

        return result;
    }
}
