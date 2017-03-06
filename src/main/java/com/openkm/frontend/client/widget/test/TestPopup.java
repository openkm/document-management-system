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

package com.openkm.frontend.client.widget.test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.service.OKMTestService;
import com.openkm.frontend.client.service.OKMTestServiceAsync;
import com.openkm.frontend.client.util.Util;

import java.util.Date;
import java.util.List;

/**
 * Test Popup
 *
 * @author jllort
 *
 */
public class TestPopup extends DialogBox {
	private final OKMTestServiceAsync testService = (OKMTestServiceAsync) GWT.create(OKMTestService.class);

	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HorizontalPanel hPanel2;
	private HorizontalPanel hPanel3;
	private ScrollPanel scroll;
	private FlexTable table;
	private Button clean;
	private Button run;
	private Button runTiemout;
	private Button close;
	private Button getUserAgent;
	private TextBox sizeTest;
	private TextBox cyclesTest;
	private TextBox numThreads;
	private ListBox type;

	private String runnningTest = "";
	private int selectedTest = 0;
	private int actualCycle = 0;
	private int maxCycle = 0;
	private int textSize = 0;

	private TextBox timeoutSeconds;

	/**
	 * TestPopup
	 */
	public TestPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText("GWT Testing");
		vPanel = new VerticalPanel();

		// Controller
		hPanel = new HorizontalPanel();
		hPanel.add(new HTML("&nbsp;Type:"));
		type = new ListBox();
		type.addItem("String", "String");
		type.addItem("GWTFolder", "GWTFolder");
		type.addItem("GWTDocument", "GWTDocument");
		type.setStyleName("okm-Input");
		hPanel.add(type);
		hPanel.add(new HTML("&nbsp;Elements:"));
		sizeTest = new TextBox();
		sizeTest.setSize("60px", "20px");
		sizeTest.setText("2000");
		sizeTest.setStyleName("okm-Input");
		hPanel.add(sizeTest);
		hPanel.add(new HTML("&nbsp;Cycles:"));
		cyclesTest = new TextBox();
		cyclesTest.setText("100");
		cyclesTest.setSize("60px", "20px");
		cyclesTest.setStyleName("okm-Input");
		hPanel.add(cyclesTest);
		hPanel.add(new HTML("&nbsp;Threads:"));
		numThreads = new TextBox();
		numThreads.setText("1");
		numThreads.setSize("60px", "20px");
		numThreads.setStyleName("okm-Input");
		hPanel.add(numThreads);
		hPanel.add(new HTML("&nbsp;"));
		clean = new Button("Clean");
		clean.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				table.removeAllRows();
			}
		});
		clean.setStyleName("okm-Input");
		hPanel.add(clean);
		hPanel.add(new HTML("&nbsp;"));
		run = new Button("run");
		run.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		run.setStyleName("okm-Input");
		hPanel.add(run);
		hPanel.add(new HTML("&nbsp;"));

		// RPC timeout test
		hPanel2 = new HorizontalPanel();
		hPanel2.add(new HTML("&nbsp;Timeout seconds:"));
		timeoutSeconds = new TextBox();
		timeoutSeconds.setValue("60");
		timeoutSeconds.setStyleName("okm-Input");
		hPanel2.add(timeoutSeconds);
		hPanel2.add(new HTML("&nbsp;"));
		runTiemout = new Button("run");
		runTiemout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				runtimeout();
			}
		});
		runTiemout.setStyleName("okm-Input");
		hPanel2.add(runTiemout);

		// Get User
		hPanel3 = new HorizontalPanel();
		getUserAgent = new Button("Get user agent");
		getUserAgent.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				log("Agent: ", Util.getUserAgent());
			}
		});
		getUserAgent.setStyleName("okm-YesButton");
		hPanel3.add(getUserAgent);

		// Log
		table = new FlexTable();
		scroll = new ScrollPanel(table);
		scroll.setSize("600px", "450px");
		table.setWidth("100%");
		table.setCellPadding(5);
		table.setCellSpacing(0);

		// Close
		close = new Button("close");
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		close.setStyleName("okm-Input");

		vPanel.add(hPanel);
		vPanel.add(hPanel2);
		vPanel.add(hPanel3);
		vPanel.add(scroll);
		vPanel.add(close);
		vPanel.setCellHeight(hPanel, "30px");
		vPanel.setCellHeight(scroll, "450px");
		vPanel.setCellHeight(close, "20px");
		vPanel.setCellHorizontalAlignment(close, HasAlignment.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}

	/**
	 * run
	 */
	public void run() {
		runnningTest = "Test " + type.getValue(type.getSelectedIndex()) + " > ";
		selectedTest = type.getSelectedIndex();
		actualCycle = 0;
		maxCycle = Integer.parseInt(cyclesTest.getText());
		textSize = Integer.parseInt(sizeTest.getText());
		log(runnningTest, "Starting");
		int thread = Integer.parseInt(numThreads.getText());
		int count = 0;

		while (count < thread) {
			controller();
			count++;
		}
	}

	/**
	 * runtimeout
	 */
	public void runtimeout() {
		int delay = Integer.parseInt(timeoutSeconds.getValue());
		runnningTest = "Timeout RPC Test >";
		log(runnningTest, "started with delay=" + delay + " seconds");
		testService.RPCTimeout(delay, new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				log(runnningTest, "finished");
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("RPCTimeout", caught);
			}
		});
	}

	/**
	 * controller
	 */
	private void controller() {
		if (actualCycle < maxCycle) {
			switch (selectedTest) {
				case 0:
					stringTest(actualCycle);
					actualCycle++;
					break;
				case 1:
					folderTest(actualCycle);
					actualCycle++;
					break;
				case 2:
					documentTest(actualCycle);
					actualCycle++;
					break;
			}
		} else {
			log(runnningTest, "Finished");
		}
	}

	/**
	 * stringTest
	 */
	private void stringTest(final int cycle) {
		log(runnningTest, "Calling RPC: " + cycle);
		testService.StringTest(textSize, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				log(runnningTest, "Finished RPC: " + cycle + ", Result length: " + result.length());
				controller();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("StringTest", caught);
			}
		});
	}

	/**
	 * folderTest
	 */
	private void folderTest(final int cycle) {
		log(runnningTest, "Calling RPC: " + cycle);
		testService.folderText(textSize, new AsyncCallback<List<GWTFolder>>() {

			@Override
			public void onSuccess(List<GWTFolder> result) {
				log(runnningTest, "Finished RPC: " + cycle + ", Result size: " + result.size());
				controller();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("folderText", caught);
			}
		});
	}

	/**
	 * documentTest
	 */
	private void documentTest(final int cycle) {
		log(runnningTest, "Calling RPC: " + cycle);
		testService.documentText(textSize, new AsyncCallback<List<GWTDocument>>() {
			@Override
			public void onSuccess(List<GWTDocument> result) {
				log(runnningTest, "Finished RPC: " + cycle + ", Result size: " + result.size());
				controller();
			}

			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("folderText", caught);
			}
		});
	}

	/**
	 * @param value
	 * @param value2
	 */
	private void log(String value, String value2) {
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		int row = table.getRowCount();
		table.setHTML(row, 0, dtf.format(new Date()) + " " + value);
		table.setHTML(row, 1, value2);
		table.getCellFormatter().setHeight(row, 0, "20px");
		table.getCellFormatter().setWidth(row, 0, "250px");
	}
}