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

package com.openkm.frontend.client.widget.foldertree;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.Draggable;
import com.openkm.frontend.client.widget.OriginPanel;

import java.util.Vector;

/**
 * ExtendedTree captures right button and marks a popup flag
 *
 * @author jllort
 *
 */
public class ExtendedTree extends Tree implements HasSelectionHandlers<TreeItem> {
	// Drag pixels sensibility
	private static final int DRAG_PIXELS_SENSIBILITY = 3;

	private boolean flagPopup = false;
	public int mouseX = 0;
	public int mouseY = 0;
	private boolean dragged = false;
	private int mouseDownX = 0;
	private int mouseDownY = 0;

	/**
	 * ExtendedTree
	 */
	public ExtendedTree() {
		super();
		sinkEvents(Event.MOUSEEVENTS | Event.ONCLICK | Event.ONDBLCLICK);
	}

	/**
	 * evalDragPixelSensibility
	 */
	private boolean evalDragPixelSensibility() {
		if (mouseDownX - mouseX >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else if (mouseX - mouseDownX >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else if (mouseDownY - mouseY >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else if (mouseY - mouseDownY >= DRAG_PIXELS_SENSIBILITY) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * isShowPopUP
	 *
	 * @return true or false popup flag
	 */
	public boolean isShowPopUP() {
		return flagPopup;
	}

	@Override
	public void onBrowserEvent(Event event) {

		// When de button mouse is released
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			// When de button mouse is released
			mouseX = DOM.eventGetClientX(event);
			mouseY = DOM.eventGetClientY(event);

			// remove dragable item
			Main.get().draggable.clear();

			switch (DOM.eventGetButton(event)) {
				case Event.BUTTON_RIGHT:
					DOM.eventPreventDefault(event); // Prevent to fire event to browser
					flagPopup = true;
					mouseDownX = 0;
					mouseDownY = 0;
					dragged = false;
					Main.get().activeFolderTree.menuPopup.disableAllOptions();
					fireSelection(elementClicked(DOM.eventGetTarget(event)));
					break;
				default:
					flagPopup = false;
					// dragging is enable only if cursor is inside actual item
					dragged = isCursorInsideActualItem(elementClicked(DOM.eventGetTarget(event)));
					mouseDownX = event.getScreenX();
					mouseDownY = event.getClientY();
			}
		} else if (DOM.eventGetType(event) == Event.ONMOUSEMOVE) {
			mouseX = DOM.eventGetClientX(event);
			mouseY = DOM.eventGetClientY(event);
			if (Main.get().activeFolderTree.canDrag() && dragged && mouseDownX > 0 && mouseDownY > 0
					&& evalDragPixelSensibility()) {
				TreeItem actualItem = Main.get().activeFolderTree.getActualItem();
				Main.get().draggable.show(actualItem.getHTML(), OriginPanel.TREE_ROOT);
				Main.get().activeFolderTree.fileBrowserRefreshDone();
				mouseDownX = 0;
				mouseDownY = 0;
				dragged = false;
			}
		} else if (DOM.eventGetType(event) == Event.ONMOUSEUP || DOM.eventGetType(event) == Event.ONCLICK
				|| DOM.eventGetType(event) == Event.ONDBLCLICK) {
			mouseDownX = 0;
			mouseDownY = 0;
			dragged = false; // Always disabling the popup flag
		}

		// Prevent folder creation or renaming propagate actions to other tree nodes
		int action = Main.get().activeFolderTree.getFolderAction();

		if (action != FolderTree.ACTION_CREATE && action != FolderTree.ACTION_RENAME) {
			super.onBrowserEvent(event);
		}
	}

	/**
	 * disableDragged
	 */
	public void disableDragged() {
		dragged = false;
	}

	/**
	 * fire a change event
	 */
	private void fireSelection(TreeItem treeItem) {
		// SelectElement nativeEvent = Document.get().createSelectElement();
		SelectionEvent.fire(this, treeItem);
		// setSelectedItem(treeItem); // Now is not necessary select treeItem here is done by capturing events
	}

	public HandlerRegistration addSelectionHandler(SelectionHandler<TreeItem> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * elementClicked
	 *
	 * Returns the treeItem when and element is clicked, used to capture drag and drop tree Item
	 */
	public TreeItem elementClicked(Element element) {
		Vector<Element> chain = new Vector<Element>();
		collectElementChain(chain, this.getElement(), element);
		TreeItem item = findItemByChain(chain, 0, null);
		return item;
	}

	/**
	 * collectElementChain
	 */
	private void collectElementChain(Vector<Element> chain, Element elementRoot, Element element) {
		if ((element == null) || element == elementRoot)
			return;

		collectElementChain(chain, elementRoot, DOM.getParent(element));
		chain.add(element);
	}

	/**
	 * findItemByChain
	 */
	private TreeItem findItemByChain(Vector<Element> chain, int idx, TreeItem root) {
		if (idx == chain.size())
			return root;

		Element hCurElem = (Element) chain.get(idx);

		if (root == null) {
			for (int i = 0, n = this.getItemCount(); i < n; ++i) {
				TreeItem child = this.getItem(i);
				if (child.getElement() == hCurElem) {
					TreeItem retItem = findItemByChain(chain, idx + 1, child);
					if (retItem == null)
						return child;
					return retItem;
				}
			}
		} else {
			for (int i = 0, n = root.getChildCount(); i < n; ++i) {
				TreeItem child = root.getChild(i);
				if (child.getElement() == hCurElem) {
					TreeItem retItem = findItemByChain(chain, idx + 1, root.getChild(i));
					if (retItem == null)
						return child;
					return retItem;
				}
			}
		}

		return findItemByChain(chain, idx + 1, root);
	}

	/**
	 * Detects whether mouse cursor is inside actual item.
	 *
	 * @return returns true if mouse cursor is inside actual item
	 */
	private boolean isCursorInsideActualItem(TreeItem clickedItem) {
		if (clickedItem == null) {
			return false;
		}

		Element selectedElement = Draggable.getSelectedElement(clickedItem.getElement());

		if (selectedElement == null) {
			return false;
		}

		return mouseX >= selectedElement.getAbsoluteLeft() && mouseX <= selectedElement.getAbsoluteRight()
				&& mouseY >= selectedElement.getAbsoluteTop() && mouseY <= selectedElement.getAbsoluteBottom();
	}
}