var contextRoot = "aimir-web";
/* 대쉬 보드 생성을 위한 ExtJS 및 모듈을 정의 */
/* ExtJS 기반 모듈 - 접근 금지 */
document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/jquery-1.4.2.min.js'></script>");
document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/extjs/adapter/ext/ext-base.js'></script>");
document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/extjs/ext-all.js'></script>");
/* 전체 레이아웃 및 대쉬보드 설정을 위한 Javascript */
document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/extjs-layouts/styleswitcher.js'></script>");
document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/extjs-layouts/GadgetModules.js'></script>");

//하단의 GadgetLayout.js는 /gadget/index.jsp로 이동(for 국제화 처리) 
/*document.write("<script type='text/javascript' charset='utf-8' src='/"+contextRoot+"/js/extjs-layouts/GadgetLayout.js'></script>");*/
