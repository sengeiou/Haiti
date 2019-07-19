<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.*,
                java.net.*,
                java.text.*,
                java.util.zip.*,
                java.io.*"
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
    String firmwareDir =  null; // CommandProperty.getProperty("firmware.tooldir");
    String filePath = null;
    response.reset();
    response.setContentType("application/octet-stream");
	   
    if(fileType.equals("diff")){
        filePath = filename;
    }
    else if(fileType.equals("binary")){
        filePath = filename;
        //out.println("<br>firmwareDir: "+firmwareDir);
        //out.println("<br>File.separator: "+File.separator);
        //out.println("<br>filename: "+filename);
        response.setHeader("Content-Transfer-Encoding", "binary");
    }
    //out.println("filePath: "+filePath);

    File f = new File(filePath);
    if (f.exists() && f.canRead()) {
	    response.setContentType("application/octet-stream");
	    response.setHeader("Content-Disposition", "attachment;filename=\"" + f.getName() + "\"");
	    // response.setContentLength((int) f.length());
        response.setHeader("Content-Length", ""+f.length());
		
        OutputStream out1 = response.getOutputStream();
        FileInputStream fis = null;
        byte[] b = new byte[2048];
        int leng = 0;
        try {
            fis = new FileInputStream(f);
            while ((leng = fis.read(b)) > 0) {
                out1.write(b, 0, leng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {}
            }
            if (out1 != null) {
                try {
                    out1.close();
                }
                catch (IOException e) {}
            }
        }
    }
%>