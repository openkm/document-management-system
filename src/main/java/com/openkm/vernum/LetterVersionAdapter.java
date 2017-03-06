package com.openkm.vernum;

import com.openkm.dao.bean.NodeDocument;
import com.openkm.dao.bean.NodeDocumentVersion;
import org.hibernate.Query;
import org.hibernate.Session;

public class LetterVersionAdapter implements VersionNumerationAdapter {
	public String getInitialVersionNumber() {
		return "A";
	}

	public String getNextVersionNumber(Session paramSession, NodeDocument paramNodeDocument, NodeDocumentVersion paramNodeDocumentVersion, int paramInt) {
		String str1 = paramNodeDocumentVersion.getName();
		String str2 = "";
		String[] arrayOfString = new String[702];
		Query localQuery = paramSession.createQuery("from NodeDocumentVersion ndv where ndv.parent=:parent and ndv.name=:name");
		NodeDocumentVersion localNodeDocumentVersion = null;
		buildR(arrayOfString, 702);
		str2 = searchArr(arrayOfString, str1);
		do {
			localQuery.setString("parent", paramNodeDocument.getUuid());
			localQuery.setString("name", str2);
			localNodeDocumentVersion = (NodeDocumentVersion) localQuery.setMaxResults(1).uniqueResult();
		} while (localNodeDocumentVersion != null);

		return str2;
	}

	public void buildR(String[] paramArrayOfString, int paramInt) {
		char[] arrayOfChar1 = new char[1];
		char[] arrayOfChar2 = new char[2];
		for (int i = 0; i < paramInt; i++) {
			int k = i / 26;
			if (k == 0) {
				int j = i + 65;
				arrayOfChar1[0] = ((char) j);
				paramArrayOfString[i] = new String(arrayOfChar1);
			} else {
				arrayOfChar2[0] = ((char) (k + 64));
				arrayOfChar2[1] = ((char) (i % 26 + 65));
				paramArrayOfString[i] = new String(arrayOfChar2);
			}
		}
	}

	public String searchArr(String[] paramArrayOfString, String paramString) {
		String str = "";
		for (int i = 0; i < paramArrayOfString.length; i++) {
			if (paramArrayOfString[i].equals(paramString))
				str = paramArrayOfString[(++i)];
		}
		return str;
	}
}