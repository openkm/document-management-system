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

package com.openkm.frontend.client.widget.properties.mail;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Search saved menu
 *
 * @author jllort
 *
 */
public class Menu extends MenuBase {

	private MenuBar mailMenu;
	private MenuItem all;
	private MenuItem cc;
	private MenuItem bcc;
	private MenuItem reply;
	private MenuItem extra;
	private boolean showAll = false;
	private boolean showCC = false;
	private boolean showBCC = false;
	private boolean showReply = false;
	private boolean showExtra = false;

	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected

		// First initialize language values
		mailMenu = new MenuBar(true);
		all = new MenuItem(Util.menuHTML("img/icon/actions/select_all.png", Main.i18n("mail.menu.show.all")), true, commandShowAll);
		all.addStyleName("okm-MenuItem");
		mailMenu.addItem(all);
		cc = new MenuItem(Util.menuHTML("img/white_page.png", Main.i18n("mail.menu.show.cc")), true, commandShowCC);
		cc.addStyleName("okm-MenuItem");
		mailMenu.addItem(cc);
		bcc = new MenuItem(Util.menuHTML("img/white_page.png", Main.i18n("mail.menu.show.bcc")), true, commandShowBCC);
		bcc.addStyleName("okm-MenuItem");
		mailMenu.addItem(bcc);
		reply = new MenuItem(Util.menuHTML("img/white_page.png", Main.i18n("mail.menu.show.reply")), true, commandShowReply);
		reply.addStyleName("okm-MenuItem");
		mailMenu.addItem(reply);
		extra = new MenuItem(Util.menuHTML("img/white_page.png", Main.i18n("mail.menu.show.extre")), true, commandShowExtra);
		extra.addStyleName("okm-MenuItem");
		mailMenu.addItem(extra);
		mailMenu.addStyleName("okm-MenuBar");
		initWidget(mailMenu);
	}

	// Command menu to show all
	Command commandShowAll = new Command() {
		public void execute() {
			showAll = !showAll;
			showCC = showAll;
			showBCC = showAll;
			showReply = showAll;
			showExtra = showAll;
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.showHideAll(showAll);
			langRefresh();
			evaluateOptions();
			hide();
		}
	};

	// Command menu to show cc
	Command commandShowCC = new Command() {
		public void execute() {
			showCC = !showCC;
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.showCC(showCC);
			langRefresh();
			evaluateOptions();
			hide();
		}
	};

	// Command menu to show bcc
	Command commandShowBCC = new Command() {
		public void execute() {
			showBCC = !showBCC;
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.showBCC(showBCC);
			langRefresh();
			evaluateOptions();
			hide();
		}
	};

	// Command menu to show reply
	Command commandShowReply = new Command() {
		public void execute() {
			showReply = !showReply;
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.showReply(showReply);
			langRefresh();
			evaluateOptions();
			hide();
		}
	};

	// Command menu to show extra
	Command commandShowExtra = new Command() {
		public void execute() {
			showExtra = !showExtra;
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.showExtra(showExtra);
			langRefresh();
			evaluateOptions();
			hide();
		}
	};

	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		all.setHTML(Util.menuHTML("img/icon/actions/select_all.png", !showAll ? Main.i18n("mail.menu.show.all") : Main.i18n("mail.menu.hide.all")));
		cc.setHTML(Util.menuHTML("img/white_page.png", !showCC ? Main.i18n("mail.menu.show.cc") : Main.i18n("mail.menu.hide.cc")));
		bcc.setHTML(Util.menuHTML("img/white_page.png", !showBCC ? Main.i18n("mail.menu.show.bcc") : Main.i18n("mail.menu.hide.bcc")));
		reply.setHTML(Util.menuHTML("img/white_page.png", !showReply ? Main.i18n("mail.menu.show.reply") : Main.i18n("mail.menu.hide.reply")));
		extra.setHTML(Util.menuHTML("img/white_page.png", !showExtra ? Main.i18n("mail.menu.show.extra") : Main.i18n("mail.menu.hide.extra")));
	}

	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.mailViewer.optionsMenuPopup.hide();
	}

	/**
	 * evaluateOptions
	 */
	private void evaluateOptions() {
		showAll = showCC && showBCC && showReply && showExtra;
		if (!showAll) {
			enable(all);
		} else {
			disable(all);
		}
		if (!showCC) {
			enable(cc);
		} else {
			disable(cc);
		}
		if (!showBCC) {
			enable(bcc);
		} else {
			disable(bcc);
		}
		if (!showReply) {
			enable(reply);
		} else {
			disable(reply);
		}
		if (!showExtra) {
			enable(extra);
		} else {
			disable(extra);
		}
	}

	@Override
	public void setOptions(ToolBarOption toolBarOption) {
		// Not implemented
	}

	@Override
	public void evaluateMenuOptions() {
		// Not implemented
	}

	@Override
	public void disableAllOptions() {
		// Not implemented
	}

	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		// Not implemented
	}

	@Override
	public void disableAddPropertyGroup() {
		// Not implemented
	}

	@Override
	public void enableAddPropertyGroup() {
		// Not implemented
	}

	@Override
	public void disablePdfMerge() {
		// Not implemented
	}

	@Override
	public void enablePdfMerge() {
		// Not implemented
	}
}