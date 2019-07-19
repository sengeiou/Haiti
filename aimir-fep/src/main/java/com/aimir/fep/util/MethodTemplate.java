package com.aimir.fep.util;

public class MethodTemplate
{
    private String methodName = null;
    private String returnType = null;
    private String parameter = null;
    private String description = null;

    public MethodTemplate()
    {
    }

    public String getMethodName()
    {
        return methodName;
    }
    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getReturnType()
    {
        return returnType;
    }
    public void setReturnType(String returnType)
    {
        this.returnType = returnType;
    }

    public String getParameter()
    {
        return parameter;
    }
    public void setParameter(String parameter)
    {
        this.parameter = parameter;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
}
