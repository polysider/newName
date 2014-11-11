package com.synergyapps.plugins.rest.service;

import com.synergyapps.plugins.reports.DaysList;
import com.synergyapps.plugins.util.DateUtil;
import com.synergyapps.plugins.util.DayOfWeek;
import com.synergyapps.plugins.util.TextUtil;
import com.synergyapps.plugins.util.TimeInterval;
import com.synergyapps.plugins.worklog.SearchParametersAggregator;
import com.synergyapps.plugins.worklog.WorklogContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.synergyapps.plugins.util.DateUtil.determineBoundsOfWeeksRange;

public class VelocityParamsGadgetHelper
{
    public static Map<String, Object> prepareVelocityParams(int timeInterval, int startDayOfTheWeek, String jqlQuery, String projectId, String summaryLinesOnlyFlag, String baseUrl)
    {
        Date[] boundsOfWeeksRange = determineBoundsOfWeeksRange(TimeInterval.createEntity(timeInterval), DayOfWeek.createEntity(startDayOfTheWeek));
        Date startDate = boundsOfWeeksRange[0];
        Date endDate = boundsOfWeeksRange[1];
        DaysList daysList = new DaysList(DateUtil.dateRangePrettyPrint(startDate, endDate));

        SearchParametersAggregator searchParams = new SearchParametersAggregator(startDate, endDate, jqlQuery, projectId);
        WorklogContext worklogContext = new WorklogContext(searchParams, true);

        Map<String, Object> velocityParams = new HashMap<String, Object>();
        velocityParams.put("daysList", daysList);
        velocityParams.put("worklogContext", worklogContext);
        velocityParams.put("textUtil", TextUtil.INSTANCE);
        velocityParams.put("forGadget", true);
        velocityParams.put("baseurl", baseUrl);
        velocityParams.put("summaryLinesOnly", "true".equals(summaryLinesOnlyFlag));

        return velocityParams;
    }
}
