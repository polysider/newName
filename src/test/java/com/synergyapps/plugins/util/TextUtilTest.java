package com.synergyapps.plugins.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TextUtilTest
{
    @Test
    public void worklogPrettyPrint()
    {
        assertThat("Wrong result for null param", TextUtil.worklogPrettyPrint(null), is(""));
        assertThat("Wrong result for 0 param", TextUtil.worklogPrettyPrint(0), is(""));
        assertThat("Wrong result", TextUtil.worklogPrettyPrint(46), is("46s"));
    }

    @Test
    public void determineWorklogTime()
    {
        assertThat("Wrong result for 0 param", TextUtil.determineWorklogTime(0), is(""));
        assertThat("Wrong result for 46 param", TextUtil.determineWorklogTime(46), is("46s"));
        assertThat("Wrong result for 63 param", TextUtil.determineWorklogTime(63), is("1m3s"));
        assertThat("Wrong result for 3674 param", TextUtil.determineWorklogTime(3674), is("1h1m14s"));
    }
}
