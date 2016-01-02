package com.widerwille.afterglow;

import com.intellij.util.RetinaImage;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AfterglowTintedIcon implements Icon
{
	private BufferedImage image;
	private int width;
	private int height;

	public AfterglowTintedIcon(Icon original, Color tint)
	{
		width = original.getIconWidth();
		height = original.getIconHeight();

		BufferedImage imageIcon = UIUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB);
		{
			Graphics graphics = imageIcon.createGraphics();
			original.paintIcon(null, graphics, 0, 0);
			graphics.dispose();
		}

		image = UIUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB);

		{
			for(int y = 0; y < image.getHeight(); y ++)
			{
				for(int x = 0; x < image.getWidth(); x ++)
				{
					int alpha = imageIcon.getRGB(x, y) >> 24 & 0xff;

					Color sourceColor = new Color(imageIcon.getRGB(x, y));
					Color imageColor = multiplyColor(new Color(sourceColor.getRed(), sourceColor.getGreen(), sourceColor.getBlue(), alpha), tint);

					image.setRGB(x, y, imageColor.getRGB());
				}
			}
		}
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public boolean isRetina()
	{
		return UIUtil.isRetina();
	}


	private Color multiplyColor(Color color1, Color color2)
	{
		float[] color1Components = color1.getRGBComponents(null);
		float[] color2Components = color2.getRGBColorComponents(null);
		float[] newComponents = new float[3];

		for(int i = 0; i < 3; i ++)
			newComponents[i] = color1Components[i] * color2Components[i];

		return new Color(newComponents[0], newComponents[1], newComponents[2], color1Components[3]);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.drawImage(image, x, y, getIconWidth(), getIconHeight(), c);
	}


	@Override
	public int getIconWidth()
	{
		return width;
	}

	@Override
	public int getIconHeight()
	{
		return height;
	}
}
