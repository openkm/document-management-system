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

package com.openkm.frontend.client.panel.left;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.categories.CategoriesTree;
import com.openkm.frontend.client.widget.foldertree.Status;
import com.openkm.frontend.client.widget.mail.MailTree;
import com.openkm.frontend.client.widget.metadata.MetadataTree;
import com.openkm.frontend.client.widget.personal.PersonalTree;
import com.openkm.frontend.client.widget.taxonomy.TaxonomyTree;
import com.openkm.frontend.client.widget.template.TemplateTree;
import com.openkm.frontend.client.widget.thesaurus.ThesaurusTree;
import com.openkm.frontend.client.widget.trash.TrashTree;

/**
 * Navigator panel
 *
 * @author jllort
 *
 */
public class Navigator extends Composite {

	public ExtendedStackPanel stackPanel;
	public ExtendedScrollPanel scrollTaxonomyPanel;
	public ScrollPanel scrollCategoriesPanel;
	public ScrollPanel scrollMetadataPanel;
	public ScrollPanel scrollThesaurusPanel;
	private ScrollPanel scrollTrashPanel;
	private ExtendedScrollPanel scrollTemplatePanel;
	private ExtendedScrollPanel scrollPersonalPanel;
	private ExtendedScrollPanel scrollMailPanel;
	public VerticalPanel verticalTaxonomyPanel;
	public VerticalPanel verticalCategoriesPanel;
	public VerticalPanel verticalMetadataPanel;
	public VerticalPanel verticalThesaurusPanel;
	public VerticalPanel verticalTrashPanel;
	public VerticalPanel verticalTemplatePanel;
	public VerticalPanel verticalMyDocumentsPanel;
	public VerticalPanel verticalMailPanel;
	public TaxonomyTree taxonomyTree;
	public CategoriesTree categoriesTree;
	public MetadataTree metadataTree;
	public ThesaurusTree thesaurusTree;
	public TemplateTree templateTree;
	public PersonalTree personalTree;
	public TrashTree trashTree;
	public MailTree mailTree;
	public Status status;
	private int width = 0;
	private int height = 0;
	private int skinExtrStackSize = 0;

	public Navigator() {
		stackPanel = new ExtendedStackPanel();
		scrollTaxonomyPanel = new ExtendedScrollPanel();
		scrollTaxonomyPanel.setSize("100%", "100%");
		scrollCategoriesPanel = new ScrollPanel();
		scrollCategoriesPanel.setSize("100%", "100%");
		scrollMetadataPanel = new ScrollPanel();
		scrollMetadataPanel.setSize("100%", "100%");
		scrollThesaurusPanel = new ScrollPanel();
		scrollThesaurusPanel.setSize("100%", "100%");
		scrollTrashPanel = new ScrollPanel();
		scrollTrashPanel.setSize("100%", "100%");
		scrollTemplatePanel = new ExtendedScrollPanel();
		scrollTemplatePanel.setSize("100%", "100%");
		scrollPersonalPanel = new ExtendedScrollPanel();
		scrollPersonalPanel.setSize("100%", "100%");
		scrollMailPanel = new ExtendedScrollPanel();
		scrollMailPanel.setSize("100%", "100%");
		verticalTaxonomyPanel = new VerticalPanel();
		verticalTaxonomyPanel.setSize("100%", "100%");
		verticalCategoriesPanel = new VerticalPanel();
		verticalCategoriesPanel.setSize("100%", "100%");
		verticalMetadataPanel = new VerticalPanel();
		verticalMetadataPanel.setSize("100%", "100%");
		verticalThesaurusPanel = new VerticalPanel();
		verticalThesaurusPanel.setSize("100%", "100%");
		verticalTrashPanel = new VerticalPanel();
		verticalTrashPanel.setSize("100%", "100%");
		verticalTemplatePanel = new VerticalPanel();
		verticalTemplatePanel.setSize("100%", "100%");
		verticalMyDocumentsPanel = new VerticalPanel();
		verticalMyDocumentsPanel.setSize("100%", "100%");
		verticalMailPanel = new VerticalPanel();
		verticalMailPanel.setSize("100%", "100%");

		status = new Status();
		status.setStyleName("okm-StatusPopup");
		trashTree = new TrashTree();
		trashTree.setSize("100%", "100%");
		templateTree = new TemplateTree();
		templateTree.setSize("100%", "100%");
		mailTree = new MailTree();
		mailTree.setSize("100%", "100%");
		personalTree = new PersonalTree();
		personalTree.setSize("100%", "100%");
		thesaurusTree = new ThesaurusTree();
		thesaurusTree.setSize("100%", "100%");
		metadataTree = new MetadataTree();
		metadataTree.setSize("100%", "100%");
		categoriesTree = new CategoriesTree();
		categoriesTree.setSize("100%", "100%");
		taxonomyTree = new TaxonomyTree();
		taxonomyTree.setSize("100%", "100%");

		verticalTaxonomyPanel.add(taxonomyTree);
		scrollTaxonomyPanel.add(verticalTaxonomyPanel);
		verticalCategoriesPanel.add(categoriesTree);
		scrollCategoriesPanel.add(verticalCategoriesPanel);
		verticalMetadataPanel.add(metadataTree);
		scrollMetadataPanel.add(verticalMetadataPanel);
		verticalThesaurusPanel.add(thesaurusTree);
		scrollThesaurusPanel.add(verticalThesaurusPanel);
		verticalTrashPanel.add(trashTree);
		scrollTrashPanel.add(verticalTrashPanel);
		scrollTrashPanel.addStyleName("okm-DisableSelect"); // Disables drag and drop browser text selection
		verticalTemplatePanel.add(templateTree);
		scrollTemplatePanel.add(verticalTemplatePanel);
		verticalMyDocumentsPanel.add(personalTree);
		scrollPersonalPanel.add(verticalMyDocumentsPanel);
		verticalMailPanel.add(mailTree);
		scrollMailPanel.add(verticalMailPanel);

		scrollTaxonomyPanel.setStyleName("okm-Background-White");
		scrollCategoriesPanel.setStyleName("okm-Background-White");
		scrollMetadataPanel.setStyleName("okm-Background-White");
		scrollThesaurusPanel.setStyleName("okm-Background-White");
		scrollTrashPanel.setStyleName("okm-Background-White");
		scrollTemplatePanel.setStyleName("okm-Background-White");
		scrollPersonalPanel.setStyleName("okm-Background-White");
		scrollMailPanel.setStyleName("okm-Background-White");

		stackPanel.setStyleName("okm-StackPanel");

		initWidget(stackPanel);
	}

	/**
	 * Refresh language descriptions
	 */
	public void langRefresh() {
		int count = 0;
		if (stackPanel.isTaxonomyVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/chart_organisation.gif", Main.i18n("leftpanel.label.taxonomy")));
			taxonomyTree.langRefresh();
		}
		if (stackPanel.isCategoriesVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/table_key.gif", Main.i18n("leftpanel.label.categories")));
			categoriesTree.langRefresh();
		}
		if (stackPanel.isMetadataVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/metadata.png", Main.i18n("leftpanel.label.metadata")));
			metadataTree.langRefresh();
		}
		if (stackPanel.isThesaurusVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/book_open.gif", Main.i18n("leftpanel.label.thesaurus")));
			thesaurusTree.langRefresh();
		}
		if (stackPanel.isTemplatesVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/template.gif", Main.i18n("leftpanel.label.templates")));
			templateTree.langRefresh();
		}
		if (stackPanel.isPersonalVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/personal.gif", Main.i18n("leftpanel.label.my.documents")));
			personalTree.langRefresh();
		}
		if (stackPanel.isMailVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/email.gif", Main.i18n("leftpanel.label.mail")));
			mailTree.langRefresh();
		}
		if (stackPanel.isTrashVisible()) {
			stackPanel.setHeaderHTML(count++, Util.createHeaderHTML("img/icon/stackpanel/bin.gif", Main.i18n("leftpanel.label.trash")));
			trashTree.langRefresh();
		}
	}

	/**
	 * Resizes all objects on the widget the panel and the tree
	 *
	 * @param width The widget width
	 * @param height The widget height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		int hiddenStacks = stackPanel.getHiddenStacks();
		width = width - 2; // -2 caused by border
		height = height - 2; // -2 caused by border
		// To prevent negative resizing
		if (width < 0) {
			width = 0;
		}
		if (height < 0) {
			height = 0;
		}
		stackPanel.setPixelSize(width, height);
		// Calculating scroll height
		height = (height - ((UIDesktopConstants.NUMBER_OF_STACKS - hiddenStacks) * (UIDesktopConstants.STACK_HEIGHT + skinExtrStackSize)));
		// To prevent negative resizing
		if (height < 0) {
			height = 0;
		}
		if (stackPanel.isTaxonomyVisible()) {
			scrollTaxonomyPanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isCategoriesVisible()) {
			scrollCategoriesPanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isMetadataVisible()) {
			scrollMetadataPanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isThesaurusVisible()) {
			scrollThesaurusPanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isTemplatesVisible()) {
			scrollTemplatePanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isPersonalVisible()) {
			scrollPersonalPanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isMailVisible()) {
			scrollMailPanel.setSize("" + width + "px", "" + height + "px");
		}
		if (stackPanel.isTrashVisible()) {
			scrollTrashPanel.setSize("" + width + "px", "" + height + "px");
		}
	}

	/**
	 * showTaxonomy
	 */
	public void showTaxonomy() {
		stackPanel.showTaxonomy();
	}

	/**
	 * showCategories
	 */
	public void showCategories() {
		stackPanel.showCategories();
	}

	/**
	 * showMetadata
	 */
	public void showMetadata() {
		stackPanel.showMetadata();
	}

	/**
	 * showThesaurus
	 */
	public void showThesaurus() {
		stackPanel.showThesaurus();
	}

	/**
	 * showTemplates
	 */
	public void showTemplates() {
		stackPanel.showTemplates();
	}

	/**
	 * showPersonal
	 */
	public void showPersonal() {
		stackPanel.showPersonal();
	}

	/**
	 * showMail
	 */
	public void showMail() {
		stackPanel.showMail();
	}

	/**
	 * showTrash
	 */
	public void showTrash() {
		stackPanel.showTrash();
	}

	/**
	 * refreshContentPanels
	 */
	private void refreshStartupContentPanels() {
		while (stackPanel.getWidgetCount() > 0) {
			stackPanel.remove(0);
		}
		if (stackPanel.isTaxonomyVisible()) {
			stackPanel.add(scrollTaxonomyPanel, Util.createHeaderHTML("img/icon/stackpanel/chart_organisation.gif", Main.i18n("leftpanel.label.taxonomy")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isCategoriesVisible()) {
			stackPanel.add(scrollCategoriesPanel, Util.createHeaderHTML("img/icon/stackpanel/table_key.gif", Main.i18n("leftpanel.label.categories")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isMetadataVisible()) {
			stackPanel.add(scrollMetadataPanel, Util.createHeaderHTML("img/icon/stackpanel/metadata.png", Main.i18n("leftpanel.label.metadata")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isThesaurusVisible()) {
			stackPanel.add(scrollThesaurusPanel, Util.createHeaderHTML("img/icon/stackpanel/book_open.gif", Main.i18n("leftpanel.label.thesaurus")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isTemplatesVisible()) {
			stackPanel.add(scrollTemplatePanel, Util.createHeaderHTML("img/icon/stackpanel/template.gif", Main.i18n("leftpanel.label.templates")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isPersonalVisible()) {
			stackPanel.add(scrollPersonalPanel, Util.createHeaderHTML("img/icon/stackpanel/personal.gif", Main.i18n("leftpanel.label.my.documents")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isMailVisible()) {
			stackPanel.add(scrollMailPanel, Util.createHeaderHTML("img/icon/stackpanel/email.gif", Main.i18n("leftpanel.label.mail")), true, UIDesktopConstants.STACK_HEIGHT);
		}
		if (stackPanel.isTrashVisible()) {
			stackPanel.add(scrollTrashPanel, Util.createHeaderHTML("img/icon/stackpanel/bin.gif", Main.i18n("leftpanel.label.trash")), true, UIDesktopConstants.STACK_HEIGHT);
		}

		stackPanel.setStartUpFinished();
	}

	/**
	 * refreshView
	 *
	 * Only must be executed in starting up time
	 *
	 */
	public void refreshView() {
		refreshStartupContentPanels();
		setSize(width, height);
	}

	/**
	 * Gets the stack index value
	 *
	 * @return The stack index value
	 */
	public int getStackIndex() {
		return stackPanel.getStackIndex();
	}

	/**
	 * @param skinExtrStackSize
	 */
	public void setSkinExtrStackSize(int skinExtrStackSize) {
		this.skinExtrStackSize = skinExtrStackSize;
	}
}