package com.synergyapps.plugins.util;

import java.util.Comparator;

public class SortIgnoreCaseComparator implements Comparator<String>
{
    public static final SortIgnoreCaseComparator INSTANCE = new SortIgnoreCaseComparator();

    private SortIgnoreCaseComparator()
    {
    }

    @Override
    public int compare(String o1, String o2)
    {
        return o1.compareToIgnoreCase(o2);
    }
}
