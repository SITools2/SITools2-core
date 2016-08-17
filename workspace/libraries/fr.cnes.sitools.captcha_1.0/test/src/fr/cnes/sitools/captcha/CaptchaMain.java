 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
