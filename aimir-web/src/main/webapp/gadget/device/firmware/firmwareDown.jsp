<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.*,
                java.net.*,
                java.text.*,
                java.util.zip.*,
                java.io.*,
                com.aimir.bo.common.CommandProperty,
                org.springframework.util.FileCopyUtils"
%><%!
	void copyStreamsWithoutClose(InputStream in, OutputStream out, byte[] buffer)
			throws IOException {
		int b;
		while ((b = in.read(buffer)) != -1)
			out.write(buffer, 0, b);
	}
	class Writer2Stream extends OutputStream {
		Writer out;

		Writer2Stream(Writer w) {
			super();
			out = w;
		}

		public void write(int i) throws IOException {
			out.write(i);
		}

		public void write(byte[] b) throws IOException {
			for (int i = 0; i < b.length; i++) {
				int n = b[i];
				//Convert byte to ubyte..
				n = ((n >>> 4) & 0xF) * 16 + (n & 0xF);
				out.write(n);
			}
		}

		public void write(byte[] b, int off, int len) throws IOException {
			for (int i = off; i < off + len; i++) {
				int n = b[i];
				n = ((n >>> 4) & 0xF) * 16 + (n & 0xF);
				out.write(n);
			}
		}
	}
%><%
	String filename = request.getParameter("fileName");
	String fileType = request.getParameter("fileType");
	if(fileType == null || filename == null)
		return;
	String firmwareDir =  CommandProperty.getProperty("firmware.tooldir");
	String filePath = null;
	if(fileType.equals("diff")){
		filePath = firmwareDir + File.separator + filename.split("_FROM_")[0]
		         + File.separator + filename;
	}else if(fileType.equals("binary")){
		filePath = firmwareDir + File.separator + filename.replaceAll(".ebl.gz","").replaceAll(".ebl","").replaceAll(".bin.gz","").replaceAll(".bin","").replaceAll(".tar.gz","").replaceAll(".tar","")
		         + File.separator + filename;
		//out.println("<br>firmwareDir: "+firmwareDir);
		//out.println("<br>File.separator: "+File.separator);
		//out.println("<br>filename: "+filename);
		response.setHeader("Content-Transfer-Encoding", "binary");
	}
	//out.println("filePath: "+filePath);

	File f = new File(filePath);
	if (f.exists() && f.canRead()) {
		//response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=\"" + f.getName()
				+ "\"");
		response.setContentLength((int) f.length());
		
		OutputStream out1 = response.getOutputStream();
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(f);
            FileCopyUtils.copy(fis, out1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
		
	}
%>