// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GetEventStateResponse.java

package com.aimir.service.system.impl.demandResponseMgmt.soap;

import com.aimir.service.system.impl.demandResponseMgmt.eventstate.ListOfEventStates;

public class GetEventStateResponse
{

    public GetEventStateResponse()
    {
    }

    public String getReturnValue()
    {
        return returnValue;
    }

    public void setReturnValue(String value)
    {
        returnValue = value;
    }

    public ListOfEventStates getEventStates()
    {
        return eventStates;
    }

    public void setEventStates(ListOfEventStates value)
    {
        eventStates = value;
    }

    protected String returnValue;
    protected ListOfEventStates eventStates;
}
