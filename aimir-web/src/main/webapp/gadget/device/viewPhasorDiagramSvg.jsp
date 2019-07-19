<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="image/svg+xml;charset=utf-8"%>
<%@ page import="java.util.*,
                 com.aimir.util.StringUtil" %>
<%!
    /**
     * Demand Reset
     * 
     * @param rad
     * @param cx
     * @param cy
     * @param degree 입력받은 각도값. 실제 계산에 사용할 때는 (360 - degree) 값을 사용한다. 
     * @return
     */
    private Map<String, Object> getVolPhasorPositionList(double rad, double cx, double cy, double degree) {
        Map<String, Object> volMap = new HashMap<String, Object>();

        double volArrDeg = 15d;        // 화살표 측면 각도
        double volArrSideLen = 20d;    // 화살표 측면 길이

        // 실제 각도값
        double realDeg = 360 - degree;
        double volRa = Math.toRadians(realDeg);   // radian 값으로 변경
        // 시작 x좌표 = 원 중심 x 좌표
        double volSx = cx;
        // 시작 y좌표 = 원 중심 y 좌표
        double volSy = cy;
        // 끝 x좌표
        double volEx = (Math.cos(volRa) * rad) + cx;
        // 끝 y좌표
        double volEy = cy - (Math.sin(volRa) * rad);

        // 화살표 각도1
        // 실제 데이터 입력시 적용
        //double ln1arr1deg = (360-deg1) + 180 - 20;
        double volArr1Deg = realDeg + 180 - volArrDeg;
        double volArr1Ra = Math.toRadians(volArr1Deg);

        // line1 시작 x좌표 = 원 중심 x 좌표
        double volArr1Sx = volEx;
        // line1 시작 y좌표 = 원 중심 y 좌표
        double volArr1Sy = volEy;
        // line1 끝 x좌표
        double volArr1Ex = volEx + (Math.cos(volArr1Ra) * volArrSideLen);
        // line1 끝 y좌표
        double volArr1Ey = volEy - (Math.sin(volArr1Ra) * volArrSideLen);
        
        // 화살표 각도2
        double volArr2Deg = realDeg + 180 + volArrDeg;
        double volArr2Ra = Math.toRadians(volArr2Deg);

        // line1 시작 x좌표 = 원 중심 x 좌표
        double volArr2Sx = volEx;
        // line1 시작 y좌표 = 원 중심 y 좌표
        double volArr2Sy = volEy;
        // line1 끝 x좌표
        double volArr2Ex = volEx + (Math.cos(volArr2Ra) * volArrSideLen);
        // line1 끝 y좌표
        double volArr2Ey = volEy - (Math.sin(volArr2Ra) * volArrSideLen);

/*
        // Text
        double volTxtDeg = realDeg + 20;
        double volTxtRa = Math.toRadians(volTxtDeg);

        // Text1 x좌표
        double volTxt1x = volEx + (Math.cos(volTxtRa) * 20);
        // Text1 y좌표
        double volTxt1y = volEy - (Math.sin(volTxtRa) * 20);

        // Text2 x좌표
        double volTxt2x = volTxt1x + 9;
        // Text1 y좌표
        double volTxt2y = volTxt1y;
*/
        
        // Text
        //double volTxtDeg = realDeg + 20;
        double volTxtDeg = 0d;
        
        if ((realDeg ==360 || (realDeg >= 0 && realDeg < 45)) || (realDeg >= 90 && realDeg < 135) || (realDeg >= 180 && realDeg < 225) || (realDeg >= 270 && realDeg < 315)) {
            volTxtDeg = realDeg + 20;
        } else {
            volTxtDeg = realDeg - 40;
        }

        double volTxtRa = Math.toRadians(volTxtDeg);

        // Text1 x좌표
        double volTxt1x = volEx + (Math.cos(volTxtRa) * 20);
        // Text1 y좌표
        double volTxt1y = volEy - (Math.sin(volTxtRa) * 20);

        // Text2 x좌표
        double volTxt2x = volTxt1x + 9;
        // Text1 y좌표
        double volTxt2y = volTxt1y;
        
        
        
        // 라인 좌표
        volMap.put("volSx", volSx);
        volMap.put("volSy", volSy);
        volMap.put("volEx", volEx);
        volMap.put("volEy", volEy);

        // 화살표 좌측면 좌표
        volMap.put("volArr1Sx", volArr1Sx);
        volMap.put("volArr1Sy", volArr1Sy);
        volMap.put("volArr1Ex", volArr1Ex);
        volMap.put("volArr1Ey", volArr1Ey);

        // 화살표 우측면 좌표
        volMap.put("volArr2Sx", volArr2Sx);
        volMap.put("volArr2Sy", volArr2Sy);
        volMap.put("volArr2Ex", volArr2Ex);
        volMap.put("volArr2Ey", volArr2Ey);

        // Text 좌표
        volMap.put("volTxt1x", volTxt1x);
        volMap.put("volTxt1y", volTxt1y);
        volMap.put("volTxt2x", volTxt2x);
        volMap.put("volTxt2y", volTxt2y);

        return volMap;
    }

    /**
     * Demand Reset
     * 
     * @param rad
     * @param cx
     * @param cy
     * @param degree 입력받은 각도값. 실제 계산에 사용할 때는 (360 - degree) 값을 사용한다. 
     * @throws FMPMcuException
     * @throws Exception
     */
    private Map<String, Object> getImpPhasorPositionList(double rad, double cx, double cy, double degree) {
        Map<String, Object> impMap = new HashMap<String, Object>();

        double impArrDeg = 15d;        // 화살표 측면 각도
        double impArrSideLen = 30d;    // 화살표 측면 길이

        double impRad = rad * 3 / 4;
        double realDeg = 360 - degree;
        double impRa = Math.toRadians(realDeg);
        // line1 시작 x좌표 = 원 중심 x 좌표
        double impSx = cx;
        // line1 시작 y좌표 = 원 중심 y 좌표
        double impSy = cy;
        // line1 끝 x좌표
        double impEx = (Math.cos(impRa) * impRad) + cx;
        // line1 끝 y좌표
        double impEy = cy - (Math.sin(impRa) * impRad);

        // line2 화살표 좌표1
        // 실제 데이터 입력시 적용
        //double ln2arr1deg = (360-deg1) + 180 - 20;
        double impArr1Deg = realDeg + 180 - impArrDeg;
        double impArr1Ra = Math.toRadians(impArr1Deg);
        // 화살표 옆면 길이
        //double ln2arrslen = 30;

        // line2 끝 x좌표
        double impArr1x = impEx + (Math.cos(impArr1Ra) * impArrSideLen);
        // line2 끝 y좌표
        double impArr1y = impEy - (Math.sin(impArr1Ra) * impArrSideLen);
        
        // line2 화살표 좌표2
        double impArr2Deg = realDeg + 180 + impArrDeg;
        double impArr2Ra = Math.toRadians(impArr2Deg);

        // line2 끝 x좌표
        double impArr2x = impEx + (Math.cos(impArr2Ra) * impArrSideLen);
        // line2 끝 y좌표
        double impArr2y = impEy - (Math.sin(impArr2Ra) * impArrSideLen);

        // Text
        //double impTxtDeg = realDeg + 90;
        double impTxtDeg = 0d;
        
        if ((realDeg == 360 || (realDeg >= 0 && realDeg < 45)) || (realDeg >= 90 && realDeg < 135) || (realDeg >= 180 && realDeg < 225) || (realDeg >= 270 && realDeg < 315)) {
            impTxtDeg = realDeg + 50;
        } else {
            impTxtDeg = realDeg - 70;
        }

        double impTxtRa = Math.toRadians(impTxtDeg);

        // Text1 x좌표
        double impTxt1x = impEx + (Math.cos(impTxtRa) * 15);
        // Text1 y좌표
        double impTxt1y = impEy - (Math.sin(impTxtRa) * 15);

        // Text2 x좌표
        double impTxt2x = impTxt1x + 7;
        // Text1 y좌표
        double impTxt2y = impTxt1y;
        
        // 라인 좌표
        impMap.put("impSx", impSx);
        impMap.put("impSy", impSy);
        impMap.put("impEx", impEx);
        impMap.put("impEy", impEy);

        // 화살표 각 꼭지점 좌표
        impMap.put("impArr0x", impEx);
        impMap.put("impArr0y", impEy);
        impMap.put("impArr1x", impArr1x);
        impMap.put("impArr1y", impArr1y);
        impMap.put("impArr2x", impArr2x);
        impMap.put("impArr2y", impArr2y);

        // Text 좌표
        impMap.put("impTxt1x", impTxt1x);
        impMap.put("impTxt1y", impTxt1y);
        impMap.put("impTxt2x", impTxt2x);
        impMap.put("impTxt2y", impTxt2y);

        // 위치파악을 위한 테스트용 연장선    
        // line1 시작 x좌표 = 원 중심 x 좌표
        //double ln2sxDm = cx;
        // line1 시작 y좌표 = 원 중심 y 좌표
        //double ln2syDm = cy;
        // line1 끝 x좌표
        //double ln2exDm = (Math.cos(impRa) * rad) + cx;
        // line1 끝 y좌표
        //double ln2eyDm = cy - (Math.sin(impRa) * rad);

        return impMap;
    }

%>
<%
    // 원 반지름
    double rad = 100d;
    // 원 x 좌표
    double cx = 175d;
    // 원 y 좌표
    double cy = 155d;

    double tick = 15d;
    // x축1 시작 x좌표
    double dx1sx = cx - rad - tick;
    // x축1 시작 y좌표 = 원 중심 y 좌표
    double dx1sy = cy;
    // x축1 끝 x좌표
    double dx1ex = cx - rad;
    // x축1 끝 y좌표 = 원 중심 y 좌표
    double dx1ey = cy;

    // x축2 시작 x좌표
    double dx2sx = cx + rad;
    // x축2 시작 y좌표 = 원 중심 y 좌표
    double dx2sy = cy;
    // x축2 끝 x좌표
    double dx2ex = cx + rad + tick;
    // x축2 끝 y좌표 = 원 중심 y 좌표
    double dx2ey = cy;

    // y축1 시작 x좌표 = 원 중심 x 좌표
    double dy1sx = cx;
    // y축1 시작 y좌표
    double dy1sy = cy - rad - tick;
    // y축1 끝 x좌표 = 원 중심 x 좌표
    double dy1ex = cx;
    // y축1 끝 y좌표
    double dy1ey = cy - rad;

    // y축2 시작 x좌표 = 원 중심 x 좌표
    double dy2sx = cx;
    // y축2 시작 y좌표
    double dy2sy = cy + rad;
    // y축2 끝 x좌표 = 원 중심 x 좌표
    double dy2ex = cx;
    // y축2 끝 y좌표
    double dy2ey = cy + rad + tick;

    
    // 눈금 font size
    double fnsize = 16;
    // 0º text
    double tx0x = dx2ex + 10;
    double tx0y = cy + 5;
    
    // 90º text
    double tx90x = cx;
    double tx90y = dy2ey + 15;
    
    // 180º text
    double tx180x = dx1sx - 20;
    double tx180y = cy + 5;
    
    // 270º text
    double tx270x = cx;
    double tx270y = dy1sy - 5;

    double lgSize = 20;         // 대문자 기호
    double smSize = 10;         // 소문자 기호

    
    Map<String, Object> vaMap = null;
    Map<String, Object> vbMap = null;
    Map<String, Object> vcMap = null;
    Map<String, Object> iaMap = null;
    Map<String, Object> ibMap = null;
    Map<String, Object> icMap = null;

    String volAng_a = request.getParameter("volAng_a");
    String volAng_b = request.getParameter("volAng_b");
    String volAng_c = request.getParameter("volAng_c");
    String curAng_a = request.getParameter("curAng_a");
    String curAng_b = request.getParameter("curAng_b");
    String curAng_c = request.getParameter("curAng_c");

    Double vaDegree = (!StringUtil.nullToBlank(volAng_a).isEmpty()) ? Double.valueOf(volAng_a) : 0d;
    Double vbDegree = (!StringUtil.nullToBlank(volAng_b).isEmpty()) ? Double.valueOf(volAng_b) : 0d;
    Double vcDegree = (!StringUtil.nullToBlank(volAng_c).isEmpty()) ? Double.valueOf(volAng_c) : 0d;
    Double iaDegree = (!StringUtil.nullToBlank(curAng_a).isEmpty()) ? Double.valueOf(curAng_a) : 0d;
    Double ibDegree = (!StringUtil.nullToBlank(curAng_b).isEmpty()) ? Double.valueOf(curAng_b) : 0d;
    Double icDegree = (!StringUtil.nullToBlank(curAng_c).isEmpty()) ? Double.valueOf(curAng_c) : 0d;

    if (vaDegree != null) {
        vaMap = getVolPhasorPositionList(rad, cx, cy, vaDegree.doubleValue());
    }

    if (vbDegree != null) {
        vbMap = getVolPhasorPositionList(rad, cx, cy, vbDegree.doubleValue());
    }

    if (vcDegree != null) {
        vcMap = getVolPhasorPositionList(rad, cx, cy, vcDegree.doubleValue());
    }

    if (iaDegree != null) {
        iaMap = getImpPhasorPositionList(rad, cx, cy, iaDegree.doubleValue());
    }

    if (ibDegree != null) {
        ibMap = getImpPhasorPositionList(rad, cx, cy, ibDegree.doubleValue());
    }

    if (icDegree != null) {
        icMap = getImpPhasorPositionList(rad, cx, cy, icDegree.doubleValue());
    }

%>
<svg width="350" height="300" xmlns="http://www.w3.org/2000/svg">
 <!-- Created with SVG-edit - http://svg-edit.googlecode.com/ -->
 <g>
  <title>Layer 1</title>
  <circle fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" cx="<%= cx %>" cy="<%= cy %>" r="<%= rad %>" id="svg_3"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= dx1sx %>" y1="<%= dx1sy %>" x2="<%= dx1ex %>" y2="<%= dx1ey %>" id="svg_4"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= dx2sx %>" y1="<%= dx2sy %>" x2="<%= dx2ex %>" y2="<%= dx2ey %>" id="svg_4"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= dy1sx %>" y1="<%= dy1sy %>" x2="<%= dy1ex %>" y2="<%= dy1ey %>" id="svg_5"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= dy2sx %>" y1="<%= dy2sy %>" x2="<%= dy2ex %>" y2="<%= dy2ey %>" id="svg_5"/>

  <text xml:space="preserve" text-anchor="middle" font-size="<%= fnsize %>" id="svg_12" x="<%= tx0x %>" y="<%= tx0y %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >0º</text>

  <text xml:space="preserve" text-anchor="middle" font-size="<%= fnsize %>" id="svg_12" x="<%= tx90x %>" y="<%= tx90y %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >90º</text>

  <text xml:space="preserve" text-anchor="middle" font-size="<%= fnsize %>" id="svg_12" x="<%= tx180x %>" y="<%= tx180y %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >180º</text>

  <text xml:space="preserve" text-anchor="middle" font-size="<%= fnsize %>" id="svg_12" x="<%= tx270x %>" y="<%= tx270y %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >270º</text>

<%--
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="304" y1="194" x2="355" y2="154" id="svg_6"/>
  <path stroke-width="2" fill="#000000" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" d="m379,135l-17,23l-10,-12l27,-11z" id="svg_7"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="303" y1="202" x2="420.12814" y2="134.37604" id="svg_8"/>
--%>

<%

    if (vaMap != null) {
%>
  <!-- Va -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vaMap.get("volSx") %>" y1="<%= vaMap.get("volSy") %>" x2="<%= vaMap.get("volEx") %>" y2="<%= vaMap.get("volEy") %>" id="svg_6"/>

  <!-- 화살표 -->  
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vaMap.get("volArr1Sx") %>" y1="<%= vaMap.get("volArr1Sy") %>" x2="<%= vaMap.get("volArr1Ex") %>" y2="<%= vaMap.get("volArr1Ey") %>" id="svg_7"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vaMap.get("volArr2Sx") %>" y1="<%= vaMap.get("volArr2Sy") %>" x2="<%= vaMap.get("volArr2Ex") %>" y2="<%= vaMap.get("volArr2Ey") %>" id="svg_8"/>

  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= lgSize %>" id="svg_12" x="<%= vaMap.get("volTxt1x") %>" y="<%= vaMap.get("volTxt1y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >V</text>
  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= smSize %>" id="svg_12" x="<%= vaMap.get("volTxt2x") %>" y="<%= vaMap.get("volTxt2y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >A</text>
<%        
    }
%>

<%

    if (vbMap != null) {
%>
  <!-- Vb -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vbMap.get("volSx") %>" y1="<%= vbMap.get("volSy") %>" x2="<%= vbMap.get("volEx") %>" y2="<%= vbMap.get("volEy") %>" id="svg_6"/>

  <!-- 화살표 -->  
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vbMap.get("volArr1Sx") %>" y1="<%= vbMap.get("volArr1Sy") %>" x2="<%= vbMap.get("volArr1Ex") %>" y2="<%= vbMap.get("volArr1Ey") %>" id="svg_7"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vbMap.get("volArr2Sx") %>" y1="<%= vbMap.get("volArr2Sy") %>" x2="<%= vbMap.get("volArr2Ex") %>" y2="<%= vbMap.get("volArr2Ey") %>" id="svg_8"/>

  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= lgSize %>" id="svg_12" x="<%= vbMap.get("volTxt1x") %>" y="<%= vbMap.get("volTxt1y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >V</text>
  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= smSize %>" id="svg_12" x="<%= vbMap.get("volTxt2x") %>" y="<%= vbMap.get("volTxt2y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >B</text>
<%        
    }
%>

<%
    if (vcMap != null) {
%>
  <!-- Vc -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vcMap.get("volSx") %>" y1="<%= vcMap.get("volSy") %>" x2="<%= vcMap.get("volEx") %>" y2="<%= vcMap.get("volEy") %>" id="svg_6"/>

  <!-- 화살표 -->  
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vcMap.get("volArr1Sx") %>" y1="<%= vcMap.get("volArr1Sy") %>" x2="<%= vcMap.get("volArr1Ex") %>" y2="<%= vcMap.get("volArr1Ey") %>" id="svg_7"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= vcMap.get("volArr2Sx") %>" y1="<%= vcMap.get("volArr2Sy") %>" x2="<%= vcMap.get("volArr2Ex") %>" y2="<%= vcMap.get("volArr2Ey") %>" id="svg_8"/>

  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= lgSize %>" id="svg_12" x="<%= vcMap.get("volTxt1x") %>" y="<%= vcMap.get("volTxt1y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >V</text>
  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= smSize %>" id="svg_12" x="<%= vcMap.get("volTxt2x") %>" y="<%= vcMap.get("volTxt2y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >C</text>
<%        
    }

%>

<%--
  <!-- line1 -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln1sx %>" y1="<%= ln1sy %>" x2="<%= ln1ex %>" y2="<%= ln1ey %>" id="svg_6"/>

  <!-- 화살표 -->  
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln1arr1sx %>" y1="<%= ln1arr1sy %>" x2="<%= ln1arr1ex %>" y2="<%= ln1arr1ey %>" id="svg_7"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln1arr2sx %>" y1="<%= ln1arr2sy %>" x2="<%= ln1arr2ex %>" y2="<%= ln1arr2ey %>" id="svg_8"/>

  <!-- 위치파악용 샘플 -->  
  <line fill="none" stroke="#FF0000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln2sxDm %>" y1="<%= ln2syDm %>" x2="<%= ln2exDm %>" y2="<%= ln2eyDm %>" id="svg_9"/>

  <!-- line2 -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln2sx %>" y1="<%= ln2sy %>" x2="<%= ln2ex %>" y2="<%= ln2ey %>" id="svg_10"/>

  <!-- 화살표 -->
  <!-- 
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln2arr1sx %>" y1="<%= ln2arr1sy %>" x2="<%= ln2arr1ex %>" y2="<%= ln2arr1ey %>" id="svg_11"/>
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ln2arr2sx %>" y1="<%= ln2arr2sy %>" x2="<%= ln2arr2ex %>" y2="<%= ln2arr2ey %>" id="svg_12"/>
  -->

  <polygon points="<%= ln2arr1sx %>,<%= ln2arr1sy %> <%= ln2arr1ex %>,<%= ln2arr1ey %> <%= ln2arr2ex %>,<%= ln2arr2ey %>" style="stroke: black; stroke-opacity: 1; stroke-width: 1; fill: #000000;"/>

--%>
  


<%

    if (iaMap != null) {
%>
  <!-- Ia -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= iaMap.get("impSx") %>" y1="<%= iaMap.get("impSy") %>" x2="<%= iaMap.get("impEx") %>" y2="<%= iaMap.get("impEy") %>" id="svg_10"/>

  <!-- 화살표 -->  
  <polygon points="<%= iaMap.get("impArr0x") %>,<%= iaMap.get("impArr0y") %> <%= iaMap.get("impArr1x") %>,<%= iaMap.get("impArr1y") %> <%= iaMap.get("impArr2x") %>,<%= iaMap.get("impArr2y") %>" style="stroke: black; stroke-opacity: 1; stroke-width: 1; fill: #000000;"/>

  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= lgSize %>" id="svg_12" x="<%= iaMap.get("impTxt1x") %>" y="<%= iaMap.get("impTxt1y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >I</text>
  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= smSize %>" id="svg_12" x="<%= iaMap.get("impTxt2x") %>" y="<%= iaMap.get("impTxt2y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >A</text>
<%        
    }
%>

<%

    if (ibMap != null) {
%>
  <!-- Ib -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= ibMap.get("impSx") %>" y1="<%= ibMap.get("impSy") %>" x2="<%= ibMap.get("impEx") %>" y2="<%= ibMap.get("impEy") %>" id="svg_10"/>

  <!-- 화살표 -->  
  <polygon points="<%= ibMap.get("impArr0x") %>,<%= ibMap.get("impArr0y") %> <%= ibMap.get("impArr1x") %>,<%= ibMap.get("impArr1y") %> <%= ibMap.get("impArr2x") %>,<%= ibMap.get("impArr2y") %>" style="stroke: black; stroke-opacity: 1; stroke-width: 1; fill: #000000;"/>

  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= lgSize %>" id="svg_12" x="<%= ibMap.get("impTxt1x") %>" y="<%= ibMap.get("impTxt1y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >I</text>
  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= smSize %>" id="svg_12" x="<%= ibMap.get("impTxt2x") %>" y="<%= ibMap.get("impTxt2y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >B</text>
<%        
    }
%>

<%
    if (icMap != null) {
%>
  <!-- Ic -->
  <line fill="none" stroke="#000000" stroke-dasharray="null" stroke-linejoin="null" stroke-linecap="null" x1="<%= icMap.get("impSx") %>" y1="<%= icMap.get("impSy") %>" x2="<%= icMap.get("impEx") %>" y2="<%= icMap.get("impEy") %>" id="svg_10"/>

  <!-- 화살표 -->  
  <polygon points="<%= icMap.get("impArr0x") %>,<%= icMap.get("impArr0y") %> <%= icMap.get("impArr1x") %>,<%= icMap.get("impArr1y") %> <%= icMap.get("impArr2x") %>,<%= icMap.get("impArr2y") %>" style="stroke: black; stroke-opacity: 1; stroke-width: 1; fill: #000000;"/>

  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= lgSize %>" id="svg_12" x="<%= icMap.get("impTxt1x") %>" y="<%= icMap.get("impTxt1y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >I</text>
  <text xml:space="preserve" text-anchor="middle" font-family="Times New Roman" font-size="<%= smSize %>" id="svg_12" x="<%= icMap.get("impTxt2x") %>" y="<%= icMap.get("impTxt2y") %>" stroke-linecap="null" stroke-linejoin="null" stroke-dasharray="null" stroke-width="0" stroke="#000000" fill="#000000"
  >C</text>
<%        
    }

%>



  <!-- 
  
  <polygon points="27.5,0 0,48 55,48" transform="translate(404,143) scale(-1,1) " style="stroke: black; stroke-opacity: 1; stroke-width: 1; fill: #9dc2de"/>
  <polygon points="27.5,10 10,48 55,48" style="stroke: black; stroke-opacity: 1; stroke-width: 1; fill: none;"/>
   -->
 </g>
</svg>