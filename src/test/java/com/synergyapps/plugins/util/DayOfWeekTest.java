package com.synergyapps.plugins.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DayOfWeekTest
{
    @Test
    public void createEntity()
    {
        assertThat("Wrong result for value == 1", DayOfWeek.createEntity(1), is(DayOfWeek.SUNDAY));
        assertThat("Wrong result for value == 2", DayOfWeek.createEntity(2), is(DayOfWeek.MONDAY));
        assertThat("Unexpected default value", DayOfWeek.createEntity(35), is(DayOfWeek.SUNDAY));
    }
}
