package edu.umd.coral.ui.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class StripedImage {

	public static TexturePaint getTexturePaint(Color c) {
		int img_width = 20;
		BufferedImage img = new BufferedImage(img_width, img_width, BufferedImage.TYPE_INT_ARGB);
		Graphics2D imgG = img.createGraphics();
		imgG.setColor(c);
		int base = 7;
		float stroke = (float) (base * Math.sqrt(2));
		System.out.println(stroke);
		imgG.setStroke(new BasicStroke(stroke));
		// draw three stripes?
		imgG.drawLine(-base/2, base/2, base/2, -base/2);
		imgG.drawLine(0, img_width, img_width, 0);
		imgG.drawLine(img_width - base/2, img_width + base/2, img_width+base/2, img_width - base/2);
		
		TexturePaint tp = new TexturePaint(img, new Rectangle2D.Float(0f, 0f, (float)img_width, (float)img_width));
		return tp;
	}
}
