package com.synergyapps.plugins.rest.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB representation of a html page
 */
@XmlRootElement
public class HtmlPresentationHelper
{
    @XmlElement
    private String html;

    private HtmlPresentationHelper()
    {
        // for JAXB
    }

    public HtmlPresentationHelper(String html)
    {
        this.html = html;
    }
}
