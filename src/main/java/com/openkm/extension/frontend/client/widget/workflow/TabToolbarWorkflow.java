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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;
import com.openkm.frontend.client.util.Util;

/**
 * TabToolbarWorkflow
 *
 * @author jllort
 *
 */
public class TabToolbarWorkflow extends Composite {
	public static final int MODE_WORKFLOW_LIST = 0;
	public static final int MODE_WORKFLOW_DETAIL = 1;
	public static final int MODE_WORKFLOW_GRAPH = 2;

	private HorizontalPanel hPanel;
	private HorizontalPanel navigator;
	private Button home;
	private HTML showList;
	private HTML showDetail;
	private HTML showGraph;
	private int mode = -1;

	/**
	 * TabToolbarWorkflow
	 */
	public TabToolbarWorkflow(final WorkflowController controller) {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		hPanel = new HorizontalPanel();

		// Left Space
		HTML space = Util.hSpace("5px");
		hPanel.add(space);
		hPanel.setCellWidth(space, "5px");

		// Navigator
		navigator = new HorizontalPanel();

		// Create
		home = new Button(GeneralComunicator.i18nExtension("button.home"));
		home.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.goHome();
			}
		});
		home.setStyleName("okm-HomeButton");
		navigator.add(home);
		navigator.add(new HTML("&nbsp;"));
		navigator.setCellVerticalAlignment(home, HasAlignment.ALIGN_MIDDLE);

		// adding navigator
		buttonsPanel.add(navigator);
		buttonsPanel.setCellVerticalAlignment(navigator, HasAlignment.ALIGN_MIDDLE);

		// Vertical line
		HorizontalPanel verticalLine = new HorizontalPanel();
		HTML vertical = new HTML("&nbsp;");
		vertical.setHeight("24px");
		vertical.setStyleName("okm-Border-Right");
		verticalLine.add(vertical);
		navigator.add(verticalLine);

		// Action
		HorizontalPanel actions = new HorizontalPanel();
		HTML space2 = Util.hSpace("5px");
		actions.add(space2);
		actions.setCellWidth(space2, "5px");
		showList = new HTML(GeneralComunicator.i18nExtension("workflow.show.list").toUpperCase());
		showDetail = new HTML(GeneralComunicator.i18nExtension("workflow.show.detail").toUpperCase());
		showGraph = new HTML(GeneralComunicator.i18nExtension("workflow.show.graphs").toUpperCase());
		showList.setVisible(false);
		showDetail.setVisible(false);
		showGraph.setVisible(false);
		actions.add(showList);
		actions.add(showDetail);
		actions.add(showGraph);
		buttonsPanel.add(actions);
		buttonsPanel.setCellVerticalAlignment(actions, HasAlignment.ALIGN_MIDDLE);

		// Vertical line
		HorizontalPanel verticalLine2 = new HorizontalPanel();
		HTML vertical2 = new HTML("&nbsp;");
		vertical2.setHeight("24px");
		vertical2.setStyleName("okm-Border-Right");
		verticalLine2.add(vertical2);
		buttonsPanel.add(verticalLine2);

		// Adding buttons panel
		hPanel.add(buttonsPanel);

		hPanel.setCellHorizontalAlignment(buttonsPanel, HasAlignment.ALIGN_LEFT);
		hPanel.setCellVerticalAlignment(buttonsPanel, HasAlignment.ALIGN_MIDDLE);

		hPanel.setStyleName("okm-Mail");
		hPanel.addStyleName("okm-Border-Bottom");

		// First time must be in topic mode
		switchViewMode(MODE_WORKFLOW_LIST);

		initWidget(hPanel);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		showList.setHTML(GeneralComunicator.i18nExtension("workflow.show.list").toUpperCase());
		showDetail.setHTML(GeneralComunicator.i18nExtension("workflow.show.detail").toUpperCase());
		showGraph.setHTML(GeneralComunicator.i18nExtension("workflow.show.graphs").toUpperCase());
		home.setHTML(GeneralComunicator.i18nExtension("button.home"));
	}

	/**
	 * switchViewMode
	 *
	 * @param mode
	 */
	public void switchViewMode(int mode) {
		this.mode = mode;
		switch (mode) {
			case MODE_WORKFLOW_LIST:
				navigator.setVisible(false);
				showList.setVisible(true);
				showDetail.setVisible(false);
				showGraph.setVisible(false);
				break;

			case MODE_WORKFLOW_DETAIL:
				navigator.setVisible(true);
				showList.setVisible(false);
				showDetail.setVisible(true);
				showGraph.setVisible(false);
				break;

			case MODE_WORKFLOW_GRAPH:
				navigator.setVisible(true);
				showList.setVisible(false);
				showDetail.setVisible(false);
				showGraph.setVisible(true);
				break;
		}
	}

	/**
	 * getMode
	 *
	 * @return
	 */
	public int getMode() {
		return mode;
	}
}