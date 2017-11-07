/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.omr;

import net.sourceforge.jiu.codecs.CodecMode;
import net.sourceforge.jiu.codecs.ImageLoader;
import net.sourceforge.jiu.codecs.PNGCodec;
import net.sourceforge.jiu.color.reduction.RGBToGrayConversion;
import net.sourceforge.jiu.data.Gray8Image;
import net.sourceforge.jiu.data.PixelImage;
import net.sourceforge.jiu.data.RGB24Image;

/**
 * @author Aaditeshwar Seth
 */
public class ImageUtil {

	/**
	 * readImage
	 */
	public static Gray8Image readImage(String filename) {
		Gray8Image grayimage = null;
		RGB24Image redimage = null;

		try {
			PixelImage image = ImageLoader.load(filename);
			if (image == null) {
				return null;
			} else {
				if (image.getImageType().toString().indexOf("RGB") != -1) {
					redimage = (RGB24Image) (ImageLoader.load(filename));
					RGBToGrayConversion rgbtogray = new RGBToGrayConversion();
					rgbtogray.setInputImage(redimage);
					// adjust this if needed
					// rgbtogray.setColorWeights(0.3f, 0.3f, 0.4f);
					rgbtogray.process();
					grayimage = (Gray8Image) (rgbtogray.getOutputImage());
				} else if (image.getImageType().toString().indexOf("Gray") != -1) {
					grayimage = (Gray8Image) (image);
				} else {
					grayimage = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.exit(-1);
		}

		return grayimage;
	}

	/**
	 * saveImage
	 */
	public static void saveImage(PixelImage img, String filename) {
		try {
			PNGCodec codec = new PNGCodec();
			codec.setFile(filename, CodecMode.SAVE);
			codec.setImage(img);
			codec.setCompressionLevel(0);
			codec.process();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * putMark
	 */
	public static void putMark(Gray8Image img, int x, int y, boolean color) {
		if (color) {
			img.putBlack(x, y);
			img.putBlack(x + 1, y + 1);
			img.putBlack(x - 1, y - 1);
			img.putBlack(x + 1, y);
			img.putBlack(x - 1, y);
			img.putBlack(x, y + 1);
			img.putBlack(x, y - 1);
			img.putBlack(x + 1, y - 1);
			img.putBlack(x - 1, y + 1);
			img.putBlack(x - 1, y - 1);
		} else {
			img.putWhite(x, y);
			img.putWhite(x + 1, y + 1);
			img.putWhite(x - 1, y - 1);
			img.putWhite(x + 1, y);
			img.putWhite(x - 1, y);
			img.putWhite(x, y + 1);
			img.putWhite(x, y - 1);
			img.putWhite(x + 1, y - 1);
			img.putWhite(x - 1, y + 1);
			img.putWhite(x - 1, y - 1);
		}
	}
}
