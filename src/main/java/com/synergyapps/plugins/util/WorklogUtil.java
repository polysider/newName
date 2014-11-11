package com.synergyapps.plugins.util;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.worklog.WorklogInputParametersImpl;
import com.atlassian.jira.bc.issue.worklog.WorklogResult;
import com.atlassian.jira.bc.issue.worklog.WorklogService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.jql.parser.DefaultJqlQueryParser;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.synergyapps.plugins.worklog.SearchParametersAggregator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorklogUtil
{
    private static final Logger LOGGER = Logger.getLogger(WorklogUtil.class);
    private static final DefaultJqlQueryParser jqlQueryParser = new DefaultJqlQueryParser();

    public static List<Issue> searchIssues(SearchParametersAggregator searchParams)
    {
        String jqlQuery = searchParams.getJqlQuery();
        String projectId = searchParams.getProjectId();
        String condition = createCondition(jqlQuery, projectId);

        JqlQueryBuilder jqlQueryBuilder = prepareJqlQueryBuilder(condition);

        return searchIssues(jqlQueryBuilder);
    }

    static List<Issue> searchIssues(JqlQueryBuilder jqlQueryBuilder)
    {
        Query query = jqlQueryBuilder.buildQuery();
        SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
        try
        {
            ApplicationUser applicationUser = ComponentAccessor.getJiraAuthenticationContext().getUser();
            User user = applicationUser.getDirectoryUser();
            SearchResults searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
            List<Issue> issues = searchResults.getIssues();

            List<Issue> result = new ArrayList<Issue>();
            for (Issue issue : issues)
            {
                result.add(issue);
            }
            return result;
        }
        catch (SearchException e)
        {
            LOGGER.error("Error occurs during search of issues");
            e.printStackTrace();
        }

        return new ArrayList<Issue>();
    }

    static JqlQueryBuilder prepareJqlQueryBuilder(String condition)
    {
        try
        {
            Query query = jqlQueryParser.parseQuery(condition);
            JqlQueryBuilder builder = JqlQueryBuilder.newBuilder(query);

            return builder;
        }
        catch (JqlParseException e)
        {
            throw new RuntimeException("JqlParseException during parsing jqlQuery!");
        }
    }

    static String createCondition(String jqlQuery, String projectId)
    {
        String condition = "";
        if (StringUtils.isNotBlank(jqlQuery))
        {
            condition += jqlQuery;
        }
        if (StringUtils.isNotBlank(projectId))
        {
            if (StringUtils.isNotBlank(condition))
            {
                condition += " AND ";
            }
            condition += "project = \"" + projectId + "\"";
        }
        LOGGER.info("Search by condition: " + condition);

        return condition;
    }

    public static boolean isJqlQueryValid(String jqlQuery)
    {
        if (StringUtils.isNotBlank(jqlQuery))
        {
            try
            {
                jqlQueryParser.parseQuery(jqlQuery);
            }
            catch (JqlParseException ex)
            {
                LOGGER.error("Invalid jqlQuery: " + jqlQuery);
                return false;
            }
        }
        return true;
    }

    public static Worklog createWorklog(String userName, Long issueId, String timeSpent, Date startDate, String comment)
    {
        Issue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(userName);
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
        WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl
                .issue(issue)
                .timeSpent(timeSpent)
                .startDate(startDate)
                .comment(comment).groupLevel(null).roleLevelId(null);
        WorklogService worklogService = ComponentAccessor.getComponent(WorklogService.class);
        WorklogResult result = worklogService.validateCreate(jiraServiceContext, builder.build());
        return worklogService.createAndAutoAdjustRemainingEstimate(jiraServiceContext, result, false);
    }

    public static boolean deleteWorklog(Long worklogId)
    {
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser();
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
        WorklogService worklogService = ComponentAccessor.getComponent(WorklogService.class);
        WorklogResult worklogResult = worklogService.validateDelete(jiraServiceContext, worklogId);
        return worklogService.deleteAndAutoAdjustRemainingEstimate(jiraServiceContext, worklogResult, false);
    }

    public static Worklog updateWorklog(Long worklogId, String comment, String timeSpent)
    {
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser();
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
        WorklogService worklogService = ComponentAccessor.getComponent(WorklogService.class);
        Worklog worklog = worklogService.getById(jiraServiceContext, worklogId);

        WorklogInputParametersImpl.Builder builder = WorklogInputParametersImpl
                .timeSpent(timeSpent)
                .worklogId(worklogId)
                .startDate(worklog.getStartDate())
                .comment(comment);

        WorklogResult result = worklogService.validateUpdate(jiraServiceContext, builder.build());
        return worklogService.updateAndAutoAdjustRemainingEstimate(jiraServiceContext, result, false);
    }

    public static List<Worklog> getWorklogsByIssue(Issue issue)
    {
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser();
        JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
        WorklogService worklogService = ComponentAccessor.getComponent(WorklogService.class);
        return worklogService.getByIssue(jiraServiceContext, issue);
    }

    public static boolean isAdmin(ApplicationUser user, Project project)
    {
        ProjectRoleManager projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager.class);
        ProjectRole administratorProjectRole = projectRoleManager.getProjectRole("Administrators");
        return projectRoleManager.isUserInProjectRole(user, administratorProjectRole, project);
    }
}
