/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.openkm.api.OKMSearch;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.QueryResult;
import com.openkm.bean.json.FindSimpleQueryValues;
import com.openkm.core.*;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.dao.bean.Translation;
import com.openkm.dao.bean.UserConfig;
import com.openkm.frontend.client.bean.GWTFilebrowseExtraColumn;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.form.*;
import com.openkm.principal.PrincipalAdapterException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSVUtil
 *
 * @author jllort
 */
public class CSVUtil {

	/**
	 * createFind
	 */
	public static String createFind(String lang, String user, List<String[]> csvValues, String json, boolean compact)
			throws DatabaseException, AccessDeniedException, PathNotFoundException, IOException, ParseException, RepositoryException,
			PrincipalAdapterException, NoSuchGroupException {
		Gson gson = new GsonBuilder().setDateFormat(ISO8601.BASIC_PATTERN).create();
		String fileName = "";

		// Getting translations
		Map<String, String> translations = LanguageUtils.getTranslations(lang,
				new String[]{Translation.MODULE_FRONTEND});
		Map<String, GWTFilebrowseExtraColumn> ecMap = new HashMap<String, GWTFilebrowseExtraColumn>();

		Profile up = new Profile();
		UserConfig uc = UserConfigDAO.findByPk(user);
		up = uc.getProfile();

		int cols = 0;
		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			cols++;
		}
		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			cols++;
		}

		cols++; // Path column
		cols += CSVUtil.numberOfExtraColumns(up);
		String[] columns = new String[cols];

		int index = 0;
		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type");
		}
		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.name");
		}
		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.size");
		}
		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.date.update");
		}
		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.author");
		}
		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.version");
		}
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.path");


		ecMap = CSVUtil.addExtraColumns(columns, up, index);
		csvValues.add(columns);
		DateFormat sdf = new SimpleDateFormat(translations.get(Translation.MODULE_FRONTEND + "." + "general.date.pattern"));
		fileName = sdf.format(Calendar.getInstance().getTime()) + "-find-export.csv";

		// Json conversion
		GWTQueryParams params = gson.fromJson(json, GWTQueryParams.class);
		for (QueryResult qr : OKMSearch.getInstance().find(null, GWTUtil.copy(params))) {
			csvValues.add(CSVUtil.toArray(qr, sdf, translations, cols, up, ecMap, compact));
		}

		return fileName;
	}

	/**
	 * createFindSimpleQuery
	 */
	public static String createFindSimpleQuery(String lang, String user, List<String[]> csvValues, String json) throws DatabaseException,
			AccessDeniedException, PathNotFoundException, IOException, ParseException, RepositoryException, PrincipalAdapterException,
			NoSuchGroupException {
		Gson gson = new GsonBuilder().setDateFormat(ISO8601.BASIC_PATTERN).create();
		String fileName = "";

		// Getting translations
		Map<String, String> translations = LanguageUtils.getTranslations(lang,
				new String[]{Translation.MODULE_FRONTEND});
		Map<String, GWTFilebrowseExtraColumn> ecMap = new HashMap<String, GWTFilebrowseExtraColumn>();

		Profile up = new Profile();
		UserConfig uc = UserConfigDAO.findByPk(user);
		up = uc.getProfile();

		int cols = 7 + CSVUtil.numberOfExtraColumns(up);
		String[] columns = new String[cols];

		int index = 0;
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.name");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.size");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.date.update");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.author");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.result.version");
		columns[index++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.path");

		ecMap = CSVUtil.addExtraColumns(columns, up, index);
		csvValues.add(columns);
		DateFormat sdf = new SimpleDateFormat(translations.get(Translation.MODULE_FRONTEND + "." + "general.date.pattern"));
		fileName = sdf.format(Calendar.getInstance().getTime()) + "-find-export.csv";

		// Json conversion
		FindSimpleQueryValues fqs = gson.fromJson(json, FindSimpleQueryValues.class);
		for (QueryResult qr : OKMSearch.getInstance().findSimpleQuery(null, fqs.getStatement())) {
			csvValues.add(CSVUtil.toArray(qr, sdf, translations, cols, up, ecMap, false));
		}

		return fileName;
	}

	/**
	 * toArray
	 */
	private static String[] toArray(QueryResult qr, DateFormat dtf, Map<String, String> translations, int cols,
	                                Profile up, Map<String, GWTFilebrowseExtraColumn> ecMap, boolean compact) throws IOException, ParseException,
			NoSuchGroupException, AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException,
			PrincipalAdapterException {
		String[] columns = new String[cols];
		if (qr.getNode() instanceof Document) {
			if (!qr.isAttachment()) {
				columns = handleDocument((Document) qr.getNode(), dtf, translations, cols, up, ecMap, compact);	
			} else {
				columns = handleDocument((Document) qr.getNode(), dtf, translations, cols, up, ecMap, compact);				
			}
		} else if (qr.getNode() instanceof Folder) {
			columns = handleFolder((Folder) qr.getNode(), dtf, translations, cols, up, ecMap, compact);
		} else if (qr.getNode() instanceof Mail) {
			columns = handleMail((Mail) qr.getNode(), dtf, translations, cols, up, ecMap, compact);
		}
		return columns;
	}

	/**
	 * Handle document
	 */
	private static String[] handleDocument(Document doc, DateFormat dtf, Map<String, String> translations, int cols, Profile up,
	                                       Map<String, GWTFilebrowseExtraColumn> ecMap, boolean compact) throws DatabaseException, ParseException, IOException,
			RepositoryException, PrincipalAdapterException, PathNotFoundException, AccessDeniedException, NoSuchGroupException {
		String[] columns = new String[cols];
		int col = 0;

		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.document");
		}

		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			columns[col++] = PathUtils.getName(doc.getPath());
		}

		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			columns[col++] = FormatUtil.formatSize(doc.getActualVersion().getSize());
		}

		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			columns[col++] = dtf.format(doc.getActualVersion().getCreated().getTime());
		}

		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			columns[col++] = doc.getAuthor();
		}

		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			columns[col++] = doc.getActualVersion().getName();
		}

		columns[col++] = Config.APPLICATION_URL + "?uuid=" + doc.getUuid();
		addExtraColumnsValues(columns, up, col, doc.getPath(), ecMap, dtf);
		return columns;
	}

	/**
	 * Handle folder
	 */
	private static String[] handleFolder(Folder fld, DateFormat dtf, Map<String, String> translations, int cols, Profile up,
	                                     Map<String, GWTFilebrowseExtraColumn> ecMap, boolean compact) throws DatabaseException, ParseException, IOException,
			RepositoryException, PrincipalAdapterException, PathNotFoundException, AccessDeniedException, NoSuchGroupException {
		String[] columns = new String[cols];
		int col = 0;

		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.folder");
		}

		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			columns[col++] = PathUtils.getName(fld.getPath());
		}

		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			columns[col++] = "";
		}

		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			columns[col++] = dtf.format(fld.getCreated().getTime());
		}

		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			columns[col++] = fld.getAuthor();
		}

		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			columns[col++] = "";
		}

		columns[col++] = Config.APPLICATION_URL + "?uuid=" + fld.getUuid();
		addExtraColumnsValues(columns, up, col, fld.getPath(), ecMap, dtf);
		return columns;
	}

	/**
	 * Handle mail
	 */
	private static String[] handleMail(Mail mail, DateFormat dtf, Map<String, String> translations, int cols, Profile up,
	                                   Map<String, GWTFilebrowseExtraColumn> ecMap, boolean compact) throws DatabaseException, ParseException, IOException,
			RepositoryException, PrincipalAdapterException, PathNotFoundException, AccessDeniedException, NoSuchGroupException {
		String[] columns = new String[cols];
		int col = 0;

		if (up.getPrfFileBrowser().isIconVisible() || !compact) {
			columns[col++] = translations.get(Translation.MODULE_FRONTEND + "." + "search.type.mail");
		}

		if (up.getPrfFileBrowser().isNameVisible() || !compact) {
			columns[col++] = mail.getSubject();
		}

		if (up.getPrfFileBrowser().isSizeVisible() || !compact) {
			columns[col++] = FormatUtil.formatSize(mail.getSize());
		}

		if (up.getPrfFileBrowser().isLastModifiedVisible() || !compact) {
			columns[col++] = dtf.format(mail.getCreated().getTime());
		}

		if (up.getPrfFileBrowser().isAuthorVisible() || !compact) {
			columns[col++] = mail.getAuthor();
		}

		if (up.getPrfFileBrowser().isVersionVisible() || !compact) {
			columns[col++] = "";
		}

		columns[col++] = Config.APPLICATION_URL + "?uuid=" + mail.getUuid();
		addExtraColumnsValues(columns, up, col, mail.getPath(), ecMap, dtf);
		return columns;
	}
	
	/**
	 * numberOfExtraColumns
	 */
	private static int numberOfExtraColumns(Profile up) {
		int cols = 0;

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn0())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn1())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn2())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn3())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn4())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn5())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn6())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn7())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn8())) {
			cols++;
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn9())) {
			cols++;
		}

		return cols;
	}

	/**
	 * addExtraColumns Note than extends columns values and return map of
	 * extended columns
	 */
	private static Map<String, GWTFilebrowseExtraColumn> addExtraColumns(String[] columns, Profile up, int index)
			throws IOException, ParseException, RepositoryException, DatabaseException, AccessDeniedException, PathNotFoundException,
			PrincipalAdapterException, NoSuchGroupException {
		Map<String, GWTFilebrowseExtraColumn> map = new HashMap<String, GWTFilebrowseExtraColumn>();

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn0())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn0());
			map.put(up.getPrfFileBrowser().getColumn0(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn1())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn1());
			map.put(up.getPrfFileBrowser().getColumn1(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn2())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn2());
			map.put(up.getPrfFileBrowser().getColumn2(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn3())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn3());
			map.put(up.getPrfFileBrowser().getColumn3(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn4())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn4());
			map.put(up.getPrfFileBrowser().getColumn4(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn5())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn5());
			map.put(up.getPrfFileBrowser().getColumn5(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn6())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn6());
			map.put(up.getPrfFileBrowser().getColumn6(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn7())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn7());
			map.put(up.getPrfFileBrowser().getColumn7(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn8())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn8());
			map.put(up.getPrfFileBrowser().getColumn8(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn9())) {
			GWTFilebrowseExtraColumn ec = GWTUtil.getExtraColumn(up.getPrfFileBrowser().getColumn9());
			map.put(up.getPrfFileBrowser().getColumn9(), ec);
			columns[index++] = ec.getFormElement().getLabel();
		}

		return map;
	}

	private static void addExtraColumnsValues(String[] columns, Profile up, int index, String path,
	                                          Map<String, GWTFilebrowseExtraColumn> map, DateFormat dtf) throws IOException, ParseException, NoSuchGroupException,
			AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException, PrincipalAdapterException {
		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn0())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn0())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn1())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn1())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn2())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn2())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn3())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn3())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn4())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn4())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn5())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn5())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn6())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn6())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn7())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn7())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn8())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn8())), dtf);
		}

		if (isValidPropertyGroup(up.getPrfFileBrowser().getColumn9())) {
			columns[index++] = getFormElementValue(GWTUtil.getExtraColumn(path,
					map.get(up.getPrfFileBrowser().getColumn9())), dtf);
		}
	}

	/**
	 * getFormElementValue
	 */
	private static String getFormElementValue(GWTFormElement formElement, DateFormat dtf) {
		String value = "";

		if (formElement instanceof GWTInput) {
			GWTInput input = (GWTInput) formElement;
			value = ((GWTInput) formElement).getValue();
			if (input.getType().equals(GWTInput.TYPE_DATE) && !value.isEmpty()) {
				Calendar cal = ISO8601.parseBasic(value);
				value = dtf.format(cal.getTime());
			}
		} else if (formElement instanceof GWTTextArea) {
			value = ((GWTTextArea) formElement).getValue();
		} else if (formElement instanceof GWTSuggestBox) {
			value = ((GWTSuggestBox) formElement).getValue();
		} else if (formElement instanceof GWTCheckBox) {
			value = String.valueOf(((GWTCheckBox) formElement).getValue());
		} else if (formElement instanceof GWTSelect) {
			GWTSelect select = (GWTSelect) formElement;

			for (GWTOption option : select.getOptions()) {
				if (option.isSelected()) {
					if (value.length() > 0) {
						value += "," + option.getValue();
					} else {
						value += option.getValue();
					}
				}
			}
		} else if (formElement instanceof GWTUpload) {
			// Nothing to do here
		} else if (formElement instanceof GWTText) {
			value = ((GWTText) formElement).getLabel();
		} else if (formElement instanceof GWTSeparator) {
			// Nothing to do here
		} else if (formElement instanceof GWTDownload) {
			// Nothing to do here
		} else if (formElement instanceof GWTPrint) {
			// Nothing to do here
		} else {
			// Nothing to do here
		}

		// Can not return null
		if (value != null) {
			return value;
		} else {
			return "";
		}
	}

	/**
	 * isValidPropertyGroup
	 */
	private static boolean isValidPropertyGroup(String propertyGroup) {
		return (propertyGroup != null && !propertyGroup.equals("") && propertyGroup.split("\\.").length >= 2);
	}
}