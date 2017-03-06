package com.openkm.servlet.mobile;

import com.openkm.bean.Folder;
import com.openkm.util.PathUtils;

import java.util.Comparator;

public class FolderComparator implements Comparator<Folder> {
	private static final Comparator<Folder> INSTANCE = new FolderComparator();

	public static Comparator<Folder> getInstance() {
		return INSTANCE;
	}

	@Override
	public int compare(Folder arg0, Folder arg1) {
		String first = PathUtils.getName(arg0.getPath());
		String second = PathUtils.getName(arg1.getPath());
		return first.compareTo(second);
	}
}
