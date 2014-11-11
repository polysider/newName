package com.synergyapps.plugins.util;

public enum DayOfWeek
{
    SUNDAY(1),
    MONDAY(2);

    int value;

    DayOfWeek(int value)
    {
        this.value = value;
    }

    public static DayOfWeek createEntity(int value)
    {
        for (DayOfWeek dayOfWeek : DayOfWeek.values())
        {
            if (dayOfWeek.value == value)
            {
                return dayOfWeek;
            }
        }
        return defaultValue();
    }

    static DayOfWeek defaultValue()
    {
        return SUNDAY;
    }

    public int getValue()
    {
        return value;
    }
}
