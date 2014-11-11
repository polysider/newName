package com.synergyapps.plugins.worklog;

import com.atlassian.jira.issue.Issue;

import java.util.Comparator;

public class IssueComparator implements Comparator<Issue>
{
    public static final IssueComparator INSTANCE = new IssueComparator();

    private IssueComparator()
    {
    }

    @Override
    public int compare(Issue issue1, Issue issue2)
    {
        return issue1.getProjectObject().getName().compareToIgnoreCase(issue2.getProjectObject().getName());
    }
}
