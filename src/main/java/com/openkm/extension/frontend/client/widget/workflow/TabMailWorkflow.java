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
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.extension.comunicator.TabMailComunicator;
import com.openkm.frontend.client.extension.event.HasMailEvent;
import com.openkm.frontend.client.extension.event.HasMailEvent.MailEventConstant;
import com.openkm.frontend.client.extension.event.handler.MailHandlerExtension;
import com.openkm.frontend.client.extension.widget.tabmail.TabMailExtension;

/**
 * TabMailWorkflow
 *
 * @author jllort
 *
 */
public class TabMailWorkflow extends TabMailExtension implements MailHandlerExtension {
	private ScrollPanel scrollPanel;
	private String title = "";
	private WorkflowManager workflowManager;

	/**
	 * TabMailWorkflow
	 */
	public TabMailWorkflow() {
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
	public void onChange(MailEventConstant event) {
		if (event.equals(HasMailEvent.MAIL_CHANGED)) {
			Workflow.get().setTabMailSelected();
			workflowManager.findProcessInstancesByNode(Workflow.get().getUuid());
		} else if (event.equals(HasMailEvent.TAB_CHANGED)) {
			if (TabMailComunicator.isWidgetExtensionVisible(this)) {
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
	public void set(GWTMail mail) {
	}

	@Override
	public void setVisibleButtons(boolean visible) {
	}
}