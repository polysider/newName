package com.synergyapps.plugins.worklog;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.user.ApplicationUser;
import com.synergyapps.plugins.util.SortIgnoreCaseComparator;
import com.synergyapps.plugins.util.DateUtil;
import com.synergyapps.plugins.util.WorklogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.synergyapps.plugins.util.WorklogUtil.isAdmin;

public class WorklogContext
{
    private List<String> users;
    private Set<UserItem> userItems = new HashSet<UserItem>();
    private Map<String, UserItem> userItemByUserName = new HashMap<String, UserItem>();

    private List<Issue> issues = new ArrayList<Issue>();
    private Set<IssueItem> issueItems = new HashSet<IssueItem>();
    private Map<Issue, IssueItem> issueItemByIssue = new HashMap<Issue, IssueItem>();

    private List<WorklogItem> worklogItems = new ArrayList<WorklogItem>();
    private Date startDate;
    private Date endDate;
    private Integer daysCount;

    private Map<Long, Worklog> worklogMap = new HashMap<Long, Worklog>();

    public WorklogContext(SearchParametersAggregator searchParams, boolean allowIssuesWithoutWorklog)
    {
        ApplicationUser applicationUser = ComponentAccessor.getJiraAuthenticationContext().getUser();
        User user = applicationUser.getDirectoryUser();

        startDate = searchParams.getStartDate();
        endDate = searchParams.getEndDate();
        this.daysCount = searchParams.getDaysList().size();

        List<Issue> allIssues = WorklogUtil.searchIssues(searchParams);
        Collections.sort(allIssues, IssueComparator.INSTANCE);

        Set<String> usersSet = new HashSet<String>();
        for (Issue issue : allIssues)
        {
            IssueItem issueItem = new IssueItem(issue, daysCount);
            boolean isUserAdminForThisIssue = isAdmin(applicationUser, issue.getProjectObject());

            List<Worklog> worklogByIssue = ComponentAccessor.getWorklogManager().getByIssue(issue);
            for (Worklog worklogByIssueItem : worklogByIssue)
            {
                int index;
                try
                {
                    index = DateUtil.calculateDateIndexInList(searchParams.getStartDate(), searchParams.getEndDate(), worklogByIssueItem.getStartDate());
                }
                catch (IllegalArgumentException ex)
                {
                    continue;
                }

                int timeSpent = worklogByIssueItem.getTimeSpent().intValue();
                String worklogAuthorName = worklogByIssueItem.getAuthorObject().getName();
                if (timeSpent > 0 && (isUserAdminForThisIssue || user.getName().equals(worklogAuthorName)))
                {
                    issues.add(issue);
                    usersSet.add(worklogAuthorName);

                    WorklogItem worklogItem = new WorklogItem();
                    worklogItem.setId(worklogByIssueItem.getId());
                    worklogItem.setIssue(issue);
                    worklogItem.setUserName(worklogAuthorName);
                    worklogItem.setSecondsReported(timeSpent);
                    worklogItem.setDayIndex(index);
                    worklogItems.add(worklogItem);

                    issueItem.addWorklog(worklogItem);

                    UserItem userItem = userItemByUserName.get(worklogAuthorName);
                    if (userItem == null)
                    {
                        userItem = new UserItem(worklogAuthorName, daysCount);
                    }
                    userItem.addWorklog(worklogItem);
                    userItems.add(userItem);
                    userItemByUserName.put(worklogAuthorName, userItem);

                    worklogMap.put(worklogByIssueItem.getId(), worklogByIssueItem);
                }
            }

            // Add user with issue without worklog
            User assignee = issue.getAssignee();
            if (allowIssuesWithoutWorklog && assignee != null && (isUserAdminForThisIssue || user.getName().equals(assignee.getName())))
            {
                String assigneeName = assignee.getName();
                usersSet.add(assigneeName);
                issueItem.addUser(assigneeName);

                UserItem userItem = userItemByUserName.get(assigneeName);
                if (userItem == null)
                {
                    userItem = new UserItem(assigneeName, daysCount);
                }
                userItem.addIssue(issue);
                userItems.add(userItem);
                userItemByUserName.put(assigneeName, userItem);
            }

            if (allowIssuesWithoutWorklog || !issueItem.isWorklogEmpty())
            {
                issueItems.add(issueItem);
                issueItemByIssue.put(issue, issueItem);
            }
        }
        users = new ArrayList<String>(usersSet);
        Collections.sort(users, SortIgnoreCaseComparator.INSTANCE);

        // some cleanup for issues without any worklog
        if (!allowIssuesWithoutWorklog)
        {
            Set<Issue> currentIssuesSet = new HashSet<Issue>(issues);
            currentIssuesSet.retainAll(issueItemByIssue.keySet());
            issues = new ArrayList<Issue>(currentIssuesSet);
            Collections.sort(issues, IssueComparator.INSTANCE);
        }
    }

    public TreeMap<String, List<Issue>> buildMapFromUserToHisIssues()
    {
        TreeMap<String, List<Issue>> result = new TreeMap<String, List<Issue>>(SortIgnoreCaseComparator.INSTANCE);
        for (UserItem userItem : userItems)
        {
            List<Issue> userIssues = new ArrayList<Issue>(userItem.getIssues());
            Collections.sort(userIssues, IssueComparator.INSTANCE);
            result.put(userItem.getUser(), userIssues);
        }

        return result;
    }

    public Map<Issue, List<Integer>> buildMapFromIssueToDaysWorklog()
    {
        Map<Issue, List<Integer>> result = new HashMap<Issue, List<Integer>>();
        for (IssueItem issueItem : issueItems)
        {
            Integer[] array = new Integer[daysCount];
            Arrays.fill(array, 0);

            for (String user : issueItem.getUsers())
            {
                List<Integer> userWorklog = issueItem.getWorklog().get(user);
                for (int i = 0; i < daysCount; i++)
                {
                    array[i] += userWorklog.get(i);
                }
            }
            result.put(issueItem.getIssue(), Arrays.asList(array));
        }

        return result;
    }

    public List<String> getUsers()
    {
        return users;
    }

    public Set<UserItem> getUserItems()
    {
        return userItems;
    }

    public List<Issue> getIssues()
    {
        return issues;
    }

    public Set<IssueItem> getIssueItems()
    {
        return issueItems;
    }

    public List<WorklogItem> getWorklogItems()
    {
        return worklogItems;
    }

    public Integer getDaysCount()
    {
        return daysCount;
    }

    public Map<String, UserItem> getUserItemByUserName()
    {
        return userItemByUserName;
    }

    public Map<Issue, IssueItem> getIssueItemByIssue()
    {
        return issueItemByIssue;
    }

    public Map<Long, Worklog> getWorklogMap()
    {
        return worklogMap;
    }

    public String getWorklogMapPrettyPrint()
    {
        String result = "";
        for (Long id : worklogMap.keySet())
        {
            Worklog worklog = worklogMap.get(id);
            String timeSpent = worklog.getTimeSpent().toString();
            String worklogAuthorName = worklog.getAuthorObject().getName();
            String issueId = worklog.getIssue().getId().toString();
            Integer dayIndex = DateUtil.calculateDateIndexInList(startDate, endDate, worklog.getStartDate());

            result += ",['" + id + "','" + worklogAuthorName + "','" + issueId + "','" +
                    dayIndex + "'," + timeSpent + ",'" + worklog.getComment() + "']";
        }
        if (result.isEmpty())
        {
            return "[]";
        }
        return "[" + result.substring(1) + "]";
    }

    public Map<String, List<Issue>> getProjectsWithRelatedIssues()
    {
        Map<String, Set<Issue>> map = new HashMap<String, Set<Issue>>();
        for (Issue issue : issues)
        {
            String projectName = issue.getProjectObject().getName();
            Set<Issue> projectIssues = map.get(projectName);
            if (projectIssues == null)
            {
                projectIssues = new HashSet<Issue>();
            }
            projectIssues.add(issue);
            map.put(projectName, projectIssues);
        }

        Map<String, List<Issue>> result = new HashMap<String, List<Issue>>();
        for (String key : map.keySet())
        {
            List<Issue> issues = new ArrayList<Issue>(map.get(key));
            Collections.sort(issues, IssueComparator.INSTANCE);
            result.put(key, issues);
        }
        return result;
    }
}
