package com.synergyapps.plugins.worklog;

import com.atlassian.jira.issue.Issue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserItem
{
    private String user;
    private Set<Issue> issues = new HashSet<Issue>();
    private Map<Issue, List<Integer>> worklog = new HashMap<Issue, List<Integer>>();
    private int daysCount;

    public UserItem(String user, Integer daysCount)
    {
        this.user = user;
        this.daysCount = daysCount;
    }

    public void addWorklog(WorklogItem worklogItem)
    {
        addWorklog(worklogItem.getIssue(), worklogItem.getDayIndex(), worklogItem.getSecondsReported());
    }

    public void addWorklog(Issue issue, int dayIndex, Integer secondsReported)
    {
        issues.add(issue);

        List<Integer> worklogItem = worklog.get(issue);
        if (worklogItem == null)
        {
            Integer[] times = new Integer[daysCount];
            Arrays.fill(times, 0);
            worklogItem = Arrays.asList(times);
            worklog.put(issue, worklogItem);
        }

        worklogItem.set(dayIndex, worklogItem.get(dayIndex) + secondsReported);
    }

    /**
     * Issue is assigned to this user, but he was not worked on it (no worklog present)
     */
    public void addIssue(Issue issue)
    {
        issues.add(issue);

        List<Integer> worklogItem = worklog.get(issue);
        if (worklogItem == null)
        {
            Integer[] times = new Integer[daysCount];
            Arrays.fill(times, 0);
            worklogItem = Arrays.asList(times);
            worklog.put(issue, worklogItem);
        }
    }

    public List<Integer> worklogByAllIssues()
    {
        Integer[] result = new Integer[daysCount];
        Arrays.fill(result, 0);
        for (Issue issue : worklog.keySet())
        {
            List<Integer> worklogOfOneUser = worklog.get(issue);
            for (int i = 0; i < daysCount; i++)
            {
                result[i] += worklogOfOneUser.get(i);
            }
        }

        return Arrays.asList(result);
    }

    public Integer totalWorklog()
    {
        List<Integer> worklogByAllIssues = worklogByAllIssues();
        Integer result = 0;
        for (Integer time : worklogByAllIssues)
        {
            result += time;
        }
        return result;
    }

    public String getUser()
    {
        return user;
    }

    public Set<Issue> getIssues()
    {
        return issues;
    }

    public Map<Issue, List<Integer>> getWorklog()
    {
        return worklog;
    }

    public Map<String, Integer> getOverallWorklogByProject()
    {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Issue issue : worklog.keySet())
        {
            List<Integer> list = worklog.get(issue);
            Integer sum = 0;
            for (Integer time : list)
            {
                sum += time;
            }

            String projectName = issue.getProjectObject().getName();
            Integer totalWorklog = result.get(projectName);
            if (totalWorklog == null)
            {
                totalWorklog = 0;
            }
            totalWorklog += sum;
            result.put(projectName, totalWorklog);
        }
        return result;
    }

    public int getDaysCount()
    {
        return daysCount;
    }
}
