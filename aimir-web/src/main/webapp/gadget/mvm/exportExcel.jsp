<%@ page contentType="application/vnd.ms-excel; charset=utf8" %>
<% 
String name = request.getParameter("filename");
response.setHeader("Content-Disposition", "attachment; filename=\""+name+".xls\"");
out.println("<html>");
out.println("<head>");
out.println("<TITLE>"+name+"</TITLE>");
out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />");
out.println("</head>");
out.println("<body>");
out.println(request.getParameter("dg"));
out.println("</body>");
out.println("</html>");
%>
