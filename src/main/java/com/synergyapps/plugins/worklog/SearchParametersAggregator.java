package com.synergyapps.plugins.worklog;

import com.synergyapps.plugins.reports.DaysList;
import com.synergyapps.plugins.util.DateUtil;

import java.util.Date;

public class SearchParametersAggregator
{
    private Date startDate;
    private Date endDate;
    private String jqlQuery;
    private String projectId;
    private DaysList daysList;

    public SearchParametersAggregator(Date startDate, Date endDate, String jqlQuery)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysList = new DaysList(DateUtil.dateRangePrettyPrint(startDate, endDate));
        this.jqlQuery = jqlQuery;
    }

    public SearchParametersAggregator(Date startDate, Date endDate, String jqlQuery, String projectId)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysList = new DaysList(DateUtil.dateRangePrettyPrint(startDate, endDate));
        this.jqlQuery = jqlQuery;
        this.projectId = projectId;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public String getJqlQuery()
    {
        return jqlQuery;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public DaysList getDaysList()
    {
        return daysList;
    }
}
