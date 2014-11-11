package com.synergyapps.plugins.util;

public enum TimeInterval
{
    CURRENT_WEEK(1),
    LAST_WEEK(2),
    LAST_2_WEEKS(3),
    LAST_4_WEEKS(4);

    int value;

    TimeInterval(int value)
    {
        this.value = value;
    }

    public static TimeInterval createEntity(int value)
    {
        for (TimeInterval timeInterval : TimeInterval.values())
        {
            if (timeInterval.value == value)
            {
                return timeInterval;
            }
        }
        return defaultValue();
    }

    static TimeInterval defaultValue()
    {
        return CURRENT_WEEK;
    }

    public int getValue()
    {
        return value;
    }
}
