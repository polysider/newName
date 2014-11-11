package com.synergyapps.plugins.rest.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorklogHelper
{
    @XmlElement
    private String id;

    @XmlElement
    private String comment;

    @XmlElement
    private String timeSpent;

    public WorklogHelper(String id, String comment, String timeSpent)
    {
        this.id = id;
        this.comment = comment;
        this.timeSpent = timeSpent;
    }
}
