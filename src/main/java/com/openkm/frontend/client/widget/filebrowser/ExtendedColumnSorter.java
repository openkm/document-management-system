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

package com.openkm.frontend.client.widget.filebrowser;

import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorter;
import com.google.gwt.gen2.table.client.SortableGrid.ColumnSorterCallback;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.constants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.ColumnComparatorDate;
import com.openkm.frontend.client.util.ColumnComparatorDouble;
import com.openkm.frontend.client.util.ColumnComparatorGWTFormElement;
import com.openkm.frontend.client.util.ColumnComparatorText;

import java.util.*;

/**
 * ExtendedColumnSorter
 *
 * @author jllort
 *
 */
public class ExtendedColumnSorter extends ColumnSorter {

	private String selectedRowDataID = "";
	private int colDataIndex = 0;
	private int column = -1;
	boolean ascending = false;
	private GWTProfileFileBrowser profileFileBrowser;

	/* (non-Javadoc)
	 * @see com.google.gwt.widgetideas.table.client.SortableGrid$ColumnSorter#onSortColumn(com.google.gwt.widgetideas.table.client.SortableGrid, com.google.gwt.widgetideas.table.client.TableModel.ColumnSortList, com.google.gwt.widgetideas.table.client.SortableGrid.ColumnSorterCallback)
	 */
	public void onSortColumn(SortableGrid grid,
	                         ColumnSortList sortList, ColumnSorterCallback callback) {

		// Get the primary column, sort order, number of rows, number of columns
		column = sortList.getPrimaryColumn();
		ascending = sortList.isPrimaryAscending();
		sort(column, ascending);
		callback.onSortingComplete();
	}

	/**
	 * refreshSort
	 */
	public void refreshSort() {
		if (isSorted()) {
			sort(column, ascending);
		}
	}

	/**
	 * isSorted
	 *
	 * @return
	 */
	public boolean isSorted() {
		return column >= 0;
	}

	/**
	 * sort
	 */
	public void sort(int column, boolean ascending) {
		Main.get().mainPanel.desktop.browser.fileBrowser.status.setFlagOrdering();
		int rows = Main.get().mainPanel.desktop.browser.fileBrowser.table.getDataTable().getRowCount();
		int columns = Main.get().mainPanel.desktop.browser.fileBrowser.table.getDataTable().getColumnCount();
		int selectedRow = Main.get().mainPanel.desktop.browser.fileBrowser.table.getSelectedRow();
		Map<Integer, Object> data = new HashMap<Integer, Object>(Main.get().mainPanel.desktop.browser.fileBrowser.table.data);

		List<String[]> elementList = new ArrayList<String[]>();                    // List with all data
		List<GWTObjectToOrder> elementToOrder = new ArrayList<GWTObjectToOrder>();    // List with column data, and actual position

		// Gets the data values and set on a list of String arrays ( element by column )
		int correctedColumn = correctedColumnIndex(column);
		if (correctedColumn <= 17) {
			for (int i = 0; i < rows; i++) {
				String[] rowI = new String[columns];
				GWTObjectToOrder rowToOrder = new GWTObjectToOrder();
				for (int x = 0; x < columns; x++) {
					rowI[x] = Main.get().mainPanel.desktop.browser.fileBrowser.table.getDataTable().getHTML(i, x);
				}
				elementList.add(i, rowI);

				switch (correctedColumn) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 6:
						// Text
						rowToOrder.setObject(rowI[column].toLowerCase());        // Lower case solves problem with sort ordering
						rowToOrder.setDataId("" + i);                            // Actual position value
						elementToOrder.add(rowToOrder);
						break;

					case 4:
						// Bytes
						if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTFolder) {
							rowToOrder.setObject(new Double(0));                                        // Byte value
							rowToOrder.setDataId("" + i);                                                // Actual position value
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTMail) {
							rowToOrder.setObject(new Double(((GWTMail) data.get(Integer.parseInt(rowI[colDataIndex]))).getSize()));
							rowToOrder.setDataId("" + i);                                                // Actual position value
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTDocument) {
							rowToOrder.setObject(new Double(((GWTDocument) data.get(Integer.parseInt(rowI[colDataIndex]))).getActualVersion().getSize()));
							rowToOrder.setDataId("" + i);                                                // Actual position value
							elementToOrder.add(rowToOrder);
						}
						break;

					case 5:
						// Date
						if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTFolder) {
							rowToOrder.setObject(((GWTFolder) data.get(Integer.parseInt(rowI[colDataIndex]))).getCreated());    // Date value
							rowToOrder.setDataId("" + i);                                                            // Actual position value
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTMail) {
							rowToOrder.setObject(((GWTMail) data.get(Integer.parseInt(rowI[colDataIndex]))).getReceivedDate()); // Date value
							rowToOrder.setDataId("" + i);                                                                 // Actual position value
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTDocument) {
							rowToOrder.setObject(((GWTDocument) data.get(Integer.parseInt(rowI[colDataIndex]))).getLastModified()); // Date value
							rowToOrder.setDataId("" + i);                                                                 // Actual position value
							elementToOrder.add(rowToOrder);
						}
						break;

					case 7:
						// Version
						if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTFolder ||
								data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTMail) {
							rowToOrder.setObject(new Double(0));
							rowToOrder.setDataId("" + i);
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTDocument) {
							String version = ((GWTDocument) data.get(Integer.parseInt(rowI[colDataIndex]))).getActualVersion().getName();
							String numberParts[] = version.split("\\.");
							version = "";
							for (int x = 0; x < numberParts.length; x++) {
								switch (numberParts[x].length()) {
									case 1:
										version = version + "00" + numberParts[x];
										break;
									case 2:
										version = version + "0" + numberParts[x];
										break;
								}
							}
							if (numberParts.length == 2) {
								version = version + "000000";
							}
							if (numberParts.length == 3) {
								version = version + "000";
							}
							rowToOrder.setObject(new Double(version));
							rowToOrder.setDataId("" + i);
							elementToOrder.add(rowToOrder);
						}
						break;

					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 17:
						if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTFolder) {
							rowToOrder.setObject(getExtraColumn((GWTFolder) data.get(Integer.parseInt(rowI[colDataIndex])), correctedColumn));    // Extra column
							rowToOrder.setDataId("" + i);                                                        // Actual position value
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTMail) {
							rowToOrder.setObject(getExtraColumn((GWTMail) data.get(Integer.parseInt(rowI[colDataIndex])), correctedColumn));    // Extra column
							rowToOrder.setDataId("" + i);                                                        // Actual position value
							elementToOrder.add(rowToOrder);
						} else if (data.get(Integer.parseInt(rowI[colDataIndex])) instanceof GWTDocument) {
							rowToOrder.setObject(getExtraColumn((GWTDocument) data.get(Integer.parseInt(rowI[colDataIndex])), correctedColumn)); // Extra column
							rowToOrder.setDataId("" + i);                                                        // Actual position value
							elementToOrder.add(rowToOrder);
						}


						break;
				}

				// Saves the selected row
				if (selectedRow == i) {
					selectedRowDataID = rowToOrder.getDataId();
				}
			}

			switch (correctedColumn) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 6:
					// Text
					Collections.sort(elementToOrder, ColumnComparatorText.getInstance());
					break;

				case 4:
				case 7:
					// Bytes
					Collections.sort(elementToOrder, ColumnComparatorDouble.getInstance());
					break;

				case 5:
					// Date
					Collections.sort(elementToOrder, ColumnComparatorDate.getInstance());
					break;

				// Extra columns
				case 8:
				case 9:
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
					// FormElement sort
					Collections.sort(elementToOrder, ColumnComparatorGWTFormElement.getInstance());
					break;
			}

			// Reversing if needed
			if (!ascending) {
				Collections.reverse(elementToOrder);
			}

			applySort(elementList, elementToOrder);
		}
		Main.get().mainPanel.desktop.browser.fileBrowser.status.unsetFlagOrdering();
	}

	/**
	 * @param elementList
	 * @param elementToOrder
	 */
	private void applySort(List<String[]> elementList, List<GWTObjectToOrder> elementToOrder) {
		// Removing all values
		while (Main.get().mainPanel.desktop.browser.fileBrowser.table.getDataTable().getRowCount() > 0) {
			Main.get().mainPanel.desktop.browser.fileBrowser.table.getDataTable().removeRow(0);
		}

		// Data map
		Map<Integer, Object> data = new HashMap<Integer, Object>(Main.get().mainPanel.desktop.browser.fileBrowser.table.data);
		Main.get().mainPanel.desktop.browser.fileBrowser.table.reset();

		int column = 0;
		for (Iterator<GWTObjectToOrder> it = elementToOrder.iterator(); it.hasNext(); ) {
			GWTObjectToOrder orderedColumn = it.next();
			String[] row = elementList.get(Integer.parseInt(orderedColumn.getDataId()));

			if (data.get(Integer.parseInt(row[colDataIndex])) instanceof GWTFolder) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.addRow((GWTFolder) data.get(Integer.parseInt(row[colDataIndex])));
			} else if (data.get(Integer.parseInt(row[colDataIndex])) instanceof GWTMail) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.addRow((GWTMail) data.get(Integer.parseInt(row[colDataIndex])));
			} else if (data.get(Integer.parseInt(row[colDataIndex])) instanceof GWTDocument) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.addRow((GWTDocument) data.get(Integer.parseInt(row[colDataIndex])));
			}

			// Sets selectedRow
			if (!selectedRowDataID.equals("") && selectedRowDataID.equals(row[colDataIndex])) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.setSelectedRow(column);
				selectedRowDataID = "";
			}

			column++;
		}
	}

	/**
	 * getExtraColumn
	 *
	 * @param obj
	 * @param column
	 * @return
	 */
	private GWTFormElement getExtraColumn(Object obj, int column) {
		GWTFormElement formElement = null;
		switch (column) {
			case 8:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn0();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn0();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn0();
				}
				break;
			case 9:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn1();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn1();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn1();
				}
				break;
			case 10:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn2();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn2();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn2();
				}
				break;
			case 11:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn3();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn3();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn3();
				}
				break;
			case 12:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn4();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn4();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn4();
				}
			case 13:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn5();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn5();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn5();
				}
				break;
			case 14:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn6();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn6();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn6();
				}
			case 15:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn7();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn7();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn7();
				}
			case 16:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn8();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn8();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn8();
				}
			case 17:
				if (obj instanceof GWTFolder) {
					return ((GWTFolder) obj).getColumn9();
				} else if (obj instanceof GWTMail) {
					return ((GWTMail) obj).getColumn9();
				}
				if (obj instanceof GWTDocument) {
					return ((GWTDocument) obj).getColumn9();
				}
		}
		return formElement;
	}

	/**
	 * correctedColumnIndex
	 *
	 * @param col
	 * @return
	 */
	private int correctedColumnIndex(int col) {
		int corrected = col;
		if (!profileFileBrowser.isStatusVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_STATUS) {
			corrected++;
		}
		if (!profileFileBrowser.isMassiveVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_MASSIVE) {
			corrected++;
		}
		if (!profileFileBrowser.isIconVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_ICON) {
			corrected++;
		}
		if (!profileFileBrowser.isNameVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_NAME) {
			corrected++;
		}
		if (!profileFileBrowser.isSizeVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_SIZE) {
			corrected++;
		}
		if (!profileFileBrowser.isLastModifiedVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_LASTMODIFIED) {
			corrected++;
		}
		if (!profileFileBrowser.isAuthorVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_AUTHOR) {
			corrected++;
		}
		if (!profileFileBrowser.isVersionVisible() && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_VERSION) {
			corrected++;
		}
		if (profileFileBrowser.getColumn0() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA0) {
			corrected++;
		}
		if (profileFileBrowser.getColumn1() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA1) {
			corrected++;
		}
		if (profileFileBrowser.getColumn2() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA2) {
			corrected++;
		}
		if (profileFileBrowser.getColumn3() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA3) {
			corrected++;
		}
		if (profileFileBrowser.getColumn4() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA4) {
			corrected++;
		}
		if (profileFileBrowser.getColumn5() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA5) {
			corrected++;
		}
		if (profileFileBrowser.getColumn6() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA6) {
			corrected++;
		}
		if (profileFileBrowser.getColumn7() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA7) {
			corrected++;
		}
		if (profileFileBrowser.getColumn8() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA8) {
			corrected++;
		}
		if (profileFileBrowser.getColumn9() == null && corrected >= UIDesktopConstants.FILEBROWSER_COLUMN_EXTRA9) {
			corrected++;
		}
		return corrected;
	}

	/**
	 * setDataColumn
	 *
	 * @param colDataIndex
	 */
	public void setColDataIndex(int colDataIndex) {
		this.colDataIndex = colDataIndex;
	}

	/**
	 * setProfileFileBrowser
	 *
	 * @param profileFileBrowser
	 */
	public void setProfileFileBrowser(GWTProfileFileBrowser profileFileBrowser) {
		this.profileFileBrowser = profileFileBrowser;
	}
}
