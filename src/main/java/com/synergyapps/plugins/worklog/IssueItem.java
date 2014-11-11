package com.synergyapps.plugins.worklog;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IssueItem
{
    private Issue issue;
    private Set<String> users = new HashSet<String>();
    private Map<String, List<Integer>> worklog = new HashMap<String, List<Integer>>();
    private Map<String, List<List<Long>>> worklogIds = new HashMap<String, List<List<Long>>>();
    private int daysCount;
    private boolean isWorklogEmpty = true;

    public IssueItem(Issue issue, Integer daysCount)
    {
        this.issue = issue;
        this.daysCount = daysCount;
    }

    public void addWorklog(WorklogItem worklogItem)
    {
        addWorklog(worklogItem.getId(), worklogItem.getUserName(), worklogItem.getDayIndex(), worklogItem.getSecondsReported());
    }

    public void addWorklog(Long id, String userName, int dayIndex, Integer secondsReported)
    {
        users.add(userName);

        List<Integer> worklogItem = worklog.get(userName);
        if (worklogItem == null)
        {
            Integer[] times = new Integer[daysCount];
            Arrays.fill(times, 0);
            worklogItem = Arrays.asList(times);
            worklog.put(userName, worklogItem);
        }
        worklogItem.set(dayIndex, worklogItem.get(dayIndex) + secondsReported);
        if (secondsReported > 0)
        {
            isWorklogEmpty = false;
        }

        List<List<Long>> worklogIdsItem = worklogIds.get(userName);
        if (worklogIdsItem == null)
        {
            worklogIdsItem = new ArrayList<List<Long>>();
            for (int i = 0; i < daysCount; i++)
            {
                worklogIdsItem.add(new ArrayList<Long>());
            }
            worklogIds.put(userName, worklogIdsItem);
        }
        worklogIdsItem.get(dayIndex).add(id);
    }

    /**
     * Issue is assigned to this user, but he was not worked on it (no worklog present)
     */
    public void addUser(String userName)
    {
        users.add(userName);

        List<Integer> worklogItem = worklog.get(userName);
        if (worklogItem == null)
        {
            Integer[] times = new Integer[daysCount];
            Arrays.fill(times, 0);
            worklogItem = Arrays.asList(times);
            worklog.put(userName, worklogItem);
        }
    }

    public List<List<Long>> worklogIdsForThisUser(String userName)
    {
        return worklogIds.get(userName);
    }

    public List<List<String>> worklogCommentsForThisUser(String userName)
    {
        List<List<Long>> worklogIdsForThisUser = worklogIdsForThisUser(userName);
        List<List<String>> result = new ArrayList<List<String>>();
        for (List<Long> worklogIdsForThisDay : worklogIdsForThisUser)
        {
            List<String> worklogCommentsForThisDay = new ArrayList<String>();
            for (Long worklogId : worklogIdsForThisDay)
            {
                String comment = ComponentAccessor.getWorklogManager().getById(worklogId).getComment();
                worklogCommentsForThisDay.add(comment);
            }
            result.add(worklogCommentsForThisDay);
        }

        return result;
    }

    public List<Integer> worklogByAllUsers()
    {
        Integer[] result = new Integer[daysCount];
        Arrays.fill(result, 0);
        for (String userName : worklog.keySet())
        {
            List<Integer> worklogOfOneUser = worklog.get(userName);
            for (int i = 0; i < daysCount; i++)
            {
                result[i] += worklogOfOneUser.get(i);
            }
        }

        return Arrays.asList(result);
    }

    public Map<String, Integer> worklogByThisUser()
    {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (String userName : worklog.keySet())
        {
            List<Integer> worklogOfOneUser = worklog.get(userName);
            Integer sum = 0;
            for (Integer time : worklogOfOneUser)
            {
                sum += time;
            }
            result.put(userName, sum);
        }

        return result;
    }

    public Integer worklogByThisUser(String userName)
    {
        Integer result = worklogByThisUser().get(userName);
        if (result == null)
        {
            return 0;
        }
        return result;
    }

    public Integer totalWorklog()
    {
        List<Integer> worklogByAllIssues = worklogByAllUsers();
        Integer result = 0;
        for (Integer time : worklogByAllIssues)
        {
            result += time;
        }
        return result;
    }

    public Issue getIssue()
    {
        return issue;
    }

    public Set<String> getUsers()
    {
        return users;
    }

    public Map<String, List<Integer>> getWorklog()
    {
        return worklog;
    }

    public int getDaysCount()
    {
        return daysCount;
    }

    public boolean isWorklogEmpty()
    {
        return isWorklogEmpty;
    }
}
