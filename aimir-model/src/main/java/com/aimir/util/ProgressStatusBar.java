package com.aimir.util;

/**
 * @author javaservice.net
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProgressStatusBar {
	private final int BAR_MAX_SIZE=35;

	private java.io.PrintWriter out;
	private int boxMaxCount;
	private int currentBoxPosition;
	private String sliceUnit;

	private int userMaxValue;
	private int userInterval;
	private int currentUserValue;

	public void clear() {
		try {
			out.println("<script language=javascript>self.status='';</script>");
			out.flush();
		}
		catch (Exception e) {
		}

	}

	public void increment() {
		currentUserValue+=userInterval;
		setPercentage((double) currentUserValue/userMaxValue*100);
	}

	/**
	 * @param double p
	 */
	public void setPercentage(double p) {
		int position=(int) (Math.ceil(boxMaxCount*p/100.0));
		if (position>boxMaxCount) {
			position=boxMaxCount;
		}

		if (position!=currentBoxPosition||p==100) {
			String progressBar="";
			for (int i=0;i<position;i++) {
				progressBar+=sliceUnit;
			}

			progressBar+=" "+((int) p)+"%";
			try {
				out.println("<script language=javascript>self.status='"
						+progressBar+"';</script>");
				out.flush();
			}
			catch (Exception e) {
			}
			currentBoxPosition=position;
		}
	}

	/**
	 * @param int p
	 */
	public void setPercentage(int p) {
		setPercentage((double) p);
	}

	/**
	 * @param String s - default slice unit character is "■";
	 */
	public void setSliceUnit(String s) {
		sliceUnit=s;
		int length=s.getBytes().length;
		boxMaxCount=BAR_MAX_SIZE/length;
	}
}