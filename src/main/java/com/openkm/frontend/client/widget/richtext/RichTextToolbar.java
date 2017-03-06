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

package com.openkm.frontend.client.widget.richtext;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.OKMBundleResources;

/**
 * @author jllort
 *
 */
public class RichTextToolbar extends Composite implements RichTextAction {
	//HTML Related (styles made by SPAN and DIV)
	private static final String HTML_STYLE_CLOSE_SPAN = "</span>";
	private static final String HTML_STYLE_CLOSE_DIV = "</div>";
	private static final String HTML_STYLE_OPEN_BOLD = "<span style=\"font-weight: bold;\">";
	private static final String HTML_STYLE_OPEN_ITALIC = "<span style=\"font-weight: italic;\">";
	private static final String HTML_STYLE_OPEN_UNDERLINE = "<span style=\"font-weight: underline;\">";
	private static final String HTML_STYLE_OPEN_LINETHROUGH = "<span style=\"font-weight: line-through;\">";
	private static final String HTML_STYLE_OPEN_ALIGNLEFT = "<div style=\"text-align: left;\">";
	private static final String HTML_STYLE_OPEN_ALIGNCENTER = "<div style=\"text-align: center;\">";
	private static final String HTML_STYLE_OPEN_ALIGNRIGHT = "<div style=\"text-align: right;\">";
	private static final String HTML_STYLE_OPEN_INDENTRIGHT = "<div style=\"margin-left: 40px;\">";

	//HTML Related (styles made by custom HTML-Tags)
	private static final String HTML_STYLE_OPEN_SUBSCRIPT = "<sub>";
	private static final String HTML_STYLE_CLOSE_SUBSCRIPT = "</sub>";
	private static final String HTML_STYLE_OPEN_SUPERSCRIPT = "<sup>";
	private static final String HTML_STYLE_CLOSE_SUPERSCRIPT = "</sup>";
	private static final String HTML_STYLE_OPEN_ORDERLIST = "<ol><li>";
	private static final String HTML_STYLE_CLOSE_ORDERLIST = "</ol></li>";
	private static final String HTML_STYLE_OPEN_UNORDERLIST = "<ul><li>";
	private static final String HTML_STYLE_CLOSE_UNORDERLIST = "</ul></li>";

	//HTML Related (styles without closing Tag)
	private static final String HTML_STYLE_HLINE = "<hr style=\"width: 100%; height: 2px;\">";

	/** Private Variables **/
	//The main (Vertical)-Panel and the two inner (Horizontal)-Panels
	private VerticalPanel outer;
	private HorizontalPanel topPanel;
	private HorizontalPanel bottomPanel;

	//The RichTextArea this Toolbar referes to and the Interfaces to access the RichTextArea
	private RichTextArea styleText;
	private Formatter styleTextFormatter;

	//We use an internal class of the ClickHandler and the KeyUpHandler to be private to others with these events
	private EventHandler evHandler;

	//The Buttons of the Menubar
	private ToggleButton bold;
	private ToggleButton italic;
	private ToggleButton underline;
	private ToggleButton stroke;
	private ToggleButton subscript;
	private ToggleButton superscript;
	private PushButton alignleft;
	private PushButton alignmiddle;
	private PushButton justify;
	private PushButton alignright;
	private PushButton orderlist;
	private PushButton unorderlist;
	private PushButton indentleft;
	private PushButton indentright;
	private PushButton generatelink;
	private PushButton breaklink;
	private PushButton insertline;
	private PushButton insertimage;
	private PushButton removeformatting;
	private ToggleButton texthtml;

	private ListBox fontList;
	private ListBox colorlist;

	RichTextPopup richTextPopup;

	/** Constructor of the Toolbar **/
	public RichTextToolbar(final RichTextArea richtext) {
		//Initialize the main-panel
		outer = new VerticalPanel();

		//Initialize the two inner panels
		topPanel = new HorizontalPanel();
		bottomPanel = new HorizontalPanel();
		topPanel.setStyleName("RichTextToolbar");
		bottomPanel.setStyleName("RichTextToolbar");

		//Save the reference to the RichText area we refer to and get the interfaces to the stylings

		styleText = richtext;
		styleTextFormatter = styleText.getFormatter();

		//Set some graphical options, so this toolbar looks how we like it.
		topPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		bottomPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);

		//Add the two inner panels to the main panel
		outer.add(topPanel);
		outer.add(bottomPanel);

		//Some graphical stuff to the main panel and the initialisation of the new widget
		outer.setStyleName("RichTextToolbar");
		initWidget(outer);

		//
		evHandler = new EventHandler();

		//Add KeyUp and Click-Handler to the RichText, so that we can actualize the toolbar if neccessary
		styleText.addKeyUpHandler(evHandler);
		styleText.addClickHandler(evHandler);

		// Changing styles
		IFrameElement e = IFrameElement.as(richtext.getElement());
		e.setSrc("iframe_richtext.html");
		e.setFrameBorder(0); // removing frame border

		richTextPopup = new RichTextPopup(this);
		richTextPopup.setWidth("300px");
		richTextPopup.setHeight("50px");
		richTextPopup.setStyleName("okm-Popup");

		//Now lets fill the new toolbar with life
		buildTools();
	}

	/** Click Handler of the Toolbar **/
	private class EventHandler implements ClickHandler, KeyUpHandler, ChangeHandler {
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(bold)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_BOLD, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleBold();
				}
			} else if (event.getSource().equals(italic)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ITALIC, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleItalic();
				}
			} else if (event.getSource().equals(underline)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_UNDERLINE, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleUnderline();
				}
			} else if (event.getSource().equals(stroke)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_LINETHROUGH, HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.toggleStrikethrough();
				}
			} else if (event.getSource().equals(subscript)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_SUBSCRIPT, HTML_STYLE_CLOSE_SUBSCRIPT);
				} else {
					styleTextFormatter.toggleSubscript();
				}
			} else if (event.getSource().equals(superscript)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_SUPERSCRIPT, HTML_STYLE_CLOSE_SUPERSCRIPT);
				} else {
					styleTextFormatter.toggleSuperscript();
				}
			} else if (event.getSource().equals(alignleft)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNLEFT, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.LEFT);
				}
			} else if (event.getSource().equals(alignmiddle)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNCENTER, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.CENTER);
				}
			} else if (event.getSource().equals(justify)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNCENTER, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.FULL);
				}
			} else if (event.getSource().equals(alignright)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ALIGNRIGHT, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.setJustification(RichTextArea.Justification.RIGHT);
				}
			} else if (event.getSource().equals(orderlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_ORDERLIST, HTML_STYLE_CLOSE_ORDERLIST);
				} else {
					styleTextFormatter.insertOrderedList();
				}
			} else if (event.getSource().equals(unorderlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_UNORDERLIST, HTML_STYLE_CLOSE_UNORDERLIST);
				} else {
					styleTextFormatter.insertUnorderedList();
				}
			} else if (event.getSource().equals(indentright)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_OPEN_INDENTRIGHT, HTML_STYLE_CLOSE_DIV);
				} else {
					styleTextFormatter.rightIndent();
				}
			} else if (event.getSource().equals(indentleft)) {
				if (isHTMLMode()) {
				} else {
					styleTextFormatter.leftIndent();
				}
			} else if (event.getSource().equals(generatelink)) {
				richTextPopup.setAction(RichTextPopup.ACTION_ENTER_URL);
				richTextPopup.show();
			} else if (event.getSource().equals(breaklink)) {
				if (isHTMLMode()) {
				} else {
					styleTextFormatter.removeLink();
				}
			} else if (event.getSource().equals(insertimage)) {
				richTextPopup.setAction(RichTextPopup.ACTION_ENTER_IMAGE_URL);
				richTextPopup.show();
			} else if (event.getSource().equals(insertline)) {
				if (isHTMLMode()) {
					changeHtmlStyle(HTML_STYLE_HLINE, "");
				} else {
					styleTextFormatter.insertHorizontalRule();
				}
			} else if (event.getSource().equals(removeformatting)) {
				if (isHTMLMode()) {
				} else {
					styleTextFormatter.removeFormat();
				}
			} else if (event.getSource().equals(texthtml)) {
				if (texthtml.isDown()) {
					styleText.setText(styleText.getHTML());
				} else {
					styleText.setHTML(styleText.getText());
				}
			} else if (event.getSource().equals(styleText)) {
				//Change invoked by the richtextArea
			}
			updateStatus();
		}

		public void onKeyUp(KeyUpEvent event) {
			updateStatus();
		}

		public void onChange(ChangeEvent event) {
			if (event.getSource().equals(fontList)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"font-family: " + fontList.getValue(fontList.getSelectedIndex()) + ";\">", HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.setFontName(fontList.getValue(fontList.getSelectedIndex()));
				}
			} else if (event.getSource().equals(colorlist)) {
				if (isHTMLMode()) {
					changeHtmlStyle("<span style=\"color: " + colorlist.getValue(colorlist.getSelectedIndex()) + ";\">", HTML_STYLE_CLOSE_SPAN);
				} else {
					styleTextFormatter.setForeColor(colorlist.getValue(colorlist.getSelectedIndex()));
				}
			}
		}
	}

	/** Native JavaScript that returns the selected text and position of the start **/
	public static native JsArrayString getSelection(Element elem) /*-{
        var txt = "";
        var pos = 0;
        var range;
        var parentElement;
        var container;

        if (elem.contentWindow.getSelection) {
            txt = elem.contentWindow.getSelection();
            pos = elem.contentWindow.getSelection().getRangeAt(0).startOffset;
        } else if (elem.contentWindow.document.getSelection) {
            txt = elem.contentWindow.document.getSelection();
            pos = elem.contentWindow.document.getSelection().getRangeAt(0).startOffset;
        } else if (elem.contentWindow.document.selection) {
            range = elem.contentWindow.document.selection.createRange();
            txt = range.text;
            parentElement = range.parentElement();
            container = range.duplicate();
            container.moveToElementText(parentElement);
            container.setEndPoint('EndToEnd', range);
            pos = container.text.length - range.text.length;
        }
        return ["" + txt, "" + pos];
    }-*/;

	/** Method called to toggle the style in HTML-Mode **/
	private void changeHtmlStyle(String startTag, String stopTag) {
		JsArrayString tx = getSelection(styleText.getElement());
		String txbuffer = styleText.getText();
		Integer startpos = Integer.parseInt(tx.get(1));
		String selectedText = tx.get(0);
		styleText.setText(txbuffer.substring(0, startpos) + startTag + selectedText + stopTag + txbuffer.substring(startpos + selectedText.length()));
	}

	/** Private method with a more understandable name to get if HTML mode is on or not **/
	private Boolean isHTMLMode() {
		return texthtml.isDown();
	}

	/** Private method to set the toggle buttons and disable/enable buttons which do not work in html-mode **/
	private void updateStatus() {
		if (styleTextFormatter != null) {
			bold.setDown(styleTextFormatter.isBold());
			italic.setDown(styleTextFormatter.isItalic());
			underline.setDown(styleTextFormatter.isUnderlined());
			subscript.setDown(styleTextFormatter.isSubscript());
			superscript.setDown(styleTextFormatter.isSuperscript());
			stroke.setDown(styleTextFormatter.isStrikethrough());
		}

		if (isHTMLMode()) {
			removeformatting.setEnabled(false);
			indentleft.setEnabled(false);
			breaklink.setEnabled(false);
		} else {
			removeformatting.setEnabled(true);
			indentleft.setEnabled(true);
			breaklink.setEnabled(true);
		}
	}

	/** Initialize the options on the toolbar **/
	private void buildTools() {
		//Init the TOP Panel forst
		topPanel.add(bold = createToggleButton(OKMBundleResources.INSTANCE.bold(), Main.i18n("richtext.bold")));
		topPanel.add(italic = createToggleButton(OKMBundleResources.INSTANCE.italic(), Main.i18n("richtext.italic")));
		topPanel.add(underline = createToggleButton(OKMBundleResources.INSTANCE.underline(), Main.i18n("richtext.underline")));
		topPanel.add(stroke = createToggleButton(OKMBundleResources.INSTANCE.stroke(), Main.i18n("richtext.stroke")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(subscript = createToggleButton(OKMBundleResources.INSTANCE.subScript(), Main.i18n("richtext.subscript")));
		topPanel.add(superscript = createToggleButton(OKMBundleResources.INSTANCE.superScript(), Main.i18n("richtext.superscript")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(alignleft = createPushButton(OKMBundleResources.INSTANCE.justifyLeft(), Main.i18n("richtext.justify.left")));
		topPanel.add(alignmiddle = createPushButton(OKMBundleResources.INSTANCE.justifyCenter(), Main.i18n("richtext.justify.center")));
		topPanel.add(justify = createPushButton(OKMBundleResources.INSTANCE.justify(), Main.i18n("richtext.justify")));
		topPanel.add(alignright = createPushButton(OKMBundleResources.INSTANCE.justifyRight(), Main.i18n("richtext.justify.right")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(orderlist = createPushButton(OKMBundleResources.INSTANCE.ordered(), Main.i18n("richtext.list.ordered")));
		topPanel.add(unorderlist = createPushButton(OKMBundleResources.INSTANCE.unOrdered(), Main.i18n("richtext.list.unordered")));
		topPanel.add(indentright = createPushButton(OKMBundleResources.INSTANCE.identRight(), Main.i18n("richtext.ident.right")));
		topPanel.add(indentleft = createPushButton(OKMBundleResources.INSTANCE.identLeft(), Main.i18n("richtext.ident.left")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(generatelink = createPushButton(OKMBundleResources.INSTANCE.createEditorLink(), Main.i18n("richtext.link.create")));
		topPanel.add(breaklink = createPushButton(OKMBundleResources.INSTANCE.breakEditorLink(), Main.i18n("richtext.link.break")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(insertline = createPushButton(OKMBundleResources.INSTANCE.line(), Main.i18n("richtext.line")));
		topPanel.add(insertimage = createPushButton(OKMBundleResources.INSTANCE.picture(), Main.i18n("richtext.image")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(removeformatting = createPushButton(OKMBundleResources.INSTANCE.removeFormat(), Main.i18n("richtext.remove.format")));
		topPanel.add(new HTML("&nbsp;"));
		topPanel.add(texthtml = createToggleButton(OKMBundleResources.INSTANCE.html(), Main.i18n("richtext.switch.view")));

		//Init the BOTTOM Panel
		fontList = new ListBox();
		fontList.addChangeHandler(evHandler);
		fontList.setVisibleItemCount(1);
		refreshFontList();
		colorlist = new ListBox();
		colorlist.addChangeHandler(evHandler);
		colorlist.setVisibleItemCount(1);
		refreshColorList();
		bottomPanel.add(fontList);
		bottomPanel.add(new HTML("&nbsp;"));
		bottomPanel.add(colorlist);

		fontList.setStyleName("okm-Input");
		colorlist.setStyleName("okm-Input");
	}

	/** Method to create a Toggle button for the toolbar **/
	private ToggleButton createToggleButton(ImageResource resource, String tip) {
		ToggleButton tb = new ToggleButton(new Image(resource));
		tb.addClickHandler(evHandler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}

	/** Method to create a Push button for the toolbar **/
	private PushButton createPushButton(ImageResource resource, String tip) {
		PushButton tb = new PushButton(new Image(resource));
		tb.addClickHandler(evHandler);
		if (tip != null) {
			tb.setTitle(tip);
		}
		return tb;
	}

	/** Method to refresh the fontlist values for the toolbar **/
	private void refreshFontList() {
		fontList.clear();
		fontList.addItem(Main.i18n("richtext.fonts"));
		fontList.addItem("Times New Roman", "Times New Roman");
		fontList.addItem("Arial", "Arial");
		fontList.addItem("Courier New", "Courier New");
		fontList.addItem("Georgia", "Georgia");
		fontList.addItem("Trebuchet", "Trebuchet");
		fontList.addItem("Verdana", "Verdana");

	}

	/** Method to refresh the colorlist for the toolbar **/
	private void refreshColorList() {
		colorlist.clear();
		colorlist.addItem(Main.i18n("richtext.colors"));
		colorlist.addItem(Main.i18n("richtext.white"), "#FFFFFF");
		colorlist.addItem(Main.i18n("richtext.black"), "#000000");
		colorlist.addItem(Main.i18n("richtext.red"), "red");
		colorlist.addItem(Main.i18n("richtext.green"), "green");
		colorlist.addItem(Main.i18n("richtext.yellow"), "yellow");
		colorlist.addItem(Main.i18n("richtext.blue"), "blue");
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		refreshFontList();
		refreshColorList();
		bold.setTitle(Main.i18n("richtext.bold"));
		italic.setTitle(Main.i18n("richtext.italic"));
		underline.setTitle(Main.i18n("richtext.underline"));
		stroke.setTitle(Main.i18n("richtext.stroke"));
		subscript.setTitle(Main.i18n("richtext.subscript"));
		superscript.setTitle(Main.i18n("richtext.superscript"));
		alignleft.setTitle(Main.i18n("richtext.justify.left"));
		alignmiddle.setTitle(Main.i18n("richtext.justify.center"));
		justify.setTitle(Main.i18n("richtext.justify"));
		alignright.setTitle(Main.i18n("richtext.justify.right"));
		orderlist.setTitle(Main.i18n("richtext.list.ordered"));
		unorderlist.setTitle(Main.i18n("richtext.list.unordered"));
		indentright.setTitle(Main.i18n("richtext.ident.right"));
		indentleft.setTitle(Main.i18n("richtext.ident.left"));
		generatelink.setTitle(Main.i18n("richtext.link.create"));
		breaklink.setTitle(Main.i18n("richtext.link.break"));
		insertline.setTitle(Main.i18n("richtext.line"));
		insertimage.setTitle(Main.i18n("richtext.image"));
		removeformatting.setTitle(Main.i18n("richtext.remove.format"));
		texthtml.setTitle(Main.i18n("richtext.switch.view"));
		richTextPopup.langRefresh();
	}

	@Override
	public void insertURL(String url) {
		if (url != null) {
			if (isHTMLMode()) {
				changeHtmlStyle("<a href=\"" + url + "\">", "</a>");
			} else {
				styleTextFormatter.createLink(url);
			}
		}
	}

	@Override
	public void insertImageURL(String url) {
		if (url != null) {
			if (isHTMLMode()) {
				changeHtmlStyle("<img src=\"" + url + "\">", "");
			} else {
				styleTextFormatter.insertImage(url);
			}
		}
	}
}
