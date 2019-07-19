<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.File,
                 java.io.FileInputStream,
                 java.io.FileOutputStream,
                 java.util.List,
                 java.util.ArrayList,
                 java.util.Properties,
                 java.util.Enumeration,
                 jxl.Workbook,
                 jxl.WorkbookSettings,
                 jxl.Sheet,
                 jxl.Cell,
                 jxl.write.Label,
                 jxl.write.WritableSheet,
                 jxl.write.WritableWorkbook"%>
                 
<%
  String message_path = "../resources";
  String message_prefix = "message_";
  String message_excel = "aimir_ui_message.xls";
  String method = request.getParameter("method");
  
  if ("createExcel".equals(method)) {
    //WEB-INF/classes/res 에 있는 모든 message_xx.properties를 읽는다.
    File realPath = new File(request.getSession().getServletContext().getRealPath(message_path));
    File[] files = realPath.listFiles();
    
    List<File> list = new ArrayList<File>();
    for (int i=0; i < files.length; i++) {
        if (files[i].getName().indexOf(message_prefix) > -1) {
            list.add(files[i]);
        }
    }
    // 읽은 properties 갯수 만큼 헤더 인코딩 개수를 쓴다.
    WorkbookSettings ws = new WorkbookSettings();
    WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream(), ws);
    WritableSheet s1 = workbook.createSheet("data", 0);
    
    s1.setColumnView(0, 50);
    s1.addCell(new Label(0, 0, "property"));
    
    Properties[] prop = new Properties[list.size()];
    String[] encodes = new String[list.size()];
    File file = null;
    for (int i=0; i < list.size(); i++) {
        file = (File)list.get(i);
        encodes[i] = file.getName().substring(message_prefix.length(),
                                              file.getName().lastIndexOf("."));
        s1.setColumnView(i+1, 20);
        s1.addCell(new Label(i+1, 0, encodes[i]));
        prop[i] = new Properties();
        prop[i].load(new FileInputStream(file));
    }
    // 차례대로  프로퍼티에서 같은 것이 있는지 찾아서 엑셀에 저장하고 프로퍼티를 지운다.
    String property = null;
    String value = null;
    int row = 1;
    for (int i=0; i < prop.length; i++) {
      for (Enumeration<?> e = prop[i].propertyNames(); e.hasMoreElements(); row++) {
        property = (String)e.nextElement();
        s1.addCell(new Label(0, row, property));
        //out.print(property + " ");
        for (int j=i; j < prop.length; j++) {
          value = prop[j].getProperty(property);
          s1.addCell(new Label(j+1, row, value));
          //out.print("[" + j + "]=" + value + " ");
          prop[j].remove(property);
        }
        out.print("\n");
      }
    }
    
    String strClient = request.getHeader("user-agent"); 
    if(strClient.indexOf("MSIE 5.5") != -1 ) { 
      response.setHeader("Content-Type", "doesn/matter;"); 
      response.setHeader("Content-Disposition", "filename=\"" + message_excel + "\";"); 
    } else { 
      response.setHeader("Content-Type", "application/octet-stream;"); 
      response.setHeader("Content-Disposition", "attachment;filename=\"" + message_excel + "\";"); 
    } 
    response.setHeader("Buffer", "true");
    workbook.write();
    workbook.close();
  }
  else if ("writeProperties".equals(method)) {
      String realPath = request.getSession().getServletContext().getRealPath(message_path);
//	  String realPath = "S:/07_aimir_workspace/3.0/aimir-web/src/main/resources";
      File excelFile = new File(realPath + File.separator + message_excel);
      WorkbookSettings workbookSetting = new WorkbookSettings();
      workbookSetting.setEncoding("ISO8859_1");
      Workbook workbook = Workbook.getWorkbook(excelFile,workbookSetting); 
      Sheet sheet = workbook.getSheet(0); 
      int columns = sheet.getColumns();
      String[] encodes = new String[columns-1];
      Properties[] props = new Properties[encodes.length];
      
      // 테이블에서 인코딩을 가져온다.
      Cell cell = null;
      for (int i=0; i < encodes.length; i++) {
          cell = sheet.getCell(i+1, 0);
          encodes[i] = cell.getContents();
          props[i] = new Properties();
      }
      
      // 1번 행부터 읽어서 프로퍼티에 넣는다.
      String property = null;
      int rows = sheet.getRows();
      
      for (int i=1; i < rows; i++) {
          property = sheet.getCell(0, i).getContents();
          for (int j = 0; j < encodes.length; j++) {
              cell = sheet.getCell(j+1, i);
              if(props[j].get(property) != null) {
                  System.out.println("same property[" + property + "]");
              }
              props[j].put(property, cell.getContents());
          }
      }
      System.out.println("message properties rows[" + props[0].size() + "]");
      for (int i=0; i < encodes.length; i++) {
        props[i].store(new FileOutputStream(realPath + 
              File.separator + message_prefix + encodes[i] + ".properties"),
              "Message " + encodes[i] + " Properties");
      }
      
      out.println("메시지 프로퍼티 생성 완료~");
  }
  else if ("readProperties".equals(method)) {
      //Message message = new MessageStore(request);
      //out.println(message.toString("aimir.celluarphone"));
  }
  else {
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
  <head>
    <title>Message Properties 관리</title>
    <link href="${pageContext.request.contextPath}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
	<script type="text/javascript" charset="utf-8" src="${pageContext.request.contextPath}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${pageContext.request.contextPath}/js/extjs/ext-all.js"></script>
    <script language="javascript">
      function createExcel() {
        msgprop.method.value = "createExcel";
        msgprop.submit();
      }
      function writeProperties() {
        var url = 'messageProperties.jsp?';
        url += 'method=writeProperties';
        HttpConnection.open(url,function(msg,cb_fun) {
        	Ext.Msg.alert('<fmt:message key='aimir.message'/>', msg);
          }
        );
      }
      function readProperties() {
        msgprop.method.value = "readProperties";
        msgprop.submit();
      }
    </script>
    <script src='../scripts/HttpConnection.js' language='JavaScript1.2'></script>
  </head>
  <body>
    <p>웹 UI를 위한 메시지 프로퍼티 엑셀 생성과 엑셀에서 프로퍼티 생성하는 툴입니다.</p>
    <p>(* 생성된 메시지 파일은 aimir-schedule/src/main/resources/lang 폴더에도 복사해야 합니다.)</p>
    <form name="msgprop">
      <input type="hidden" name="method"/>
      <br> <%= message_path%> 에 있는 모든 message_xx.properteis를 읽어서 엑셀로 생성한다.
      <br><input type="button" value="엑셀생성" onclick="javascript:createExcel();"/>
      <br>프로퍼티 생성 전에 엑셀로 편집을 하고 <%= message_path%> 에 올린 후에 시도한다.
      <br><input type="button" value="프로퍼티생성" onclick="javascript:writeProperties();"/>
    </form>
  </body>
</html>
<%
  }
%>