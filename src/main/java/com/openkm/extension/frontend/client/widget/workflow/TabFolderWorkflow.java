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

package com.openkm.extension.frontend.client.widget.workflow;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabFolderComunicator;
import com.openkm.frontend.client.extension.event.HasFolderEvent;
import com.openkm.frontend.client.extension.event.HasFolderEvent.FolderEventConstant;
import com.openkm.frontend.client.extension.event.handler.FolderHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabfolder.TabFolderExtension;

/**
 * TabFolderWorkflow
 *
 * @author jllort
 *
 */
public class TabFolderWorkflow extends TabFolderExtension implements FolderHandlerExtension {
	private ScrollPanel scrollPanel;
	private String title = "";
	private WorkflowManager workflowManager;

	/**
	 * TabDocumentWorkflow
	 */
	public TabFolderWorkflow() {
		title = GeneralComunicator.i18nExtension("workflow.tab.title");
		workflowManager = new WorkflowManager();
		scrollPanel = new ScrollPanel(workflowManager);
		initWidget(scrollPanel);
	}

	@Override
	public void setPixelSize(int width, int height) {
		scrollPanel.setPixelSize(width, height);
		workflowManager.setPixelSize(width, height);
		workflowManager.workflowTable.setPixelSize(width, height - 25);
		workflowManager.workflowTable.fillWidth();
		workflowManager.workflowDetailTable.setPixelSize(width, height - 25);
		workflowManager.workflowDetailTable.fillWidth();
		workflowManager.scrollGraphPanel.setPixelSize(width, height - 25);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		title = GeneralComunicator.i18nExtension("workflow.tab.title");
		workflowManager.langRefresh();
	}

	@Override
	public String getTabText() {
		return title;
	}

	@Override
	public void onChange(FolderEventConstant event) {
		if (event.equals(HasFolderEvent.FOLDER_CHANGED)) {
			Workflow.get().setTabFolderSelected();
			workflowManager.findProcessInstancesByNode(Workflow.get().getUuid());
		} else if (event.equals(HasFolderEvent.TAB_CHANGED)) {
			if (TabFolderComunicator.isWidgetExtensionVisible(this)) {
				Timer timer = new Timer() {
					@Override
					public void run() {
						workflowManager.fillWidth();
					}
				};
				timer.schedule(100);
			}
		}
	}

	@Override
	public void set(GWTFolder doc) {
	}

	@Override
	public void setVisibleButtons(boolean visible) {
	}


}