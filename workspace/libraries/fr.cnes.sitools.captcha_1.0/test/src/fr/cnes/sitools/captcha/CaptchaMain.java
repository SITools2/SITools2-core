package fr.cnes.sitools.captcha;

import java.awt.image.BufferedImage;

import nl.captcha.Captcha;
import nl.captcha.CaptchaBean;

public class CaptchaMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int width = 100;
		int height = 100;
		CaptchaBean captcha = new CaptchaBean(width, height);
		captcha.build();
		String answer = captcha.getAnswer();
		BufferedImage image = captcha.getImage();
		
		String userAnswer = "toto";
		boolean authorize = captcha.isCorrect(userAnswer);
	}

}
