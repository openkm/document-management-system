package com.openkm.servlet.frontend.util;

import com.openkm.frontend.client.bean.GWTFolder;

/**
 * FolderComparator
 *
 * @author jllort
 */
public class FolderComparator extends CultureComparator<GWTFolder> {

	protected FolderComparator(String locale) {
		super(locale);
	}

	public static FolderComparator getInstance(String locale) {
		try {
			return (FolderComparator) CultureComparator.getInstance(FolderComparator.class, locale);
		} catch (Exception e) {
			return new FolderComparator(locale);
		}
	}

	public static FolderComparator getInstance() {
		return getInstance(CultureComparator.DEFAULT_LOCALE);
	}

	public int compare(GWTFolder first, GWTFolder second) {
		return collator.compare(first.getName(), second.getName());
	}
}
