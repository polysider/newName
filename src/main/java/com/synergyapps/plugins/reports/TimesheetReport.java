package com.synergyapps.plugins.reports;

import java.util.Map;

public class TimesheetReport extends AbstractCustomReport
{
    @Override
    protected Map<String, Object> prepareVelocityParams(Map map)
    {
        return VelocityParamsReportHelper.prepareVelocityParams(map, true);
    }
}
