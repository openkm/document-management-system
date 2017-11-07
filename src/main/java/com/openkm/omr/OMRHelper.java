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

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMRepository;
import com.openkm.automation.AutomationException;
import com.openkm.bean.Document;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Select;
import com.openkm.core.*;
import com.openkm.dao.OmrDAO;
import com.openkm.dao.bean.Omr;
import com.openkm.extension.core.ExtensionException;
import com.openkm.util.FileUtils;
import net.sourceforge.jiu.codecs.InvalidFileStructureException;
import net.sourceforge.jiu.codecs.InvalidImageIndexException;
import net.sourceforge.jiu.codecs.UnsupportedTypeException;
import net.sourceforge.jiu.data.Gray8Image;
import net.sourceforge.jiu.ops.MissingParameterException;
import net.sourceforge.jiu.ops.WrongParameterException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OMRHelper
 *
 * @author jllort
 */
public class OMRHelper {
	public static final String ASC_FILE = "ASC_FILE";
	public static final String CONFIG_FILE = "CONFIG_FILE";

	/**
	 * isValid
	 */
	public static boolean isValid(Document doc) {
		return doc.getMimeType().equals(MimeTypeConfig.MIME_PNG);
	}

	/**
	 * trainingTemplate
	 */
	public static Map<String, File> trainingTemplate(File template) throws IOException, InvalidFileStructureException,
			InvalidImageIndexException, UnsupportedTypeException, MissingParameterException, WrongParameterException {
		Map<String, File> fileMap = new HashMap<String, File>();
		Gray8Image grayimage = ImageUtil.readImage(template.getCanonicalPath());
		ImageManipulation image = new ImageManipulation(grayimage);
		image.locateConcentricCircles();
		image.locateMarks();
		File ascFile = FileUtils.createTempFile();
		File configFile = FileUtils.createTempFile();
		image.writeAscTemplate(ascFile.getCanonicalPath());
		image.writeConfig(configFile.getCanonicalPath());
		fileMap.put(ASC_FILE, ascFile);
		fileMap.put(CONFIG_FILE, configFile);
		return fileMap;
	}

	/**
	 * process
	 */
	public static Map<String, String> process(File fileToProcess, long omId) throws IOException, OMRException, DatabaseException,
			InvalidFileStructureException, InvalidImageIndexException, UnsupportedTypeException, MissingParameterException,
			WrongParameterException {
		Map<String, String> values = new HashMap<String, String>();
		Omr omr = OmrDAO.getInstance().findByPk(omId);
		InputStream asc = new ByteArrayInputStream(omr.getAscFileContent());
		InputStream config = new ByteArrayInputStream(omr.getConfigFileContent());
		InputStream fields = new ByteArrayInputStream(omr.getFieldsFileContent());

		if (asc != null && asc.available() > 0 && config != null && config.available() > 0 && fields != null && fields.available() > 0) {
			Gray8Image grayimage = ImageUtil.readImage(fileToProcess.getCanonicalPath());
			if (grayimage == null) {
				throw new OMRException("Not able to process the image as gray image");
			}

			ImageManipulation image = new ImageManipulation(grayimage);
			image.locateConcentricCircles();
			image.readConfig(config);
			image.readFields(fields);
			image.readAscTemplate(asc);
			image.searchMarks();
			File dataFile = FileUtils.createTempFile();
			image.saveData(dataFile.getCanonicalPath());

			// Parse data file

			FileInputStream dfStream = new FileInputStream(dataFile);
			DataInputStream in = new DataInputStream(dfStream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				// format key=value ( looking for first = )
				String key = "";
				String value = "";

				if (strLine.contains("=")) {
					key = strLine.substring(0, strLine.indexOf("="));
					value = strLine.substring(strLine.indexOf("=") + 1);
					value = value.trim();
				}

				if (!key.equals("")) {
					if (value.equals("")) {
						IOUtils.closeQuietly(br);
						IOUtils.closeQuietly(in);
						IOUtils.closeQuietly(dfStream);
						IOUtils.closeQuietly(asc);
						IOUtils.closeQuietly(config);
						IOUtils.closeQuietly(fields);
						throw new OMRException("Empty value");
					}

					if (omr.getProperties().contains(key)) {
						values.put(key, value);
					}
				}
			}

			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(dfStream);
			IOUtils.closeQuietly(asc);
			IOUtils.closeQuietly(config);
			IOUtils.closeQuietly(fields);
			FileUtils.deleteQuietly(dataFile);
			return values;
		} else {
			throw new OMRException("Error asc, config or fields files not found");
		}
	}

	/**
	 * storeMetadata
	 */
	public static void storeMetadata(Map<String, String> results, String docPath) throws IOException, ParseException,
			PathNotFoundException, RepositoryException, DatabaseException, NoSuchGroupException, LockException, AccessDeniedException,
			ExtensionException, AutomationException, NoSuchPropertyException, OMRException {
		List<String> groups = new ArrayList<String>();

		for (String key : results.keySet()) {
			if (key.contains(":")) {
				String grpName = key.substring(0, key.indexOf("."));

				// convert to okg (group name always start with okg )
				grpName = grpName.replace("okp", "okg");
				if (!groups.contains(grpName)) {
					groups.add(grpName);
				}
			}
		}

		// Add missing groups
		for (PropertyGroup registeredGroup : OKMPropertyGroup.getInstance().getGroups(null, docPath)) {
			if (groups.contains(registeredGroup.getName())) {
				groups.remove(registeredGroup.getName());
			}
		}
		// Add properties
		for (String grpName : groups) {
			OKMPropertyGroup.getInstance().addGroup(null, docPath, grpName);

			// convert okg to okp ( property format )
			String propertyBeginning = grpName.replace("okg", "okp");
			Map<String, String> properties = new HashMap<String, String>();

			for (String key : results.keySet()) {
				if (key.startsWith(propertyBeginning)) {
					String value = results.get(key);

					// Evaluate select multiple otherside throw exception
					if (value.contains(" ")) {
						for (FormElement formElement : OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName)) {
							if (formElement.getName().equals(key) && formElement instanceof Select) {
								if (!((Select) formElement).getType().equals(Select.TYPE_MULTIPLE)) {
									throw new OMRException(
											"Found multiple value in a non multiple select. White space indicates multiple value");
								} else {
									// Change " " to ";" the way to pass
									// multiple values into setPropertiesSimple
									value = value.replaceAll(" ", ";");
								}
							}
						}
					}

					properties.put(key, value);
				}
			}

			OKMPropertyGroup.getInstance().setPropertiesSimple(null, docPath, grpName, properties);
		}
	}

	/**
	 * processAndStoreMetadata
	 */
	public static void processAndStoreMetadata(long omId, String uuid) throws IOException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, OMRException, NoSuchGroupException, LockException, ExtensionException, ParseException,
			NoSuchPropertyException, AutomationException, InvalidFileStructureException, InvalidImageIndexException,
			UnsupportedTypeException, MissingParameterException, WrongParameterException {
		InputStream is = null;
		File fileToProcess = null;

		try {
			String docPath = OKMRepository.getInstance().getNodePath(null, uuid);

			// create tmp content file
			fileToProcess = FileUtils.createTempFile();
			is = OKMDocument.getInstance().getContent(null, docPath, false);
			FileUtils.copy(is, fileToProcess);
			is.close();

			// process
			Map<String, String> results = OMRHelper.process(fileToProcess, omId);
			OMRHelper.storeMetadata(results, docPath);
		} catch (IOException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (RepositoryException e) {
			throw e;
		} catch (DatabaseException e) {
			throw e;
		} catch (OMRException e) {
			throw e;
		} catch (NoSuchGroupException e) {
			throw e;
		} catch (LockException e) {
			throw e;
		} catch (ExtensionException e) {
			throw e;
		} catch (ParseException e) {
			throw e;
		} catch (NoSuchPropertyException e) {
			throw e;
		} catch (AutomationException e) {
			throw e;
		} catch (InvalidFileStructureException e) {
			throw e;
		} catch (InvalidImageIndexException e) {
			throw e;
		} catch (UnsupportedTypeException e) {
			throw e;
		} catch (MissingParameterException e) {
			throw e;
		} catch (WrongParameterException e) {
			throw e;
		} finally {
			FileUtils.deleteQuietly(fileToProcess);
		}
	}
}