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

package com.openkm.extension.frontend.client.widget.wiki;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.openkm.extension.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.extension.comunicator.GeneralComunicator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * WikiToolBarEditor
 *
 * @author jllort
 *
 */
public class WikiToolBarEditor extends Composite {

	public static final int SMILES_TABLE_WIDTH = 160;

	private HorizontalPanel hPanel;
	private PushButton b;
	private PushButton i;
	private PushButton u;
	private PushButton p;
	private PushButton quote;
	private PushButton img;
	private PushButton url;
	private PushButton email;
	private PushButton h1;
	private PushButton h2;
	private PushButton h3;
	private PushButton h4;
	private PushButton h5;
	private PushButton h6;
	private PushButton justifyLeft;
	private PushButton justifyCenter;
	private PushButton justify;
	private PushButton justifyRight;
	private ListBox size;
	private PushButton fontColor;
	private PushButton addDocument;
	private PushButton addImage;
	private PushButton addFolder;
	private PushButton addNewWikiPage;
	private PushButton addWikiPage;
	private TextArea textArea;
	private FlexTable tableColor;
	private FlexTable tableSmiles;
	private NewWikiPagePopup newWikiPagePopup;

	/**
	 * ForumToolBarEditor
	 */
	public WikiToolBarEditor(final TextArea textArea) {
		this.textArea = textArea;
		hPanel = new HorizontalPanel();

		newWikiPagePopup = new NewWikiPagePopup();
		newWikiPagePopup.setWidth("200px");
		newWikiPagePopup.setHeight("50px");
		newWikiPagePopup.setStyleName("okm-Popup");
		newWikiPagePopup.addStyleName("okm-DisableSelect");

		// Buttons
		b = new PushButton(new Image(OKMBundleResources.INSTANCE.bold()));
		b.setTitle(GeneralComunicator.i18nExtension("forum.edit.bold"));
		b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[b]", "[/b]");
			}
		});
		i = new PushButton(new Image(OKMBundleResources.INSTANCE.italic()));
		i.setTitle(GeneralComunicator.i18nExtension("forum.edit.italic"));
		i.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[i]", "[/i]");
			}
		});
		u = new PushButton(new Image(OKMBundleResources.INSTANCE.underline()));
		u.setTitle(GeneralComunicator.i18nExtension("forum.edit.underline"));
		u.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[u]", "[/u]");
			}
		});
		p = new PushButton();
		p.setHTML("p");
		p.setTitle(GeneralComunicator.i18nExtension("forum.edit.paragraph"));
		p.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[p]", "[/p]");
			}
		});
		quote = new PushButton();
		quote.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.quote"));
		quote.setTitle(GeneralComunicator.i18nExtension("forum.edit.quote"));
		quote.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[quote]", "[/quote]");
			}
		});
		img = new PushButton();
		img.setHTML("Img");
		img.setTitle(GeneralComunicator.i18nExtension("forum.edit.url"));
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[img]", "[/img]");
			}
		});
		url = new PushButton();
		url.setHTML("URL");
		url.setTitle(GeneralComunicator.i18nExtension("forum.edit.url"));
		url.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[url]", "[/url]");
			}
		});
		email = new PushButton();
		email.setHTML("Email");
		email.setTitle(GeneralComunicator.i18nExtension("forum.edit.email"));
		email.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[email]", "[/email]");
			}
		});
		h1 = new PushButton();
		h1.setHTML("h1");
		h1.setTitle("Header1 text:[h1]text[/h1]");
		h1.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[h1]", "[/h1]");
			}
		});
		h2 = new PushButton();
		h2.setHTML("h2");
		h2.setTitle("Header2 text:[h2]text[/h2]");
		h2.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[h2]", "[/h2]");
			}
		});
		h3 = new PushButton();
		h3.setHTML("h3");
		h3.setTitle("Header3 text:[h3]text[/h3]");
		h3.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[h3]", "[/h3]");
			}
		});
		h4 = new PushButton();
		h4.setHTML("h4");
		h4.setTitle("Header4 text:[h4]text[/h4]");
		h4.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[h4]", "[/h4]");
			}
		});
		h5 = new PushButton();
		h5.setHTML("h5");
		h5.setTitle("Header5 text:[h5]text[/h5]");
		h5.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[h5]", "[/h5]");
			}
		});
		h6 = new PushButton();
		h6.setHTML("h6");
		h6.setTitle("Header6 text:[h6]text[/h6]");
		h6.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[h6]", "[/h6]");
			}
		});
		justifyLeft = new PushButton(new Image(OKMBundleResources.INSTANCE.justifyLeft()));
		justifyLeft.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify.left"));
		justifyLeft.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[align=left]", "[/align]");
			}
		});
		justifyCenter = new PushButton(new Image(OKMBundleResources.INSTANCE.justifyCenter()));
		justifyCenter.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify.center"));
		justifyCenter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[center]", "[/center]");
			}
		});
		justify = new PushButton(new Image(OKMBundleResources.INSTANCE.justify()));
		justify.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify"));
		justify.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[align=justify]", "[/align]");
			}
		});
		justifyRight = new PushButton(new Image(OKMBundleResources.INSTANCE.justifyRight()));
		justifyRight.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify.right"));
		justifyRight.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag("[align=right]", "[/align]");
			}
		});
		size = new ListBox();
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.tiny"), "5");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.small"), "8");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.normal"), "10");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.large"), "14");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.huge"), "25");
		size.setSelectedIndex(2); // By default Normal is selected
		size.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// In case selected is nomal must do nothing
				if (size.getSelectedIndex() != 2) {
					addTag("[size=" + size.getValue(size.getSelectedIndex()) + "]", "[/size]");
					size.setSelectedIndex(2);
				}
			}
		});
		size.setHeight("23px");
		size.setStyleName("okm-Input");
		fontColor = new PushButton();
		fontColor.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.show.color"));
		fontColor.setTitle(GeneralComunicator.i18nExtension("forum.edit.color"));
		fontColor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (tableColor.isVisible()) {
					fontColor.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.show.color"));
					tableColor.setVisible(false);
				} else {
					fontColor.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.hide.color"));
					tableColor.setVisible(true);
				}
			}
		});

		addDocument = new PushButton(new Image(OKMBundleResources.INSTANCE.findDocument()));
		addDocument.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.document"));
		addDocument.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Wiki.get().findDocumentSelectPopup.showFindDocument();
			}
		});

		addImage = new PushButton(new Image(OKMBundleResources.INSTANCE.findImage()));
		addImage.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.image"));
		addImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Wiki.get().findDocumentSelectPopup.showFindImage();
			}
		});

		addFolder = new PushButton(new Image(OKMBundleResources.INSTANCE.findFolder()));
		addFolder.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.folder"));
		addFolder.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Wiki.get().findFolderSelectPopup.show();
			}
		});

		addNewWikiPage = new PushButton(new Image(OKMBundleResources.INSTANCE.wikiAdd()));
		addNewWikiPage.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.new.wiki"));
		addNewWikiPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				newWikiPagePopup.reset();
				newWikiPagePopup.center();
				newWikiPagePopup.setFocus();
			}
		});

		addWikiPage = new PushButton(new Image(OKMBundleResources.INSTANCE.wikiLink()));
		addWikiPage.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.wiki"));
		addWikiPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Wiki.get().findWikiSelectPopup.show();
			}
		});

		hPanel.add(b);
		HTML space = new HTML();
		hPanel.add(space);
		hPanel.add(i);
		HTML space2 = new HTML();
		hPanel.add(space2);
		hPanel.add(u);
		HTML space13 = new HTML();
		hPanel.add(space13);
		hPanel.add(p);
		HTML space3 = new HTML();
		hPanel.add(space3);
		hPanel.add(quote);
		HTML space4 = new HTML();
		hPanel.add(space4);
		hPanel.add(img);
		HTML space5 = new HTML();
		hPanel.add(space5);
		hPanel.add(url);
		HTML space12 = new HTML();
		hPanel.add(space12);
		hPanel.add(email);
		HTML space6 = new HTML();
		hPanel.add(space6);
		hPanel.add(h1);
		HTML space7 = new HTML();
		hPanel.add(space7);
		hPanel.add(h2);
		HTML space8 = new HTML();
		hPanel.add(space8);
		hPanel.add(h3);
		HTML space9 = new HTML();
		hPanel.add(space9);
		hPanel.add(h4);
		HTML space10 = new HTML();
		hPanel.add(space10);
		hPanel.add(h5);
		HTML space11 = new HTML();
		hPanel.add(space11);
		hPanel.add(h6);
		HTML space14 = new HTML();
		hPanel.add(space14);
		hPanel.add(justifyLeft);
		HTML space15 = new HTML();
		hPanel.add(space15);
		hPanel.add(justifyCenter);
		HTML space16 = new HTML();
		hPanel.add(space16);
		hPanel.add(justify);
		HTML space17 = new HTML();
		hPanel.add(space17);
		hPanel.add(justifyRight);
		HTML space18 = new HTML();
		hPanel.add(space18);
		hPanel.add(size);
		hPanel.setCellVerticalAlignment(size, HasAlignment.ALIGN_BOTTOM);
		HTML space19 = new HTML();
		hPanel.add(space19);
		hPanel.add(fontColor);
		HTML space20 = new HTML();
		hPanel.add(space20);
		hPanel.add(addDocument);
		HTML space21 = new HTML();
		hPanel.add(space21);
		hPanel.add(addImage);
		HTML space22 = new HTML();
		hPanel.add(space22);
		hPanel.add(addFolder);
		HTML space23 = new HTML();
		hPanel.add(space23);
		hPanel.add(addNewWikiPage);
		HTML space24 = new HTML();
		hPanel.add(space24);
		hPanel.add(addWikiPage);

		hPanel.setCellWidth(space, "2px");
		hPanel.setCellWidth(space2, "2px");
		hPanel.setCellWidth(space3, "2px");
		hPanel.setCellWidth(space4, "2px");
		hPanel.setCellWidth(space5, "2px");
		hPanel.setCellWidth(space6, "2px");
		hPanel.setCellWidth(space7, "2px");
		hPanel.setCellWidth(space8, "2px");
		hPanel.setCellWidth(space9, "2px");
		hPanel.setCellWidth(space10, "2px");
		hPanel.setCellWidth(space11, "2px");
		hPanel.setCellWidth(space12, "2px");
		hPanel.setCellWidth(space13, "2px");
		hPanel.setCellWidth(space14, "2px");
		hPanel.setCellWidth(space15, "2px");
		hPanel.setCellWidth(space16, "2px");
		hPanel.setCellWidth(space17, "2px");
		hPanel.setCellWidth(space18, "2px");
		hPanel.setCellWidth(space19, "2px");
		hPanel.setCellWidth(space20, "2px");
		hPanel.setCellWidth(space21, "2px");
		hPanel.setCellWidth(space22, "2px");
		hPanel.setCellWidth(space23, "2px");
		hPanel.setCellWidth(space24, "2px");

		initWidget(hPanel);
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		b.setTitle(GeneralComunicator.i18nExtension("forum.edit.bold"));
		i.setTitle(GeneralComunicator.i18nExtension("forum.edit.italic"));
		u.setTitle(GeneralComunicator.i18nExtension("forum.edit.underline"));
		p.setTitle(GeneralComunicator.i18nExtension("forum.edit.paragraph"));
		quote.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.quote"));
		quote.setTitle(GeneralComunicator.i18nExtension("forum.edit.quote"));
		img.setTitle(GeneralComunicator.i18nExtension("forum.edit.url"));
		url.setTitle(GeneralComunicator.i18nExtension("forum.edit.url"));
		email.setTitle(GeneralComunicator.i18nExtension("forum.edit.email"));
		justifyLeft.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify.left"));
		justifyCenter.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify.center"));
		justify.setTitle(GeneralComunicator.i18nExtension("forum.edit.justify"));
		size.clear();
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.tiny"), "5");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.small"), "8");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.normal"), "10");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.large"), "14");
		size.addItem(GeneralComunicator.i18nExtension("forum.edit.font.huge"), "25");
		size.setSelectedIndex(2); // By default Normal is selected
		fontColor.setTitle(GeneralComunicator.i18nExtension("forum.edit.color"));
		if (tableColor.isVisible()) {
			fontColor.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.show.color"));
		} else {
			fontColor.setHTML(GeneralComunicator.i18nExtension("forum.edit.button.hide.color"));
		}
		addDocument.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.document"));
		addImage.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.image"));
		addFolder.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.folder"));
		addNewWikiPage.setTitle(GeneralComunicator.i18nExtension("forum.edit.add.new.wiki"));
	}

	/**
	 * addDocumentTag
	 *
	 * @param uuid
	 * @param docName
	 */
	public void addDocumentTag(String uuid, String docName) {
		addTag("[doc=" + uuid + "]", docName + "[/doc]");
	}

	/**
	 * addImageTag
	 */
	public void addImageTag(String url, String params) {
		addTag("[okmimg=" + url + "]", params + "[/okmimg]");
	}

	/**
	 * addFolderTag
	 *
	 * @param uuid
	 * @param fldName
	 */
	public void addFolderTag(String uuid, String fldName) {
		addTag("[fld=" + uuid + "]", fldName + "[/fld]");
	}

	/**
	 * addWigiTag
	 *
	 * @param uuid
	 * @param fldName
	 */
	public void addWigiTag(String wikiTitle) {
		addTag("[wiki=" + wikiTitle + "]", wikiTitle + "[/wiki]");
	}

	/**
	 * addTag
	 *
	 * @param leftTag
	 * @param rightTag
	 */
	private void addTag(String leftTag, String rightTag) {
		int pos = textArea.getCursorPos();
		int length = textArea.getSelectionLength();
		if (length == 0) {
			if (pos == 0) {
				textArea.setText(leftTag + rightTag + textArea.getText());
			} else {
				textArea.setText(textArea.getText().substring(0, pos) + leftTag + rightTag + textArea.getText().substring(pos));
			}
		} else {
			textArea.setText(textArea.getText().substring(0, pos) + leftTag + textArea.getSelectedText() + rightTag + textArea.getText().substring(pos + length));
		}
	}

	/**
	 * getColorPanel
	 *
	 * @return
	 */
	public Widget getColorPanel() {
		String color[] = {"00", "40", "80", "bf", "ff"};
		tableColor = new FlexTable();
		tableColor.setCellPadding(0);
		tableColor.setCellSpacing(1);
		for (int i = 0; i < color.length; i++) {
			int col = 0;
			for (int x = 0; x < color.length; x++) {
				for (int y = 0; y < color.length; y++) {
					HTML square = new HTML("");
					square.setPixelSize(15, 10);
					square.setStyleName("okm-Hyperlink");
					final String hexColor = "#" + color[i] + color[x] + color[y];
					square.setTitle(hexColor);
					square.getElement().getStyle().setProperty("backgroundColor", hexColor);
					tableColor.setWidget(i, col, square);
					square.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							addTag("[color=" + hexColor + "]", "[/color]");
						}
					});
					col++;
				}
			}
		}
		tableColor.setHTML(5, 0, "");
		tableColor.getCellFormatter().setHeight(5, 0, "5px");
		tableColor.getFlexCellFormatter().setRowSpan(5, 0, 25);
		tableColor.setVisible(false);
		return tableColor;
	}

	/**
	 * getSmilesPanel
	 *
	 * @return
	 */
	public Widget getSmilesPanel() {
		tableSmiles = new FlexTable();
		tableSmiles.setCellPadding(2);
		tableSmiles.setCellSpacing(0);
		Image bigGrin = new Image(OKMBundleResources.INSTANCE.smileBigGrin());
		bigGrin.setTitle("Very happy");
		bigGrin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :D ", "");
			}
		});
		bigGrin.setStyleName("okm-Hyperlink");
		Image smile = new Image(OKMBundleResources.INSTANCE.smileSmile());
		smile.setTitle("Smile");
		smile.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :) ", "");
			}
		});
		smile.setStyleName("okm-Hyperlink");
		Image sad = new Image(OKMBundleResources.INSTANCE.smileSad());
		sad.setTitle("Sad");
		sad.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :( ", "");
			}
		});
		sad.setStyleName("okm-Hyperlink");
		Image surprised = new Image(OKMBundleResources.INSTANCE.smileSurprised());
		surprised.setTitle("Surprised");
		surprised.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :o ", "");
			}
		});
		surprised.setStyleName("okm-Hyperlink");
		Image eek = new Image(OKMBundleResources.INSTANCE.smileEek());
		eek.setTitle("Shocked");
		eek.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :shock: ", "");
			}
		});
		eek.setStyleName("okm-Hyperlink");
		Image confused = new Image(OKMBundleResources.INSTANCE.smileConfused());
		confused.setTitle("Confused");
		confused.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :? ", "");
			}
		});
		confused.setStyleName("okm-Hyperlink");
		Image cool = new Image(OKMBundleResources.INSTANCE.smileCool());
		cool.setTitle("Cool");
		cool.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" 8) ", "");
			}
		});
		cool.setStyleName("okm-Hyperlink");
		Image lol = new Image(OKMBundleResources.INSTANCE.smileLol());
		lol.setTitle("Laughing");
		lol.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :lol: ", "");
			}
		});
		lol.setStyleName("okm-Hyperlink");
		Image mad = new Image(OKMBundleResources.INSTANCE.smileMad());
		mad.setTitle("Mad");
		mad.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :x ", "");
			}
		});
		mad.setStyleName("okm-Hyperlink");
		Image razz = new Image(OKMBundleResources.INSTANCE.smileRazz());
		razz.setTitle("Razz");
		razz.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :P ", "");
			}
		});
		razz.setStyleName("okm-Hyperlink");
		Image redface = new Image(OKMBundleResources.INSTANCE.smileRedface());
		redface.setTitle("Embarassed");
		redface.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :redface: ", "");
			}
		});
		redface.setStyleName("okm-Hyperlink");
		Image cry = new Image(OKMBundleResources.INSTANCE.smileCry());
		cry.setTitle("Crying or very sad");
		cry.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :cry: ", "");
			}
		});
		cry.setStyleName("okm-Hyperlink");
		Image evil = new Image(OKMBundleResources.INSTANCE.smileEvil());
		evil.setTitle("Evil or very mad");
		evil.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :evil: ", "");
			}
		});
		evil.setStyleName("okm-Hyperlink");
		Image twisted = new Image(OKMBundleResources.INSTANCE.smileTwisted());
		twisted.setTitle("Twisted evil");
		twisted.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :twisted: ", "");
			}
		});
		twisted.setStyleName("okm-Hyperlink");
		Image rolleyes = new Image(OKMBundleResources.INSTANCE.smileRolleyes());
		rolleyes.setTitle("Rolling eyes");
		rolleyes.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :roll: ", "");
			}
		});
		rolleyes.setStyleName("okm-Hyperlink");
		Image wink = new Image(OKMBundleResources.INSTANCE.smileWink());
		wink.setTitle("Wink");
		wink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :wink: ", "");
			}
		});
		wink.setStyleName("okm-Hyperlink");
		Image exclaim = new Image(OKMBundleResources.INSTANCE.smileExclaim());
		exclaim.setTitle("Exclamation");
		exclaim.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :!: ", "");
			}
		});
		exclaim.setStyleName("okm-Hyperlink");
		Image question = new Image(OKMBundleResources.INSTANCE.smileQuestion());
		question.setTitle("Question");
		question.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :question: ", "");
			}
		});
		question.setStyleName("okm-Hyperlink");
		Image idea = new Image(OKMBundleResources.INSTANCE.smileIdea());
		idea.setTitle("Idea");
		idea.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :idea: ", "");
			}
		});
		idea.setStyleName("okm-Hyperlink");
		Image arrow = new Image(OKMBundleResources.INSTANCE.smileArrow());
		arrow.setTitle("Arrow");
		arrow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :arrow: ", "");
			}
		});
		arrow.setStyleName("okm-Hyperlink");
		Image neutral = new Image(OKMBundleResources.INSTANCE.smileNeutral());
		neutral.setTitle("Neutral");
		neutral.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :| ", "");
			}
		});
		neutral.setStyleName("okm-Hyperlink");
		Image mrgreen = new Image(OKMBundleResources.INSTANCE.smileMrgreen());
		mrgreen.setTitle("Mr. Green");
		mrgreen.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addTag(" :mrgreen: ", "");
			}
		});
		mrgreen.setStyleName("okm-Hyperlink");

		tableSmiles.setWidget(0, 0, bigGrin);
		tableSmiles.setWidget(0, 1, smile);
		tableSmiles.setWidget(0, 2, sad);
		tableSmiles.setWidget(0, 3, surprised);
		tableSmiles.setWidget(0, 4, eek);
		tableSmiles.setWidget(0, 5, confused);
		tableSmiles.setWidget(0, 6, cool);
		tableSmiles.setWidget(0, 7, lol);
		tableSmiles.setWidget(1, 0, mad);
		tableSmiles.setWidget(1, 1, razz);
		tableSmiles.setWidget(1, 2, redface);
		tableSmiles.setWidget(1, 3, cry);
		tableSmiles.setWidget(1, 4, evil);
		tableSmiles.setWidget(1, 5, twisted);
		tableSmiles.setWidget(1, 6, rolleyes);
		tableSmiles.setWidget(1, 7, wink);
		tableSmiles.setWidget(2, 0, exclaim);
		tableSmiles.setWidget(2, 1, question);
		tableSmiles.setWidget(2, 2, idea);
		tableSmiles.setWidget(2, 3, arrow);
		tableSmiles.setWidget(2, 4, neutral);
		tableSmiles.setWidget(2, 5, mrgreen);

		return tableSmiles;
	}

	/**
	 * bbcode
	 *
	 * @param text
	 * @return
	 */
	public static String bbcode(String text) {
		String html = text;
		Map<String, String> bbMap = new LinkedHashMap<String, String>();
		bbMap.put("(\r\n|\r|\n|\n\r)", "<br/>");
		bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");
		bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style=\"font-style:italic;\">$1</span>");
		bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style=\"text-decoration:underline;\">$1</span>");
		bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
		bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
		bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
		bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
		bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
		bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
		bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");
		bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
		bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style=\"text-indent:$1px;line-height:$2%;\">$3</p>");
		bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align=\"center\">$1</div>");
		bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align=\"$1\">$2</div>");
		bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style=\"color:$1;\">$2</span>");
		bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style=\"font-size:$1;\">$2</span>");
		bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src=\"$1\" />");
		bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width=\"$1\" height=\"$2\" src=\"$3\" />");
		bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href=\"mailto:$1\">$1</a>");
		bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href=\"mailto:$1\">$2</a>");
		bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href=\"$1\">$1</a>");
		bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href=\"$1\">$2</a>");
		//bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");
		//bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");
		bbMap.put(":D", "<img width=\"15\" height=\"15\" alt=\":D\" title=\"Very happy\" src=\"img/icon/smilies/icon_biggrin.gif\" />");
		bbMap.put(":\\)", "<img width=\"15\" height=\"15\" alt=\":)\" title=\"Smile\" src=\"img/icon/smilies/icon_smile.gif\" />");
		bbMap.put(":\\(", "<img width=\"15\" height=\"15\" alt=\":(\" title=\"Sad\" src=\"img/icon/smilies/icon_sad.gif\" />");
		bbMap.put(":o", "<img width=\"15\" height=\"15\" alt=\":o\" title=\"Surprised\" src=\"img/icon/smilies/icon_surprised.gif\" />");
		bbMap.put(":shock:", "<img width=\"15\" height=\"15\" alt=\":shock:\" title=\"Shocked\" src=\"img/icon/smilies/icon_eek.gif\" />");
		bbMap.put(":\\?", "<img width=\"15\" height=\"15\" alt=\":?\" title=\"Confused\" src=\"img/icon/smilies/icon_confused.gif\" />");
		bbMap.put("8\\)", "<img width=\"15\" height=\"15\" alt=\"8)\" title=\"Cool\" src=\"img/icon/smilies/icon_cool.gif\" />");
		bbMap.put(":lol:", "<img width=\"15\" height=\"15\" alt=\":lol:\" title=\"Laughing\" src=\"img/icon/smilies/icon_lol.gif\" />");
		bbMap.put(":x", "<img width=\"15\" height=\"15\" alt=\":x\" title=\"Mad\" src=\"img/icon/smilies/icon_mad.gif\" />");
		bbMap.put(":P", "<img width=\"15\" height=\"15\" alt=\":P\" title=\"Razz\" src=\"img/icon/smilies/icon_razz.gif\" />");
		bbMap.put(":redface:", "<img width=\"15\" height=\"15\" alt=\":redface:\" title=\"Embarassed\" src=\"img/icon/smilies/icon_redface.gif\" />");
		bbMap.put(":cry:", "<img width=\"15\" height=\"15\" alt=\":cry:\" title=\"Crying or very sad\" src=\"img/icon/smilies/icon_cry.gif\" />");
		bbMap.put(":evil:", "<img width=\"15\" height=\"15\" alt=\":evil:\" title=\"Evil or very mad\" src=\"img/icon/smilies/icon_evil.gif\" />");
		bbMap.put(":twisted:", "<img width=\"15\" height=\"15\" alt=\":twisted:\" title=\"Twisted evil\" src=\"img/icon/smilies/icon_twisted.gif\" />");
		bbMap.put(":roll:", "<img width=\"15\" height=\"15\" alt=\":roll:\" title=\"Rolling eyes\" src=\"img/icon/smilies/icon_rolleyes.gif\" />");
		bbMap.put(":wink:", "<img width=\"15\" height=\"15\" alt=\":wink:\" title=\"Wink\" src=\"img/icon/smilies/icon_wink.gif\" />");
		bbMap.put(":!:", "<img width=\"15\" height=\"15\" alt=\":!:\" title=\"Exclamation\" src=\"img/icon/smilies/icon_exclaim.gif\" />");
		bbMap.put(":question:", "<img width=\"15\" height=\"15\" alt=\":question:\" title=\"Question\" src=\"img/icon/smilies/icon_question.gif\" />");
		bbMap.put(":idea:", "<img width=\"15\" height=\"15\" alt=\":idea:\" title=\"Idea\" src=\"img/icon/smilies/icon_idea.gif\" />");
		bbMap.put(":arrow:", "<img width=\"15\" height=\"15\" alt=\":arrow:\" title=\"Arrow\" src=\"img/icon/smilies/icon_arrow.gif\" />");
		bbMap.put(":\\|", "<img width=\"15\" height=\"15\" alt=\":|\" title=\"Neutral\" src=\"img/icon/smilies/icon_neutral.gif\" />");
		bbMap.put(":mrgreen:", "<img width=\"15\" height=\"15\" alt=\":mrgreen:\" title=\"Mr. Green\" src=\"img/icon/smilies/icon_mrgreen.gif\" />");
		bbMap.put("\\[doc=(.+?)\\](.+?)\\[/doc\\]", "<a href=\"#\" onclick=\"javascript:jsOpenPathByUuid('$1');\">$2</a>"); // Because it's uses function() must be the last to be replaced
		bbMap.put("\\[fld=(.+?)\\](.+?)\\[/fld\\]", "<a href=\"#\" onclick=\"javascript:jsOpenPathByUuid('$1');\">$2</a>"); // Because it's uses function() must be the last to be replaced
		bbMap.put("\\[mail=(.+?)\\](.+?)\\[/mail\\]", "<a href=\"#\" onclick=\"javascript:jsOpenPathByUuid('$1');\">$2</a>"); // Because it's uses function() must be the last to be replaced
		bbMap.put("\\[wiki=(.+?)\\](.+?)\\[/wiki\\]", "<a href=\"#\" onclick=\"javascript:openWikiPage('$1');\">$2</a>"); // Because it's uses function() must be the last to be replaced
		bbMap.put("\\[okmimg=(.+?)\\]\\[/okmimg\\]", "<img src=\"$1\"/>"); // Because it's uses function() must be the last to be replaced
		bbMap.put("\\[okmimg=(.+?)\\](.+?)\\[/okmimg\\]", "<img src=\"$1\" $2/>"); // Because it's uses function() must be the last to be replaced

		for (Map.Entry<String, String> entry : bbMap.entrySet()) {
			html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
		}

		return html;
	}
}