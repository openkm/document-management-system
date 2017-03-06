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

package com.openkm.util;

import com.openkm.bean.ExecutionResult;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.MimeTypeConfig;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

/**
 * ImageUtil
 *
 * @author jllort
 *
 * @see http://www.thebuzzmedia.com/software/imgscalr-java-image-scaling-library/
 * @see http://marvinproject.sourceforge.net/en/index.html
 */
public class ImageUtils {
	private static Logger log = LoggerFactory.getLogger(ImageUtils.class);

	/**
	 * cloneImage
	 */
	public static BufferedImage clone(BufferedImage source) {
		BufferedImage img = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return img;
	}

	/**
	 * pointWithinRange
	 */
	public static boolean pointWithinRange(Point p, BufferedImage img) {
		return !(p.x < 0 || p.y < 0 || p.x >= img.getWidth() || p.y >= img.getHeight());
	}

	/**
	 * crop
	 */
	public static byte[] crop(byte[] img, int x, int y, int width, int height) throws RuntimeException {
		log.debug("crop({}, {}, {}, {}, {})", new Object[]{img.length, x, y, width, height});
		ByteArrayInputStream bais = new ByteArrayInputStream(img);
		byte[] imageInByte;

		try {
			BufferedImage image = ImageIO.read(bais);
			BufferedImage croppedImage = image.getSubimage(x, y, width, height);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(croppedImage, "png", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			IOUtils.closeQuietly(baos);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException("IOException: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(bais);
		}

		log.debug("crop: {}", imageInByte.length);
		return imageInByte;
	}

	/**
	 * Rotate an image.
	 *
	 * @param input Image to rotate.
	 * @param output Image rotated.
	 * @param angle Rotation angle.
	 */
	public static void rotate(File input, File output, double angle) throws RuntimeException {
		log.debug("rotate({}, {}, {})", new Object[]{input, output, angle});
		String params = "-rotate " + angle + " ${fileIn} ${fileOut}";
		ImageMagickConvert(input, output, params);
	}

	/**
	 * Rotate an image.
	 *
	 * @param img Image to rotate.
	 * @param angle Rotation angle.
	 * @return the image rotated.
	 */
	public static byte[] rotate(byte[] img, double angle) throws RuntimeException {
		log.debug("rotate({}, {})", new Object[]{img.length, angle});
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileOutputStream fos = null;
		FileInputStream fis = null;
		byte[] ret = new byte[]{};
		File tmpFileIn = null;

		try {
			// Save to disk
			tmpFileIn = FileUtils.createTempFileFromMime(MimeTypeConfig.MIME_PNG);
			fos = new FileOutputStream(tmpFileIn);
			IOUtils.write(img, fos);
			IOUtils.closeQuietly(fos);

			// Rotate
			rotate(tmpFileIn, tmpFileIn, angle);

			// Read from disk
			fis = new FileInputStream(tmpFileIn);
			IOUtils.copy(fis, baos);
			IOUtils.closeQuietly(fis);
			ret = baos.toByteArray();
		} catch (DatabaseException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			FileUtils.deleteQuietly(tmpFileIn);
			IOUtils.closeQuietly(baos);
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(fis);
		}

		log.debug("crop: {}", ret.length);
		return ret;
	}

	/**
	 * Create image thumbnail.
	 */
	public static void createThumbnail(File input, String size, File output) throws RuntimeException {
		log.debug("createThumbnail({}, {}, {})", new Object[]{input, size, output});
		String params = "-thumbnail " + size + " -background white -flatten ${fileIn} ${fileOut}";
		ImageMagickConvert(input, output, params);
	}

	/**
	 * Resize image.
	 */
	public static void resize(File input, String size, File output) throws RuntimeException {
		log.debug("resize({}, {}, {})", new Object[]{input, size, output});
		String params = "-resize " + size + " ${fileIn} ${fileOut}";
		ImageMagickConvert(input, output, params);
	}

	/**
	 * Execute ImageMagick convert with parameters.
	 */
	public static void ImageMagickConvert(File input, File output, String params) {
		ImageMagickConvert(input.getPath(), output.getPath(), params);
	}

	/**
	 * Execute ImageMagick convert with parameters.
	 */
	public static void ImageMagickConvert(String input, String output, String params) {
		log.debug("ImageMagickConvert({}, {}, {})", new Object[]{input, output, params});
		String cmd = null;

		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fileIn", input);
			hm.put("fileOut", output);
			String tpl = Config.SYSTEM_IMAGEMAGICK_CONVERT + " " + params;
			cmd = TemplateUtils.replace("IMAGE_CONVERT", tpl, hm);
			ExecutionResult er = ExecutionUtils.runCmd(cmd);

			if (er.getExitValue() != 0) {
				throw new RuntimeException(er.getStderr());
			}
		} catch (SecurityException e) {
			throw new RuntimeException("Security exception executing command: " + cmd, e);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted exception executing command: " + cmd, e);
		} catch (IOException e) {
			throw new RuntimeException("IO exception executing command: " + cmd, e);
		} catch (TemplateException e) {
			throw new RuntimeException("Template exception", e);
		}
	}
}