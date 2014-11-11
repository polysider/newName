package com.synergyapps.plugins.util;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class DateUtilTest
{
    static Date preparedDate;
    static List<Date> preparedDatesWithTime;

    @Before
    public void setUp() throws ParseException
    {
        preparedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2014-02-26");

        preparedDatesWithTime = Arrays.asList(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-01 13:02:23"), //0

                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-26 14:02:23"), //1
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-26 15:02:23"), //2

                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-27 02:02:23"), //3
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-27 15:02:23"), //4

                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-02-28 14:02:23")  //5
        );
    }

    @Test
    public void determineDayName()
    {
        String dayName = DateUtil.determineDayOfWeekName(preparedDate);

        assertThat("Wrong result", dayName, is("We"));
    }

    @Test
    public void parseDate()
    {
        Date result = DateUtil.parseDate(2014, 2, 26);

        assertThat("Wrong result", result.getTime(), is(preparedDate.getTime()));
    }

    @Test
    public void parseDateWithTime()
    {
        long expectedTime = preparedDate.getTime() + 1000 * (56 + 60 * (14 + 60 * 2));

        Date result = DateUtil.parseDate(2014, 2, 26, 2, 14, 56);

        assertThat("Wrong result", result.getTime(), is(expectedTime));
    }

    @Test
    public void removeTime()
    {
        long expectedTime = preparedDate.getTime();
        preparedDate.setTime(preparedDate.getTime() + 1000);

        Date result = DateUtil.removeTime(preparedDate);

        assertThat("", result.getTime(), is(expectedTime));
    }

    @Test
    public void calculateDateIndexInList() throws ParseException
    {
        assertThat("Wrong result when test time equals to left border", DateUtil.calculateDateIndexInList(
                preparedDatesWithTime.get(0),
                preparedDatesWithTime.get(5),
                preparedDatesWithTime.get(0)),
                is(0));

        assertThat("Wrong result when test time equals to right border", DateUtil.calculateDateIndexInList(
                preparedDatesWithTime.get(0),
                preparedDatesWithTime.get(5),
                preparedDatesWithTime.get(5)),
                is(27));

        assertThat("Wrong result for standart case", DateUtil.calculateDateIndexInList(
                preparedDatesWithTime.get(0),
                preparedDatesWithTime.get(5),
                preparedDatesWithTime.get(2)),
                is(25));

        assertThat("Wrong result when test time in same day as left border", DateUtil.calculateDateIndexInList(
                preparedDatesWithTime.get(1),
                preparedDatesWithTime.get(5),
                preparedDatesWithTime.get(2)),
                is(0));

        assertThat("Wrong result when test time in same day as right border", DateUtil.calculateDateIndexInList(
                preparedDatesWithTime.get(0),
                preparedDatesWithTime.get(4),
                preparedDatesWithTime.get(3)),
                is(26));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateDateIndexInListForTestTimeLessThanLeftBorder() throws ParseException
    {
        DateUtil.calculateDateIndexInList(preparedDatesWithTime.get(1), preparedDatesWithTime.get(4), preparedDatesWithTime.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateDateIndexInListForTestTimeMoreThanRightBorder() throws ParseException
    {
        DateUtil.calculateDateIndexInList(preparedDatesWithTime.get(1), preparedDatesWithTime.get(4), preparedDatesWithTime.get(5));
    }

    @Test
    public void determineDaysCountInRange()
    {
        assertThat("Wrong result", DateUtil.determineDaysCountInRange(
                preparedDatesWithTime.get(0),
                preparedDatesWithTime.get(5)),
                is(28));

        assertThat("Wrong result", DateUtil.determineDaysCountInRange(
                preparedDatesWithTime.get(0),
                preparedDatesWithTime.get(0)),
                is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void determineDaysCountInRangeWhenStartTimeMoreThanEndTime()
    {
        DateUtil.determineDaysCountInRange(preparedDatesWithTime.get(5), preparedDatesWithTime.get(0));
    }

    @Test
    public void datePrettyPrint()
    {
        String result = DateUtil.datePrettyPrint(preparedDate);

        assertThat("Wrong result", result, is("26/Feb"));
    }

    @Test
    public void dateRangePrettyPrint()
    {
        List<UiDate> result = DateUtil.dateRangePrettyPrint(preparedDatesWithTime.get(1), preparedDatesWithTime.get(5));

        assertThat("Wrong size of result list", result.size(), is(3));

        String[] dayOfWeekNames = {
                result.get(0).getDayOfWeekName(),
                result.get(1).getDayOfWeekName(),
                result.get(2).getDayOfWeekName()
        };
        assertThat("Wrong result", Arrays.asList(dayOfWeekNames), hasItems("We", "Th", "Fr"));

        String[] dayAndMonths = {
                result.get(0).getDayAndMonth(),
                result.get(1).getDayAndMonth(),
                result.get(2).getDayAndMonth()
        };
        assertThat("Wrong result", Arrays.asList(dayAndMonths), hasItems("26/Feb", "27/Feb", "28/Feb"));
    }

    @Test
    public void dateRangePrettyPrintForOneDay()
    {
        List<UiDate> result = DateUtil.dateRangePrettyPrint(preparedDatesWithTime.get(1), preparedDatesWithTime.get(1));

        assertThat("Wrong size of result list", result.size(), is(1));

        String[] dayOfWeekNames = {
                result.get(0).getDayOfWeekName()
        };
        assertThat("Wrong result", Arrays.asList(dayOfWeekNames), hasItems("We"));

        String[] dayAndMonths = {
                result.get(0).getDayAndMonth()
        };
        assertThat("Wrong result", Arrays.asList(dayAndMonths), hasItems("26/Feb"));
    }

    @Test
    public void determineBoundsOfWeek() throws ParseException
    {
        long expectedStartTime = DateUtil.parseDate(2014, 2, 23).getTime(); //Su,23/Feb
        long expectedEndTime = DateUtil.parseDate(2014, 3, 1).getTime();    //Sat,1/Mar

        Date[] result = DateUtil.determineBoundsOfWeek(preparedDate, DayOfWeek.SUNDAY);

        assertThat("Wrong size of result list", result.length, is(2));
        assertThat("Wrong start time", result[0].getTime(), is(expectedStartTime));
        assertThat("Wrong end time", result[1].getTime(), is(expectedEndTime));
    }

    @Test
    public void determineBoundsOfWeeksRange() throws ParseException
    {
        long expectedStartTime = DateUtil.parseDate(2014, 2, 9).getTime();  //Su,9/Feb
        long expectedEndTime = DateUtil.parseDate(2014, 3, 1).getTime();    //Sat,1/Mar
        Date[] result = DateUtil.determineBoundsOfWeeksRange(preparedDate, 3, DayOfWeek.SUNDAY);

        assertThat("Wrong size of result list", result.length, is(2));
        assertThat("Wrong start time", result[0].getTime(), is(expectedStartTime));
        assertThat("Wrong end time", result[1].getTime(), is(expectedEndTime));

        expectedStartTime = DateUtil.parseDate(2014, 2, 23).getTime();      //Su,9/Feb
        result = DateUtil.determineBoundsOfWeeksRange(preparedDate, 0, DayOfWeek.SUNDAY);

        assertThat("Wrong size of result list for weeksCount param = 0", result.length, is(2));
        assertThat("Wrong start time for weeksCount param = 0", result[0].getTime(), is(expectedStartTime));
        assertThat("Wrong end time for weeksCount param = 0", result[1].getTime(), is(expectedEndTime));

        result = DateUtil.determineBoundsOfWeeksRange(preparedDate, -2, DayOfWeek.SUNDAY);

        assertThat("Wrong size of result list for weeksCount param = -2", result.length, is(2));
        assertThat("Wrong start time for weeksCount param = -2", result[0].getTime(), is(expectedStartTime));
        assertThat("Wrong end time for weeksCount param = -2", result[1].getTime(), is(expectedEndTime));

        expectedStartTime = DateUtil.parseDate(2014, 2, 10).getTime();  //Mon,10/Feb
        expectedEndTime = DateUtil.parseDate(2014, 3, 2).getTime();     //Su,2/Mar
        result = DateUtil.determineBoundsOfWeeksRange(preparedDate, 3, DayOfWeek.MONDAY);

        assertThat("Wrong size of result list for week start at Monday", result.length, is(2));
        assertThat("Wrong start time for week start at Monday", result[0].getTime(), is(expectedStartTime));
        assertThat("Wrong end time for week start at Monday", result[1].getTime(), is(expectedEndTime));
    }

    @Test
    public void parseDateToString()
    {
        String result = DateUtil.parseDate(preparedDatesWithTime.get(5));

        assertThat("Wrong result", result, is("2014-02-28"));
    }

    @Test
    public void parseDateFromUi()
    {
        Date date = DateUtil.parseDateFromUi("26/Feb/14");

        assertThat("Wrong result", date.getTime(), is(preparedDate.getTime()));
    }
}
