package com.openkm.servlet.mobile;

import com.openkm.bean.Document;
import com.openkm.util.PathUtils;

import java.util.Comparator;

public class DocumentComparator implements Comparator<Document> {
	private static final Comparator<Document> INSTANCE = new DocumentComparator();

	public static Comparator<Document> getInstance() {
		return INSTANCE;
	}

	@Override
	public int compare(Document arg0, Document arg1) {
		String first = PathUtils.getName(arg0.getPath());
		String second = PathUtils.getName(arg1.getPath());
		return first.compareTo(second);
	}
}
