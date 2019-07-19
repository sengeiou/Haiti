package com.aimir.schedule.util;

import java.util.ArrayList;
import java.util.List;

public class JobDefinition
{
    private String name;
    private String description;
    private String className;
    private List parameters;

    public JobDefinition()
    {
        parameters = new ArrayList();
    }

    public JobDefinition(String jobName, String desc, String cName)
    {
        parameters = new ArrayList();
        name = jobName;
        description = desc;
        className = cName;
    }

    public String getClassName()
    {
        return className;
    }

    public String getDescription()
    {
        return description;
    }

    public List getParameters()
    {
        return parameters;
    }

    public void setClassName(String string)
    {
        className = string;
    }

    public void setDescription(String string)
    {
        description = string;
    }

    public void addParameter(JobParameter param)
    {
        parameters.add(param);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String string)
    {
        name = string;
    }
}