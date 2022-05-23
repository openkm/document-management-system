package com.openkm.frontend.client.widget.toolbar;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.ToolBarOption;

public class DocumentSecurityHelper {

	/**
	 * menuPopupEvaluation
	 * <p>
	 * Is used to evaluate menu popup in panels where toolbar options are disabled
	 */
	public static ToolBarOption menuPopupEvaluation(ToolBarOption toolBarOption, GWTDocument doc) {
		boolean evaluateDownload = Main.get().mainPanel.topPanel.toolBar.isEvaluateDownload();
		toolBarOption.downloadOption = false;
		toolBarOption.findSimilarDocumentOption = true;

		if (evaluateDownload && ((doc.getPermissions() & GWTPermission.DOWNLOAD) == GWTPermission.DOWNLOAD)) {
			toolBarOption.downloadOption = true;
		} else if (!evaluateDownload) {
			toolBarOption.downloadOption = true;
		}

		return toolBarOption;
	}
}
