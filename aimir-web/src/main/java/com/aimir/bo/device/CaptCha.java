package com.aimir.bo.device;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static nl.captcha.Captcha.NAME;

public class CaptCha {

	private static final long serialVersionUID = 1L;
	private static int width = 150; // 이미지 가로크기
	private static int height = 50; // 이미지 높이

	public void getCaptCha(HttpServletRequest req, HttpServletResponse res) {

		try {
			Captcha captcha = null;
			captcha = new Captcha.Builder(width, height)
					.addText().gimp(new FishEyeGimpyRenderer())
					.addNoise().addBorder().addBackground(new GradiatedBackgroundProducer())
					.build();

			// JSP에서 Captcha 객체에 접근할 수 있도록 Session에 저장한다.
			req.getSession().setAttribute(NAME, captcha); 
			CaptchaServletUtil.writeImage(res, captcha.getImage());
		} catch (Exception ie) {
			ie.printStackTrace();
		}
	}
}