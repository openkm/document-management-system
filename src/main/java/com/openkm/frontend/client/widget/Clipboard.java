package com.openkm.frontend.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;

/**
 * Clipboard
 *
 * @author sochoa
 */
public class Clipboard extends Composite implements ClickHandler {
	private Image imgCopyDav;
	private String text;

	public Clipboard() {
		// Show clipboard icon
		imgCopyDav = new Image(OKMBundleResources.INSTANCE.clipboard());
		imgCopyDav.setStyleName("okm-Hyperlink");
		imgCopyDav.addClickHandler(this);

		// All composites must call initWidget() in their constructors.
		initWidget(imgCopyDav);
	}

	public Clipboard(String text) {
		this.text = text;

		// Show clipboard icon
		imgCopyDav = new Image(OKMBundleResources.INSTANCE.clipboardSmall());
		imgCopyDav.setStyleName("okm-Hyperlink");
		imgCopyDav.setAltText(text);
		imgCopyDav.setTitle(text);
		imgCopyDav.addClickHandler(this);

		// All composites must call initWidget() in their constructors.
		initWidget(imgCopyDav);
	}

	public void setText(String text) {
		this.text = text;
		imgCopyDav.setAltText(text);
		imgCopyDav.setTitle(text);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (text != null && !text.isEmpty()) {
			Util.copyToClipboard(text);
		} else {
			Util.consoleLog("URL is nul or empty");
		}
	}
}
