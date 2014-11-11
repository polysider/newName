package com.synergyapps.plugins.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WorklogUtilTest
{
    @Test
    public void createCondition()
    {
        String jqlQuery = "issuetype = BUG";
        String projectId = "SDT sdf";

        assertThat("Wrong result for null jqlQuery and projectId", WorklogUtil.createCondition(null, null), is(""));
        assertThat("Wrong result for empty jqlQuery and projectId", WorklogUtil.createCondition("", ""), is(""));
        assertThat("Wrong result for null jqlQuery", WorklogUtil.createCondition(jqlQuery, null), is(jqlQuery));
        assertThat("Wrong result for null projectId", WorklogUtil.createCondition(null, projectId), is("project = \"SDT sdf\""));
        assertThat("Wrong result", WorklogUtil.createCondition(jqlQuery, projectId), is(jqlQuery + " AND project = \"SDT sdf\""));
    }
}
