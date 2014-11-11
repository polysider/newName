package com.synergyapps.plugins.reports;

import com.synergyapps.plugins.util.UiDate;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DaysListTest
{
    DaysList daysList;

    @Before
    public void setup()
    {
        daysList = new DaysList(
                Arrays.asList(
                        prepareUiDate("Su", "12/03", "12-Mar"),
                        prepareUiDate("Mo", "13/03", "13-Mar"),
                        prepareUiDate("Th", "14/03", "14-Mar")
                ));
    }

    @Test
    public void getDayListPrettyPrint()
    {
        assertThat("Wrong pretty print value", daysList.getDayListPrettyPrint(), is("['12-Mar','13-Mar','14-Mar']"));
    }

    @Test
    public void getFirstDay()
    {
        assertThat("Wrong first day", daysList.getFirstDay(), is(daysList.get(0)));
    }

    @Test
    public void getLastDay()
    {
        assertThat("Wrong last day", daysList.getLastDay(), is(daysList.get(daysList.size() - 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void daysListCreationWithEmptyConstructorParameter()
    {
        new DaysList(new ArrayList<UiDate>());
    }

    @Test
    public void getDayListPrettyPrintForDaysListWithOneItem()
    {
        daysList = new DaysList(Arrays.asList(prepareUiDate("Su", "12/03", "12-Mar")));

        assertThat("Wrong pretty print value", daysList.getDayListPrettyPrint(), is("['12-Mar']"));
    }

    @Test
    public void getFirstDayForDaysListWithOneItem()
    {
        daysList = new DaysList(Arrays.asList(prepareUiDate("Su", "12/03", "12-Mar")));

        assertThat("Wrong first day", daysList.getFirstDay(), is(daysList.get(0)));
    }

    @Test
    public void getLastDayForDaysListWithOneItem()
    {
        daysList = new DaysList(Arrays.asList(prepareUiDate("Su", "12/03", "12-Mar")));

        assertThat("Wrong last day", daysList.getLastDay(), is(daysList.get(0)));
    }

    UiDate prepareUiDate(String dayOfWeekName, String dayAndMonth, String dayForWorklog)
    {
        UiDate uiDate = new UiDate();
        uiDate.setDayOfWeekName(dayOfWeekName);
        uiDate.setDayAndMonth(dayAndMonth);
        uiDate.setDayForWorklog(dayForWorklog);

        return uiDate;
    }
}
