/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmWarePageTreat.java
 * 작성일자/작성자 : 2010.12.09 최창희
 * @see 
 * 
 * 펌웨어 관리자 페이지 에서 사용하는 페이지 number를 생성
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.bo.device.firmware;

public class FirmWarePageTreat {
	private	int		rowCnt;
	private	int		rowCnt2;
	private	int		pageCnt;
	private	int		curPage;
	private	int		totalPCnt;
	private	int		totalBCnt;
	private	int		buttonPage;
	private	int		NextP;
	private	int		Next;
	private	int		PrevP;
	private	int		FirstP;
	private	int		Prev;
	private	int		temp;
	private	int		start; // between검색조건에서 시작 num
	private	int		end;
	private	int		startSeq;
	private	String	aPrev;
	private	String	aNext;
	private	String	tableTag;
	private	String	trTag;
	private	String	tdTag;
	private	String	boldFontTag;
	private	String	boldFontTailTag;
	private	String	strPrev;
	private	String	strNext;
	private	String	Pre_vague;
	private	String	Next_vague;
	private	String	Pre_bright;
	private	String	Next_bright;
	private	String	First_Pre;
	private	String	End_Next;
	private String  navigation;
	private String  treeGubun;
	
	/**
	 * @return Returns the navigation.
	 */
	public String getNavigation() {
		return this.printpage();
	}
	public FirmWarePageTreat(int i, String s, int j, int kk,String treeGubun)
	{
		this(i, !s.equals("") && s != null ? Integer.parseInt(s) : 1, j, kk,treeGubun);
	}
    
	//rowCnt , curPage, pageCnt, rowCnt2
	public FirmWarePageTreat(int i, int j, int k, int kk, String treeGubun)
	{
		buttonPage		= 10; //아래 숫자 생성 갯수.
		aPrev			= "";
		aNext			= "";
		tableTag		= " class=\"fw_page\" align=\"center\"";
		trTag			= "  ";
		tdTag			= "  ";
		boldFontTag		= "";
		boldFontTailTag	= "";
		strPrev			= "\u25C0";
		strNext			= "\u25B6";
		rowCnt			= i;
		rowCnt2			= kk;
		curPage			= j;
		pageCnt			= k;
		FirstP			= 1;
		this.treeGubun       = treeGubun;

		Pre_vague		= "/images/fundinfo/btn_prev.gif";
		Next_vague		= "/images/fundinfo/btn_next.gif";
		Pre_bright		= "/images/fundinfo/btn_prev.gif";
		Next_bright		= "/images/fundinfo/btn_next.gif";
		First_Pre		= "/images/fundinfo/btn_pprev.gif";
		End_Next		= "/images/fundinfo/btn_nnext.gif";

		if(i == 0)
			totalPCnt = 1;
		else
		if(i % k == 0)
			totalPCnt = i / k;
		else
			totalPCnt = i / k + 1;
		if(curPage > totalPCnt)
			curPage = curPage - 1;
		if(curPage % buttonPage == 0)
			temp = curPage / buttonPage;
		else
			temp = curPage / buttonPage + 1;
		if(totalPCnt % buttonPage == 0)
			totalBCnt = totalPCnt / buttonPage;
		else
			totalBCnt = totalPCnt / buttonPage + 1;
		Prev = temp - 1;
		Next = temp + 1;
		PrevP = curPage - 1;
		NextP = curPage + 1;
		start = (curPage - 1) * k;
		if(curPage == totalPCnt)
			end = i;
		else
			end = start + k;
		startSeq = i - start;
	}

	public String getTreeGubun() {
		return treeGubun;
	}
	public void setTreeGubun(String treeGubun) {
		this.treeGubun = treeGubun;
	}
	public void setPrevague(String s)
	{
		Pre_vague = s;
	}
	public String getPrevague()
	{
		if(Pre_vague == null || Pre_vague.equals(""))
			return "./images/button/bul_f2.gif";
		else
			return Pre_vague;
	}

	public void setNextvague(String s)
	{
		Next_vague = s;
	}
	public String getNextvague()
	{
		if(Next_vague == null || Next_vague.equals(""))
			return "./images/button/bul_n2.gif";
		else
			return Next_vague;
	}

	public void setPrebright(String s)
	{
		Pre_bright = s;
	}
	public String getPrebright()
	{
		if(Pre_bright == null || Pre_bright.equals(""))
			return "./images/button/bul_f1.gif";
		else
			return Pre_bright;
	}

	public void setNextbright(String s)
	{
		Next_bright = s;
	}
	public String getNextbright()
	{
		if(Next_bright == null || Next_bright.equals(""))
			return "./images/button/bul_n1.gif";
		else
			return Next_bright;
	}

	public void setFirstPre(String s)
	{
		First_Pre = s;
	}
	public String getFirstPre()
	{
		if(First_Pre == null || First_Pre.equals(""))
			return "./images/button/b_pre2.gif";
		else
			return First_Pre;
	}

	public void setEndNext(String s)
	{
		End_Next = s;
	}
	public String getEndNext()
	{
		if(End_Next == null || End_Next.equals(""))
			return "./images/button/b_next2.gif";
		else
			return End_Next;
	}

	public void setTableTag(String s)
	{
		tableTag = CommaChk(s);
	}

	public void setTrTag(String s)
	{
		trTag = CommaChk(s);
	}

	public void setTdTag(String s)
	{
		tdTag = CommaChk(s);
	}

	public void setBoldFontTag(String s)
	{
		boldFontTag = CommaChk(s);
		if(!boldFontTag.equals(""))
		{
			boldFontTag = "<font " + boldFontTag + " >";
			boldFontTailTag = "</font>";
		}
	}

	public void setStrPrev(String s)
	{
		strPrev = CommaChk(s);
	}

	public void setStrNext(String s)
	{
		strNext = CommaChk(s);
	}

	public String getPages()
	{
		return Integer.toString(curPage);
	}

	public int getIntPages()
	{
		return curPage;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	public int getTotalPCnt()
	{
		return totalPCnt;
	}

	public int getStartSeq()
	{
		return startSeq;
	}

	public int getRowCnt()
	{
		return rowCnt;
	}

	public int getRowCnt2()
	{
		return rowCnt2;
	}

	public String printpage()
	{
		StringBuffer stringbuffer = new StringBuffer(2048);
		
		if(totalPCnt == 1)//페이지가 1페이지 밖에 없을경우 default로 그냥 보여줌
			stringbuffer.append("     <table class=\"fw_page\" align=\"center\"><tr><td class=\"pv\"><a href=\"#\"><span class=\"prev2\"> </span></a></td>        <td class=\"ppv\"><a href=\"#\"><span class=\"prev\"> </span></a></td>           <td class=\"on\"><a href=\"#\">1</a></td>        <td class=\"nv\"><a href=\"#\"><span class=\"next\"> </span></a></td>        <td class=\"nnv\"><a href=\"#\"><span class=\"next2\"> </span></a></td>     </tr>     </table>");
		else if(totalPCnt > 1)
		{
			if(PrevP > 0){
				if(curPage > buttonPage ) { //왼쪽 이미지가 이전일 경우, 이이전 이미지는 보여지나 링크가 없음.
					stringbuffer.append("<table class=\"fw_page\" align=\"center\">\n<tr>\n<td class=\"pv\" style=\"cursor:hand;\"><A HREF=\"JavaScript:go_page('" + FirstP + "','"+treeGubun+"');\" " + aPrev + ">" + "<span class=\"prev2\"> </span>" + "</A>	</td> <td class=\"ppv\" style=\"cursor:hand;\" ><A HREF=\"JavaScript:go_page('" + PrevP + "','"+treeGubun+"');\" " + aPrev + ">" + "<span class=\"prev\"> </span></a>" + "</td>");
				} else {  //왼쪽 이미지 가 이이전, 이전 일경우 .
					stringbuffer.append("<table class=\"fw_page\" align=\"center\">\n<tr>\n<td class=\"pv\"><span class=\"prev2\"> </span> </td> <td class=\"ppv\" style=\"cursor:hand;\"><A HREF=\"JavaScript:go_page('" + PrevP + "','"+treeGubun+"');\" " + aPrev + ">" + "<span class=\"prev\"> </span></a>" + "</td>");					
				}
			}
			else //이전페이지가 없을경우 즉 이이전, 이전 링크가 필요 없을경우 이미지만 보여줌
				stringbuffer.append("<table class=\"fw_page\" align=\"center\">\n<tr>\n<td class=\"pv\"><span class=\"prev2\"> </span> </td> <td class=\"ppv\"><span class=\"prev\"> </span></td>");
			if(temp > 0)
			{
				int i = 1;
				for(int j = (temp - 1) * buttonPage + 1; i <= buttonPage && j <= totalPCnt; j++)
				{
					if(j != 0)
						if(j == curPage)
							stringbuffer.append("<td class=\"on\"> <a href=\"#\">"+j+"</a></td>");
						else
							stringbuffer.append("<td> <A HREF=JavaScript:go_page('" + j + "','"+treeGubun+"')>" + j + "</A></td>");
					i++;
				}

			}
			if(curPage < totalPCnt) {
				if( ((totalPCnt-curPage) > buttonPage) || (curPage <= buttonPage && totalPCnt > buttonPage) ) {
					   stringbuffer.append("<td class=\"nv\" style=\"cursor:hand;\" ><A HREF=\"JavaScript:go_page('" + NextP + "','"+treeGubun+"');\" " + aNext + "><span class=\"next\"> </span> </A></td><td class=\"nnv\" style=\"cursor:hand;\"><A HREF=\"JavaScript:go_page('" + totalPCnt + "','"+treeGubun+"');\" " + aNext + ">" + "<span class=\"next2\"> </span>" + "</A></td>\n</tr></table>");					   
				} else {
					   stringbuffer.append("<td class=\"nv\" style=\"cursor:hand;\" ><A HREF=\"JavaScript:go_page('" + NextP + "','"+treeGubun+"');\" " + aNext + "><span class=\"next\"> </span>" + "</A></td>\n<td class=\"nnv\" ><span class=\"next2\"> </span></td> </tr></table>");
				}
			}
			else
				stringbuffer.append("<td class=\"nv\" ><span class=\"next\"> </span></td>\n<td class=\"nnv\" ><span class=\"next2\"> </span></td> </tr></table>");
		}
		return stringbuffer.toString();
	}

	private String CommaChk(String s)
	{
		if(s == null || s.trim().equals(""))
			return "";
		else
			return s.replace('"', '\'');
	}
}
