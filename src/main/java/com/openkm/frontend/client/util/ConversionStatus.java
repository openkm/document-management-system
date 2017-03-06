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

package com.openkm.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTConverterStatus;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;

/**
 * ConversionStatus
 *
 * @author jllort
 *
 */
public class ConversionStatus {
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);
	private static final int REFRESH_STATUS_DELAY = 500;

	/**
	 * getStatus
	 */
	public void getStatus() {
		generalService.getConversionStatus(new AsyncCallback<GWTConverterStatus>() {
			@Override
			public void onSuccess(GWTConverterStatus result) {
				if (result.getError() != null) {
					Main.get().showError("Conversion", new Throwable(result.getError()));
					Main.get().mainPanel.bottomPanel.setStatus("");
				} else {
					if (!result.isConversionFinish()) {
						switch (result.getStatus()) {
							case GWTConverterStatus.STATUS_LOADING:
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("status.converter.loading"));
								break;
							case GWTConverterStatus.STATUS_CONVERTING_TO_PDF:
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("status.converter.topdf"));
								break;
							case GWTConverterStatus.STATUS_CONVERTING_TO_PDF_FINISHED:
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("status.converter.topdf.finished"));
								break;
							case GWTConverterStatus.STATUS_CONVERTING_TO_SWF:
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("status.converter.toswf"));
								break;
							case GWTConverterStatus.STATUS_CONVERTING_TO_SWF_FINISHED:
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("status.converter.toswf.finished"));
								break;
							case GWTConverterStatus.STATUS_SENDING_FILE:
								Main.get().mainPanel.bottomPanel.setStatus(Main.i18n("status.converter.sending.file"));
								break;
						}
						refreshStatus();
					} else {
						Main.get().mainPanel.bottomPanel.setStatus("");
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Not show any error here
				refreshStatus();
			}
		});
	}

	/**
	 * refreshStatus
	 */
	private void refreshStatus() {
		Timer refreshStatus = new Timer() {
			@Override
			public void run() {
				getStatus();
			}
		};

		refreshStatus.schedule(REFRESH_STATUS_DELAY);
	}
}