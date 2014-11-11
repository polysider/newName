package com.synergyapps.plugins.reports;

import com.synergyapps.plugins.util.DateUtil;
import com.synergyapps.plugins.worklog.SearchParametersAggregator;
import com.synergyapps.plugins.worklog.WorklogContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VelocityParamsReportHelper
{
    public static Map<String, Object> prepareVelocityParams(Map map, boolean forTimesheetReport)
    {
        SearchParametersAggregator searchParams = prepareSearchParameters(map, !forTimesheetReport);
        WorklogContext worklogContext = new WorklogContext(searchParams, false);

        Map<String, Object> velocityParams = new HashMap<String, Object>();
        velocityParams.put("daysList", searchParams.getDaysList());
        velocityParams.put("worklogContext", worklogContext);
        if (forTimesheetReport)
        {
            velocityParams.put("showWorklogComments", (String) map.get("showWorklogCommentsId"));
        }

        return velocityParams;
    }

    static SearchParametersAggregator prepareSearchParameters(Map map, boolean includeProjectIds)
    {
        Date startDate = DateUtil.parseDateFromUi((String) map.get("startDateId"));
        Date endDate = DateUtil.parseDateFromUi((String) map.get("endDateId"));
        String jqlQuery = (String) map.get("jqlQueryId");
        String projectId = (String) map.get("projectId");

        if (includeProjectIds)
        {
            return new SearchParametersAggregator(startDate, endDate, jqlQuery, projectId);
        }
        else
        {
            return new SearchParametersAggregator(startDate, endDate, jqlQuery);
        }
    }
}
