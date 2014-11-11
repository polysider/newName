package com.synergyapps.plugins.worklog;

import com.atlassian.jira.issue.Issue;

public class WorklogItem
{
    private Long id;
    private Issue issue;
    private String userName;
    private Integer secondsReported;
    private Integer dayIndex;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Issue getIssue()
    {
        return issue;
    }

    public void setIssue(Issue issue)
    {
        this.issue = issue;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Integer getSecondsReported()
    {
        return secondsReported;
    }

    public void setSecondsReported(Integer secondsReported)
    {
        this.secondsReported = secondsReported;
    }

    public Integer getDayIndex()
    {
        return dayIndex;
    }

    public void setDayIndex(Integer dayIndex)
    {
        this.dayIndex = dayIndex;
    }

    @Override
    public String toString()
    {
        return "WorklogItem{" +
                "id=" + id +
                "issue=" + issue +
                ", userName='" + userName + '\'' +
                ", secondsReported=" + secondsReported +
                ", dayIndex=" + dayIndex +
                '}';
    }
}
