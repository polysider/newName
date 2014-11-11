package com.synergyapps.plugins.rest.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ValidationError
{
    /**
     * The field the error relates to
     */
    @XmlElement
    private String field;

    /**
     * The Error key...
     */
    @XmlElement
    private String error;

    public ValidationError(String field, String error)
    {
        this.field = field;
        this.error = error;
    }
}
