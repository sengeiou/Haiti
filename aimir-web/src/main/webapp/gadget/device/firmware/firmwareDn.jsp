<%@page import="java.util.*,
                java.net.*,
                java.text.*,
                java.util.zip.*,
                java.io.*,
                com.aimir.bo.common.CommandProperty"
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
				//Convert byte to ubyte
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
	String firmwareDir = CommandProperty.getProperty("firmware.dir");
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
	}
	//out.println("filePath: "+filePath);

	File f = new File(filePath);
	if (f.exists() && f.canRead()) {
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=\"" + f.getName()
				+ "\"");
		response.setContentLength((int) f.length());
		BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(f));
		byte buffer[] = new byte[8 * 1024];
		out.clearBuffer();
		OutputStream out_s = new Writer2Stream(out);
		copyStreamsWithoutClose(fileInput, out_s, buffer);
		fileInput.close();
		out_s.flush();
	}
%>