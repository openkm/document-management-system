/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.frontend.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.service.*;

import java.util.Iterator;

/**
 * @author jllort
 *
 */
public class Draggable extends Composite implements OriginPanel {
	private final OKMFolderServiceAsync folderService = (OKMFolderServiceAsync) GWT.create(OKMFolderService.class);
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT
			.create(OKMDocumentService.class);
	private final OKMMailServiceAsync mailService = (OKMMailServiceAsync) GWT.create(OKMMailService.class);

	private boolean dragged = false;
	private FlexTable table = new FlexTable();
	private HTML floater = new HTML();

	private int originPanel = NONE;
	private TreeItem selectedTreeItem;
	private TreeItem lastSelectedTreeItem;
	private Element selectedElement;
	private Element lastSelectElement;

	/**
	 * Dragable
	 */
	public Draggable() {
		dragged = false;
		floater = new HTML("");
		table.setVisible(false);
		floater.sinkEvents(Event.MOUSEEVENTS);
		floater.setWordWrap(false);
		floater.setStyleName("okm-Draggable");

		table.setVisible(false);
		table.setWidget(0, 0, floater);
		table.setStyleName("okm-Draggable");

		floater.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				DOM.releaseCapture(floater.getElement());
				floater.setHTML("");
				table.setVisible(false);

				// Only move if dragged has been enabled by timer
				if (dragged) {

					// Action depends on origin dragable
					switch (originPanel) {
						case TREE_ROOT:

							TreeItem clickedTreeItem = Main.get().activeFolderTree.elementClicked(DOM
									.eventGetTarget((Event) event.getNativeEvent()));
							if (clickedTreeItem != null
									&& (((GWTFolder) clickedTreeItem.getUserObject()).getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
								final TreeItem draggedTreeItem = Main.get().activeFolderTree.getActualItem();
								boolean isChild = DOM.isOrHasChild(draggedTreeItem.getElement(),
										clickedTreeItem.getElement());

								if (draggedTreeItem != clickedTreeItem && !isChild) {
									// Actual folder
									GWTFolder gwtFolder = ((GWTFolder) draggedTreeItem.getUserObject());

									// Destination path
									final String dstPath = ((GWTFolder) clickedTreeItem.getUserObject()).getPath();

									// The parent of actual item selected
									TreeItem parentItem = draggedTreeItem.getParentItem();
									ObjectToMove objToMove = new ObjectToMove(gwtFolder, dstPath, null,
											draggedTreeItem, parentItem, clickedTreeItem);
									Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_TREE);
									Main.get().confirmPopup.setValue(objToMove);
									Main.get().confirmPopup.show();
								}
							}
							break;

						case FILE_BROWSER:
							clickedTreeItem = Main.get().activeFolderTree.elementClicked(DOM
									.eventGetTarget((Event) event.getNativeEvent()));
							TreeItem actualTreeItem = Main.get().activeFolderTree.getActualItem();

							if (clickedTreeItem != null
									&& Main.get().mainPanel.desktop.browser.fileBrowser.isSelectedRow()
									&& (((GWTFolder) clickedTreeItem.getUserObject()).getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {

								// Destination path
								final String dstPath = ((GWTFolder) clickedTreeItem.getUserObject()).getPath();

								// if selected path = actual path must not move
								if (!dstPath.equals(((GWTFolder) actualTreeItem.getUserObject()).getPath())) {

									// Unselects folder destination on tree
									if (lastSelectElement != null) {
										DOM.setElementProperty(lastSelectElement, "className", "gwt-TreeItem");
									}

									if (Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {

										final GWTFolder gwtFolder = Main.get().mainPanel.desktop.browser.fileBrowser
												.getFolder(); // The dragged folder
										String fldPath = gwtFolder.getPath(); // Folder actual path

										// Destination path must not containt actual folder path, because folder can't
										// be moved to his subfolders
										if (!dstPath.startsWith(fldPath)) {
											// Gets the moved tree Item
											final TreeItem movedTreeItem = Main.get().activeFolderTree
													.getChildFolder(fldPath);
											ObjectToMove objToMove = new ObjectToMove(gwtFolder, dstPath, fldPath,
													movedTreeItem, actualTreeItem, clickedTreeItem);
											Main.get().confirmPopup
													.setConfirm(ConfirmPopup.CONFIRM_DRAG_DROP_MOVE_FOLDER_FROM_BROWSER);
											Main.get().confirmPopup.setValue(objToMove);
											Main.get().confirmPopup.show();
										}

									} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
										GWTDocument gwtDocument = Main.get().mainPanel.desktop.browser.fileBrowser
												.getDocument(); // The dragged document
										ObjectToMove objToMove = new ObjectToMove(gwtDocument, dstPath, null, null,
												null, null);
										Main.get().confirmPopup
												.setConfirm(ConfirmPopup.CONFIRM_DRAG_DROP_MOVE_DOCUMENT);
										Main.get().confirmPopup.setValue(objToMove);
										Main.get().confirmPopup.show();

									} else if (Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
										GWTMail gwtMail = Main.get().mainPanel.desktop.browser.fileBrowser.getMail(); // The
										// dragged
										// document
										ObjectToMove objToMove = new ObjectToMove(gwtMail, dstPath, null, null, null,
												null);
										Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DRAG_DROP_MOVE_MAIL);
										Main.get().confirmPopup.setValue(objToMove);
										Main.get().confirmPopup.show();
									}
								}
							}
							break;
					}
				}

				dragged = false; // Sets always dragged to false

				// Always we destroy possible timers to automatic up / down scroll
				Main.get().mainPanel.desktop.navigator.scrollTaxonomyPanel.destroyTimer();

			}
		});

		floater.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (dragged && event != null) {

					// Sets the floater visible
					table.setVisible(true);

					int posX = event.getClientX();
					int posY = event.getClientY();
					//RootPanel.get().setWidgetPosition(Main.get().draggable, posX + 1, posY);
					RootLayoutPanel.get().setWidgetLeftWidth(Main.get().draggable, posX + 1, Unit.PX, table.getOffsetWidth(), Unit.PX);
					RootLayoutPanel.get().setWidgetTopHeight(Main.get().draggable, posY, Unit.PX, table.getOffsetHeight(), Unit.PX);


					// Sets selected tree style to indicate posible selected destination
					selectedTreeItem = Main.get().activeFolderTree.elementClicked(DOM.eventGetTarget((Event) event
							.getNativeEvent()));
					TreeItem actualItem = Main.get().activeFolderTree.getActualItem();

					// Removes always style of last selected treeItem
					if (lastSelectedTreeItem != null && !actualItem.equals(lastSelectedTreeItem)
							&& lastSelectElement != null) {
						DOM.setElementProperty(lastSelectElement, "className", "gwt-TreeItem");
						lastSelectedTreeItem = null;
					}

					// Sets the style of actual tree item
					if (selectedTreeItem != null) {
						selectedElement = getSelectedElement(selectedTreeItem.getElement());
						if (selectedElement == null) {
							Window.alert("Problem: '" + selectedTreeItem.getElement().getInnerHTML() + "'");
						}
						DOM.setElementProperty(selectedElement, "className", "gwt-TreeItem gwt-TreeItem-selected");

						if (lastSelectedTreeItem != null && !selectedTreeItem.equals(lastSelectedTreeItem)
								&& !actualItem.equals(lastSelectedTreeItem) && lastSelectElement != null) {
							DOM.setElementProperty(lastSelectElement, "className", "gwt-TreeItem");
						}

						lastSelectedTreeItem = selectedTreeItem;
						lastSelectElement = selectedElement;
					}

					// Action depends dragables destinations widgets
					Main.get().mainPanel.desktop.navigator.scrollTaxonomyPanel.ScrollOnDragDrop(posX + 1, posY);
				}
			}
		});

		initWidget(table);
	}

	/**
	 * modeDocument
	 */
	public void modeDocument(ObjectToMove objToMove) {
		// Move the document
		GWTDocument gwtDocument = (GWTDocument) objToMove.getObject();
		documentService.move(gwtDocument.getPath(), objToMove.getDstPath(), callbackMove);
		// refresh file browser
		Main.get().mainPanel.desktop.browser.fileBrowser.deleteMovedOrMoved();
	}

	/**
	 * modeFolderFromTree
	 */
	public void modeFolderFromTree(final ObjectToMove objToMove) {
		final GWTFolder gwtFolder = (GWTFolder) objToMove.getObject();
		final TreeItem draggedTreeItem = objToMove.getTreeItem();
		TreeItem parentItem = objToMove.getTreeItem2();
		TreeItem clickedTreeItem = objToMove.getTreeItem3();

		// Remove the folders and evaluates parent child status
		draggedTreeItem.remove();
		if (parentItem.getChildCount() == 0) {
			((GWTFolder) parentItem.getUserObject()).setHasChildren(false); // Sets not has folder childs
		}

		clickedTreeItem.addItem(draggedTreeItem); // Adds the draggedItem to selected
		((GWTFolder) clickedTreeItem.getUserObject()).setHasChildren(true); // Always sets that the actual parent folder
		// now has childs
		clickedTreeItem.setState(true); // Always opens treeItem parent
		draggedTreeItem.setSelected(true); // Selects the treeItem

		// Evaluate icon of changed folders last and actual parent tree Item
		Main.get().activeFolderTree.evaluesFolderIcon(parentItem);
		Main.get().activeFolderTree.evaluesFolderIcon(clickedTreeItem);

		folderService.move(gwtFolder.getPath(), objToMove.getDstPath(), new AsyncCallback<Object>() {
			public void onSuccess(Object result) {
				// Sets the folder new path itself and childs

				GWTFolder draggedFolder = (GWTFolder) draggedTreeItem.getUserObject();
				String oldPath = draggedFolder.getPath();
				String newPath = objToMove.getDstPath() + "/" + draggedFolder.getName();
				preventFolderInconsitences(draggedTreeItem, oldPath, newPath, objToMove.getDstPath());
				draggedTreeItem.setState(false);

				Main.get().activeFolderTree.openAllPathFolder(newPath, null);
			}

			public void onFailure(Throwable caught) {
				draggedTreeItem.setState(false);
				Main.get().showError("Move", caught);
			}
		});
	}

	/**
	 * modeFolderFromBrowser
	 */
	public void modeFolderFromBrowser(final ObjectToMove objToMove) {
		final GWTFolder gwtFolder = (GWTFolder) objToMove.getObject();
		final TreeItem movedTreeItem = objToMove.getTreeItem();
		TreeItem actualTreeItem = objToMove.getTreeItem2();
		TreeItem clickedTreeItem = objToMove.getTreeItem3();

		// Remove the folders and evaluates parent child status
		movedTreeItem.remove();
		if (actualTreeItem.getChildCount() == 0) {
			((GWTFolder) actualTreeItem.getUserObject()).setHasChildren(false); // Sets not has folder childs
		}

		clickedTreeItem.addItem(movedTreeItem); // Adds the draggedItem to selected
		((GWTFolder) clickedTreeItem.getUserObject()).setHasChildren(true); // Always sets that the actual parent folder
		// now has childs
		clickedTreeItem.setState(true); // Always opens treeItem parent
		Main.get().activeFolderTree.removeDeleted(objToMove.getFldPath());

		// Evaluate icon of changed folders last and actual parent tree Item
		Main.get().activeFolderTree.evaluesFolderIcon(clickedTreeItem);

		folderService.move(gwtFolder.getPath(), objToMove.getDstPath(), new AsyncCallback<Object>() {
			public void onSuccess(Object result) {
				// Sets the folder new path ( parent and itself ) recursive for itself and childs
				movedTreeItem.setUserObject(gwtFolder);
				String oldPath = gwtFolder.getPath();
				String newPath = objToMove.getDstPath() + "/" + gwtFolder.getName();
				preventFolderInconsitences(movedTreeItem, oldPath, newPath, objToMove.getDstPath());
				movedTreeItem.setState(false);

				// Refresh file browser
				Main.get().mainPanel.desktop.browser.fileBrowser.deleteMovedOrMoved();
			}

			public void onFailure(Throwable caught) {
				movedTreeItem.setState(false);
				Main.get().showError("Move", caught);
			}
		});
	}

	/**
	 * modeMail
	 */
	public void modeMail(ObjectToMove objToMove) {
		// Move the document
		GWTMail gwtMail = (GWTMail) objToMove.getObject();
		mailService.move(gwtMail.getPath(), objToMove.getDstPath(), callbackMove);
		// refresh file browser
		Main.get().mainPanel.desktop.browser.fileBrowser.deleteMovedOrMoved();
	}

	/**
	 * Sets the HTML value to floater
	 */
	public void show(String html, int originPanel) {
		this.originPanel = originPanel;
		DOM.setCapture(floater.getElement());
		floater.setHTML(html);

		// Initialize values
		dragged = true;
		selectedTreeItem = null;
		lastSelectedTreeItem = null;
	}

	/**
	 * Move document or folder
	 */
	final AsyncCallback<Object> callbackMove = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {

		}

		public void onFailure(Throwable caught) {
			Main.get().showError("Move", caught);
		}
	};

	/**
	 * getSelectedElement
	 */
	public static Element getSelectedElement(Element element) {
		if (DOM.getFirstChild(element).getClassName().contains("gwt-TreeItem")) {
			// Case node without childs
			return DOM.getFirstChild(element);
		} else {
			return DOM.getChild(DOM.getChild(DOM.getChild(DOM.getChild(DOM.getChild(element, 0), 0), 0), 1), 0);
		}
	}

	/**
	 * Prevents folder incosistences changing moved path on childs recursivelly
	 * nodes drawed
	 *
	 * @param item The tree node
	 */
	public void preventFolderInconsitences(TreeItem item, String oldPath, String newPath, String parentPath) {
		GWTFolder folderItem = (GWTFolder) item.getUserObject();

		folderItem.setPath(folderItem.getPath().replaceFirst(oldPath, newPath));
		folderItem.setParentPath(parentPath);

		// Recursively changing paht value
		for (int i = 0; i < item.getChildCount(); i++) {
			preventFolderInconsitences(item.getChild(i), oldPath, newPath, folderItem.getPath());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client.ui.Widget)
	 */
	public void add(Widget w) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#clear()
	 */
	public void clear() {
		// disable dragging:
		DOM.releaseCapture(floater.getElement());
		floater.setHTML("");
		table.setVisible(false);
		dragged = false;
		Main.get().mainPanel.desktop.navigator.scrollTaxonomyPanel.destroyTimer();
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
	 */
	public Iterator<Widget> iterator() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#remove(com.google.gwt.user.client.ui.Widget)
	 */
	public boolean remove(Widget w) {
		return true;
	}

	/**
	 * ObjectToMove
	 *
	 * @author jllort
	 *
	 */
	public class ObjectToMove {
		private Object object;
		private String dstPath;
		private String fldPath;
		private TreeItem treeItem;
		private TreeItem treeItem2;
		private TreeItem treeItem3;

		/**
		 * ObjectToMove
		 */
		public ObjectToMove(Object object, String dstPath, String fldPath, TreeItem treeItem, TreeItem treeItem2,
		                    TreeItem treeItem3) {
			this.object = object;
			this.dstPath = dstPath;
			this.fldPath = fldPath;
			this.treeItem = treeItem;
			this.treeItem2 = treeItem2;
			this.treeItem3 = treeItem3;
		}

		public TreeItem getTreeItem3() {
			return treeItem3;
		}

		public void setTreeItem3(TreeItem treeItem3) {
			this.treeItem3 = treeItem3;
		}

		public TreeItem getTreeItem2() {
			return treeItem2;
		}

		public void setTreeItem2(TreeItem treeItem2) {
			this.treeItem2 = treeItem2;
		}

		public String getDstPath() {
			return dstPath;
		}

		public void setDstPath(String dstPath) {
			this.dstPath = dstPath;
		}

		public String getFldPath() {
			return fldPath;
		}

		public void setFldPath(String fldPath) {
			this.fldPath = fldPath;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public TreeItem getTreeItem() {
			return treeItem;
		}

		public void setTreeItem(TreeItem treeItem) {
			this.treeItem = treeItem;
		}
	}
}