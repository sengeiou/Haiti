<%@ page language="java" pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ page import="java.util.Enumeration" %>
<%
Object o = null;
for (Enumeration e = request.getAttributeNames(); e.hasMoreElements(); ) {
    o = e.nextElement();
    System.out.println(o + "=" + request.getAttribute((String)o));
}
out.println("<XML><STATUS>OK</STATUS><DETAIL></DETAIL></XML>");
%>
