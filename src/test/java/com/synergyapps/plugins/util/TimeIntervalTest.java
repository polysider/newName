package com.synergyapps.plugins.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimeIntervalTest
{
    @Test
    public void createEntity()
    {
        assertThat("Wrong result for value == 1", TimeInterval.createEntity(1), is(TimeInterval.CURRENT_WEEK));
        assertThat("Wrong result for value == 2", TimeInterval.createEntity(2), is(TimeInterval.LAST_WEEK));
        assertThat("Wrong result for value == 3", TimeInterval.createEntity(3), is(TimeInterval.LAST_2_WEEKS));
        assertThat("Wrong result for value == 4", TimeInterval.createEntity(4), is(TimeInterval.LAST_4_WEEKS));
        assertThat("Unexpected default value", TimeInterval.createEntity(35), is(TimeInterval.CURRENT_WEEK));
    }
}
