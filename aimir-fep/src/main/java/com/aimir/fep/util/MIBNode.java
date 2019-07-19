package com.aimir.fep.util;

import com.aimir.fep.protocol.fmp.datatype.OID;

/**
 * Command MIB Node
 * 
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class MIBNode
{
    protected String name = null;
    protected OID oid = null;
    protected int nodeType = 0;

    /**
     *  constructor
     */
    public MIBNode()
    {
    }
    
    /**
     *  constructor
     *
     * @param name <code>String</code> name
     * @param oid <code>OID</code> name
     */
    public MIBNode(String name, OID oid)
    {
        this.name = name;
        this.oid = oid;
    }

    /**
     *  get node type
     *
     * @return nodeType <code>int</code>
     */
    public int getNodeType()
    {
        return nodeType;
    }

    /**
     *  set node type
     *
     * @param nodeType <code>int</code> node type
     */
    public void setNodeType(int nodeType)
    {
        this.nodeType = nodeType;
    }

    /**
     *  get name
     *
     * @return name <code>String</code>
     */
    public String getName()
    {
        return name;
    }
    /**
     *  set name
     *
     * @param name <code>String</code> name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     *  get oid
     *
     * @return oid <code>OID</code>
     */
    public OID getOid()
    {
        return oid;
    }

    /**
     *  set oid
     *
     * @param oid <code>OID</code> oid
     */
    public void setOid(OID oid)
    {
        this.oid = oid;
    }

    /**
     * get string
     *
     * @return string <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append("name="+name);
        sb.append(',');
        sb.append("oid="+oid);
        sb.append(']');

        return sb.toString();
    }
}
