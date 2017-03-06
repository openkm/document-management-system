package com.openkm.servlet.frontend.util;

import com.openkm.frontend.client.bean.GWTFolder;

public class FolderComparator extends CultureComparator<GWTFolder> {

	protected FolderComparator(String locale) {
		super(locale);
	}

	public static FolderComparator getInstance(String locale) {
		try {
			FolderComparator comparator = (FolderComparator) CultureComparator.getInstance(FolderComparator.class, locale);
			return comparator;
		} catch (Exception e) {
			return new FolderComparator(locale);
		}
	}

	public static FolderComparator getInstance() {
		FolderComparator instance = getInstance(CultureComparator.DEFAULT_LOCALE);
		return instance;
	}

	public int compare(GWTFolder arg0, GWTFolder arg1) {
		GWTFolder first = arg0;
		GWTFolder second = arg1;

		return collator.compare(first.getName(), second.getName());
	}
}
