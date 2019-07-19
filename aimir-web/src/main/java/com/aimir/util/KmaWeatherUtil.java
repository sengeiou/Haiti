package com.aimir.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.aimir.util.Weather.Data;

/**
 * 기상청에 연결하여 날씨 정보를 가져온다.
 * URL과 HTTP를 사용하며 반환은 자바빈 형식으로 반환한다.
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
public final class KmaWeatherUtil {

	private static final String PROTOCOL = "http";
	private static final String KMA_DOMAIN = "www.kma.go.kr";
	private static final String TOWN_FILE = "/wid/queryDFS.jsp";
	private static final int RES_TIMEOUT = 5000;
	
	public enum WeatherLocation {
		
		BangBeaDong("방배1동", 60, 125),
		JungMunDong("중문동", 51, 32);
		
		String name;
		int gridX;
		int gridY;
		
		WeatherLocation(String name, int gridX, int gridY) {
			this.name = name;
			this.gridX = gridX;
			this.gridY = gridY;
		}
		
		public static WeatherLocation getByLocation(String loc) {
			if(BangBeaDong.getName().equals(loc)) {
				return BangBeaDong;
			}
			else if(JungMunDong.getName().equals(loc)) {
				return JungMunDong;
			}
			else {
				throw new IllegalAccessError(loc + " is undefined Weatherlocation");
			}
		}
		
		public int getGridX() {
			return gridX;
		}
		public int getGridY() {
			return gridY;
		}
		public String getName() {
			return name;
		}
		
		// XXX: ...
		public String getQueryString() {
			return "gridx=" + getGridX() + "&gridy=" + getGridY();
		}
	}
	
	public static Weather getWeather(WeatherLocation w) {
		Weather weather = new Weather();
		HttpURLConnection conn = null;
		try {
			conn = getConnection(getURLString(w));
			Document doc = buildDocumentFromConnection(conn);
			
			Element root = doc.getRootElement();
			Element header = root.getChild("header");
			Element body = root.getChild("body");
			
			weather.setTm(header.getChild("tm").getText());
			weather.setTs(Integer.parseInt(header.getChild("ts").getText()));
			weather.setX(Integer.parseInt(header.getChild("x").getText()));
			weather.setY(Integer.parseInt(header.getChild("y").getText()));
			
			@SuppressWarnings("unchecked")
			List<Element> dataList = body.getChildren("data")
			;
			List<Data> wd =  weather.getData();
			Data d = null;
			for (Element element : dataList) {
				d = new Data();
				d.setSeq(Integer.parseInt(element.getAttributeValue("seq")));
				d.setHour(Integer.parseInt(element.getChild("hour").getText()));
				d.setDay(Integer.parseInt(element.getChild("day").getText()));
				d.setTemp(Double.parseDouble(element.getChild("temp").getText()));
				d.setTmx(Double.parseDouble(element.getChild("tmx").getText()));
				d.setTmn(Double.parseDouble(element.getChild("tmn").getText()));
				d.setSky(Integer.parseInt(element.getChild("sky").getText()));
				d.setPty(Integer.parseInt(element.getChild("pty").getText()));
				d.setWfKor(element.getChild("wfKor").getText());
				d.setWfEn(element.getChild("wfEn").getText());
				d.setPop(Double.parseDouble(element.getChild("pop").getText()));
				d.setR12(Double.parseDouble(element.getChild("r12").getText()));
				d.setS12(Double.parseDouble(element.getChild("s12").getText()));
				d.setWs(Double.parseDouble(element.getChild("ws").getText()));
				d.setWd(Double.parseDouble(element.getChild("wd").getText()));
				d.setWdKor(element.getChild("wdKor").getText());
				d.setWdEn(element.getChild("wdEn").getText());
				d.setReh(Double.parseDouble(element.getChild("reh").getText()));
				wd.add(d);
			}
			return weather;
		}
		finally {
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
	}
	
	public static Weather getWeather(String loc) {
		WeatherLocation w = WeatherLocation.getByLocation(loc);
		return getWeather(w);
	}
	
	private static Document buildDocumentFromConnection(HttpURLConnection conn) {
		try {
			return new SAXBuilder().build(conn.getInputStream());
		}
		catch (Exception e) {
			throw new IllegalStateException("xml document bulid failed", e);
		}
	}
	
	private static String getURLString(WeatherLocation w) {
		return PROTOCOL + "://" + KMA_DOMAIN + TOWN_FILE + "?" + w.getQueryString();
	}
	
	private static HttpURLConnection getConnection(URL url) {
		if(url == null) {
			throw new IllegalAccessError("url is null");
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(RES_TIMEOUT);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			return conn;
		}
		catch (IOException e) {
			throw new IllegalStateException("cannot open connection from " + url, e);
		}		
	}
	
	private static HttpURLConnection getConnection(String url) {
		try {
			return getConnection(new URL(url));
		}
		catch (MalformedURLException e) {
			throw new IllegalStateException("cannot open connection from ", e);
		}
	}	
}
