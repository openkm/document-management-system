/**
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

package com.openkm.frontend.client.widget.dashboard.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.*;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.service.*;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.util.validator.ValidatorToFire;
import com.openkm.frontend.client.widget.form.FormManager;
import com.openkm.frontend.client.widget.form.HasWorkflow;

import java.util.*;

/**
 * WorkflowFormPanel
 *
 * @author jllort
 *
 */
public class WorkflowFormPanel extends Composite implements HasWorkflow, ValidatorToFire {
	private final OKMWorkflowServiceAsync workflowService = GWT.create(OKMWorkflowService.class);
	private final OKMRepositoryServiceAsync repositoryService = GWT.create(OKMRepositoryService.class);
	private final OKMDocumentServiceAsync documentService = GWT.create(OKMDocumentService.class);
	private final OKMFolderServiceAsync folderService = GWT.create(OKMFolderService.class);

	private VerticalPanel vPanel;
	private GWTTaskInstance taskInstance;
	private FlexTable table;
	private FlexTable parameterTable;
	private Button submitForm;
	private TitleWidget taskTitle;
	private TitleWidget processInstanceTitle;
	private TitleWidget processDefinitionTitle;
	private TitleWidget parametersTitle;
	private TitleWidget commentsTitle;
	private TitleWidget formsTitle;
	private Anchor documentLink;
	private VerticalPanel newNotePanel;
	private TextArea textArea;
	private HTML addComment;
	private Button add;
	private FlexTable tableNotes;
	private FormManager manager;

	/**
	 * WorkflowFormPanel
	 */
	public WorkflowFormPanel() {
		vPanel = new VerticalPanel();
		table = new FlexTable();
		manager = new FormManager(this, this);
		parameterTable = new FlexTable();
		newNotePanel = new VerticalPanel();
		tableNotes = new FlexTable();
		textArea = new TextArea();
		addComment = new HTML("<b>" + Main.i18n("dashboard.workflow.add.comment") + "</b>");
		textArea.setSize("500px", "100px");

		textArea.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (!textArea.getText().equals("")) {
					add.setEnabled(true);
				} else {
					add.setEnabled(false);
				}
			}
		});

		add = new Button(Main.i18n("button.add"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addComment();
			}
		});

		add.setEnabled(false);

		submitForm = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (manager.getValidationProcessor().validate()) {
					setTaskInstanceValues(taskInstance.getId(), null);
					submitForm.setEnabled(false);
				}
			}
		});

		HTML space = new HTML("");
		newNotePanel.add(space);
		newNotePanel.add(addComment);
		newNotePanel.add(textArea);
		HTML space2 = new HTML("");
		newNotePanel.add(space2);
		newNotePanel.add(add);

		newNotePanel.setCellHeight(space, "40px");
		newNotePanel.setCellHeight(space2, "10px");
		newNotePanel.setCellHorizontalAlignment(addComment, HasAlignment.ALIGN_CENTER);
		newNotePanel.setCellHorizontalAlignment(add, HasAlignment.ALIGN_CENTER);

		int[] taskRow = {1, 2, 3, 4, 5, 6};
		int[] processInstanceRow = {8, 9, 10};
		int[] processDefinitionRow = {12, 13, 14, 15};
		int[] dataTitle = {17};
		int[] commentTitle = {19};
		int[] formTitle = {};
		taskTitle = new TitleWidget(Main.i18n("dashboard.workflow.task"), taskRow);
		processInstanceTitle = new TitleWidget(Main.i18n("dashboard.workflow.task.process.instance"), processInstanceRow);
		processDefinitionTitle = new TitleWidget(Main.i18n("dashboard.workflow.task.process.definition"), processDefinitionRow);
		parametersTitle = new TitleWidget(Main.i18n("dashboard.workflow.task.process.data"), dataTitle);
		commentsTitle = new TitleWidget(Main.i18n("dashboard.workflow.comments"), commentTitle);
		formsTitle = new TitleWidget(Main.i18n("dashboard.workflow.task.process.forms"), formTitle);
		taskTitle.setWidth("100%");
		processInstanceTitle.setWidth("100%");
		processDefinitionTitle.setWidth("100%");
		parametersTitle.setWidth("100%");
		commentsTitle.setWidth("100%");
		formsTitle.setWidth("100%");

		table.setWidget(0, 0, taskTitle);
		table.setHTML(1, 0, "<b>" + Main.i18n("dashboard.workflow.task.id") + "</b>");
		table.setHTML(2, 0, "<b>" + Main.i18n("dashboard.workflow.task.name") + "</b>");
		table.setHTML(3, 0, "<b>" + Main.i18n("dashboard.workflow.task.created") + "</b>");
		table.setHTML(4, 0, "<b>" + Main.i18n("dashboard.workflow.task.start") + "</b>");
		table.setHTML(5, 0, "<b>" + Main.i18n("dashboard.workflow.task.duedate") + "</b>");
		//table.setHTML(6, 0, "<b>"+ Main.i18n("dashboard.workflow.task.end") + "</b>");
		table.setHTML(6, 0, "<b>" + Main.i18n("dashboard.workflow.task.description") + "</b>");
		table.setWidget(7, 0, processInstanceTitle);
		table.setHTML(8, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.id") + "</b>");
		table.setHTML(9, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.version") + "</b>");
		table.setHTML(10, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.path") + "</b>");
		table.setWidget(11, 0, processDefinitionTitle);
		table.setHTML(12, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.id") + "</b>");
		table.setHTML(13, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.name") + "</b>");
		table.setHTML(14, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.version") + "</b>");
		table.setHTML(15, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.description") + "</b>");
		table.setWidget(16, 0, parametersTitle);
		table.setWidget(17, 0, parameterTable);
		table.setWidget(18, 0, commentsTitle);
		table.setWidget(19, 0, tableNotes);
		table.setWidget(20, 0, formsTitle);
		table.setWidget(21, 0, manager.getTable());
		table.setHTML(1, 2, "");
		table.setHTML(2, 2, "");
		table.setHTML(3, 2, "");
		table.setHTML(4, 2, "");
		table.setHTML(5, 2, "");
		table.setHTML(8, 2, "");
		table.setHTML(9, 2, "");
		table.setHTML(10, 2, "");
		table.setHTML(12, 2, "");
		table.setHTML(13, 2, "");
		table.setHTML(14, 2, "");

		// Setting visibleRows
		taskTitle.setVisibleRows(false);
		processInstanceTitle.setVisibleRows(false);
		processDefinitionTitle.setVisibleRows(false);
		parametersTitle.setVisibleRows(true);
		commentsTitle.setVisibleRows(false);
		formsTitle.setVisibleRows(true);

		table.getCellFormatter().setWidth(1, 2, "100%");
		table.getCellFormatter().setWidth(8, 2, "100%");
		table.getCellFormatter().setWidth(12, 2, "100%");
		table.getFlexCellFormatter().setColSpan(0, 0, 3);
		table.getFlexCellFormatter().setColSpan(7, 0, 3);
		table.getFlexCellFormatter().setColSpan(11, 0, 3);
		table.getFlexCellFormatter().setColSpan(16, 0, 3);
		table.getFlexCellFormatter().setColSpan(17, 0, 3);
		table.getFlexCellFormatter().setColSpan(18, 0, 3);
		table.getFlexCellFormatter().setColSpan(19, 0, 3);
		table.getFlexCellFormatter().setColSpan(20, 0, 3);
		table.getFlexCellFormatter().setColSpan(21, 0, 3);
		table.getCellFormatter().setStyleName(0, 0, "okm-WorkflowFormPanel-Title");
		table.getCellFormatter().setStyleName(7, 0, "okm-WorkflowFormPanel-Title");
		table.getCellFormatter().setStyleName(11, 0, "okm-WorkflowFormPanel-Title");
		table.getCellFormatter().setStyleName(16, 0, "okm-WorkflowFormPanel-Title");
		table.getCellFormatter().setStyleName(18, 0, "okm-WorkflowFormPanel-Title");
		table.getCellFormatter().setStyleName(20, 0, "okm-WorkflowFormPanel-Title");

		vPanel.add(table);

		table.setStyleName("okm-NoWrap");
		vPanel.setStyleName("okm-WorkflowFormPanel");
		submitForm.setStyleName("okm-YesButton");
		add.setStyleName("okm-AddButton");
		textArea.setStyleName("okm-TextArea");
		tableNotes.setStyleName("okm-DisableSelect");

		tableNotes.setWidth("100%");
		table.setWidth("100%");
		vPanel.setHeight("100%");

		initWidget(vPanel);
	}

	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		taskTitle.setTitle(Main.i18n("dashboard.workflow.task"));
		table.setHTML(1, 0, "<b>" + Main.i18n("dashboard.workflow.task.id") + "</b>");
		table.setHTML(2, 0, "<b>" + Main.i18n("dashboard.workflow.task.name") + "</b>");
		table.setHTML(3, 0, "<b>" + Main.i18n("dashboard.workflow.task.created") + "</b>");
		table.setHTML(4, 0, "<b>" + Main.i18n("dashboard.workflow.task.start") + "</b>");
		table.setHTML(5, 0, "<b>" + Main.i18n("dashboard.workflow.task.duedate") + "</b>");
		table.setHTML(6, 0, "<b>" + Main.i18n("dashboard.workflow.task.description") + "</b>");
		processInstanceTitle.setTitle(Main.i18n("dashboard.workflow.task.process.instance"));
		table.setHTML(8, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.id") + "</b>");
		table.setHTML(9, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.version") + "</b>");
		table.setHTML(10, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.path") + "</b>");
		processDefinitionTitle.setTitle(Main.i18n("dashboard.workflow.task.process.definition"));
		table.setHTML(12, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.id") + "</b>");
		table.setHTML(13, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.name") + "</b>");
		table.setHTML(14, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.version") + "</b>");
		table.setHTML(15, 0, "<b>" + Main.i18n("dashboard.workflow.task.process.description") + "</b>");
		parametersTitle.setTitle(Main.i18n("dashboard.workflow.task.process.data"));
		commentsTitle.setTitle(Main.i18n("dashboard.workflow.comments"));
		formsTitle.setTitle(Main.i18n("dashboard.workflow.task.process.forms"));
		submitForm.setHTML(Main.i18n("button.accept"));
		addComment.setHTML("<b>" + Main.i18n("dashboard.workflow.add.comment") + "</b>");
		add.setText(Main.i18n("button.add"));
	}

	/**
	 * Sets a TaskInstance
	 */
	public void setTaskInstance(GWTTaskInstance taskInstance) {
		this.taskInstance = taskInstance;
		manager.setTaskInstance(taskInstance);
		GWTProcessInstance processInstance = taskInstance.getProcessInstance();
		GWTProcessDefinition processDefinition = processInstance.getProcessDefinition();

		clearPanel();

		table.setHTML(1, 1, "" + taskInstance.getId());
		table.setHTML(2, 1, "" + taskInstance.getName());
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		table.setHTML(3, 1, dtf.format(taskInstance.getCreate()));

		if (taskInstance.getStart() != null) {
			table.setHTML(4, 1, dtf.format(taskInstance.getStart()));
		} else {
			table.setHTML(4, 1, "");
		}

		if (taskInstance.getDueDate() != null) {
			table.setHTML(5, 1, dtf.format(taskInstance.getDueDate()));
		}

		if (taskInstance.getDescription() != null) {
			table.setHTML(6, 1, "" + taskInstance.getDescription());
		}

		table.setHTML(8, 1, "" + processInstance.getId());
		table.setHTML(9, 1, "" + processInstance.getVersion());

		documentLink = null;

		// Print variables
		for (Iterator<String> it = processInstance.getVariables().keySet().iterator(); it.hasNext(); ) {
			String key = it.next();

			if (processInstance.getVariables().get(key) instanceof String) {
				final String value = (String) processInstance.getVariables().get(key);
				int row = parameterTable.getRowCount();

				// Special case path
				if (key.equals(Main.get().workspaceUserProperties.getWorkspace().getWorkflowProcessIntanceVariableUUID())) {
					final int documentRow = row;
					parameterTable.setHTML(documentRow, 0, "<b>" +
							Main.get().workspaceUserProperties.getWorkspace().getWorkflowProcessIntanceVariablePath() +
							"</b>");

					repositoryService.getPathByUUID(value, new AsyncCallback<String>() {
						@Override
						public void onSuccess(final String docPath) {
							// Validating if is document / folder / mail and displaying object path
							documentService.isValid(docPath, new AsyncCallback<Boolean>() {
								public void onSuccess(Boolean result) {
									if (result.booleanValue()) {
										writePath(documentRow, docPath, false);
									} else {
										folderService.isValid(docPath, new AsyncCallback<Boolean>() {
											public void onSuccess(Boolean result) {
												if (result.booleanValue()) {
													writePath(documentRow, docPath, true);
												} else {
													// must be a mail object
													writePath(documentRow, docPath, false);
												}
											}

											@Override
											public void onFailure(Throwable caught) {
												Main.get().showError("isValid", caught);
											}
										});
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									Main.get().showError("isValid", caught);
								}
							});
						}

						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("getPathByUUID", caught);
						}
					});

				} else {
					// parameterTable.setHTML(row, 0, "<b>" + key + "</b>");
					// parameterTable.setHTML(row, 1, value);
				}
			}
		}

		// Print comments
		for (Iterator<GWTWorkflowComment> it = processInstance.getRootToken().getComments().iterator(); it.hasNext(); ) {
			writeComment(it.next());
		}

		writeAddComment();

		table.setHTML(12, 1, "" + processDefinition.getId());
		table.setHTML(13, 1, processDefinition.getName());
		table.setHTML(14, 1, "" + processDefinition.getVersion());

		if (processDefinition.getDescription() != null) {
			table.setHTML(16, 1, processDefinition.getDescription());
		}

		getProcessDefinitionForms(processDefinition.getId());
		startTaskInstance(taskInstance.getId());
	}

	/**
	 * clearPanel
	 */
	private void clearPanel() {
		table.setHTML(1, 1, "");
		table.setHTML(2, 1, "");
		table.setHTML(3, 1, "");
		table.setHTML(4, 1, "");
		table.setHTML(5, 1, "");
		table.setHTML(6, 1, "");
		table.setHTML(8, 1, "");
		table.setHTML(9, 1, "");
		table.setHTML(10, 1, "");
		table.setHTML(12, 1, "");
		table.setHTML(13, 1, "");
		table.setHTML(14, 1, "");
		table.setHTML(15, 1, "");
		documentLink = null;
		textArea.setText("");
		removeAllParametersTableRows();
		removeAllCommentsTableRows();
		manager.getTable().setVisible(false);
	}

	/**
	 * Get process definitions callback
	 */
	final AsyncCallback<Map<String, List<GWTFormElement>>> callbackGetProcessDefinitionForms = new AsyncCallback<Map<String, List<GWTFormElement>>>() {
		public void onSuccess(Map<String, List<GWTFormElement>> result) {
			if (result.containsKey(taskInstance.getName())) {
				manager.setFormElements(result.get(taskInstance.getName()));
				manager.loadDataFromWorkflowVariables(taskInstance.getProcessInstance().getVariables());
				manager.getTable().setVisible(true);
				drawForm();
			} else if (taskInstance.getName().contains(":") && result.containsKey(taskInstance.getName().split(":")[0])) {
				manager.setFormElements(result.get(taskInstance.getName().split(":")[0]));
				manager.loadDataFromWorkflowVariables(taskInstance.getProcessInstance().getVariables());
				manager.getTable().setVisible(true);
				drawForm();
			} else {
				manager.setFormElements(new ArrayList<GWTFormElement>());
				manager.getTable().setVisible(false);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getProcessDefinitionForms", caught);
		}
	};

	/**
	 * Start task instance callback
	 */
	final AsyncCallback<Object> callbackStartTaskInstance = new AsyncCallback<Object>() {
		@Override
		public void onSuccess(Object result) {
		}

		@Override
		public void onFailure(Throwable caught) {
			Main.get().showError("startTaskInstance", caught);
		}
	};


	/**
	 * getProcessDefinitionForms
	 */
	public void getProcessDefinitionForms(double id) {
		workflowService.getProcessDefinitionForms(id, callbackGetProcessDefinitionForms);
	}

	/**
	 * Start user task instance 
	 */
	public void startTaskInstance(double id) {
		workflowService.startTaskInstance(id, callbackStartTaskInstance);
	}

	/**
	 * drawForm
	 */
	private void drawForm() {
		// always set form visible
		submitForm.setVisible(true);

		// submitForm is hidden into manager if manager has button definitions
		manager.setSubmitFormButton(submitForm);
		manager.edit();
	}

	/**
	 * Get subscribed documents callback
	 */
	final AsyncCallback<Object> callbackSetTaskInstanceValues = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.dashboard.workflowDashboard.setProcessToExecuteNextTask(taskInstance.getProcessInstance().getId());
			Main.get().mainPanel.dashboard.workflowDashboard.findUserTaskInstances();
			clearPanel();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("setTaskInstanceValues", caught);
		}
	};

	@Override
	public void setTaskInstanceValues(double id, String transitionName) {
		if (manager.hasFileUploadFormElement()) {
			Main.get().fileUpload.enqueueFileToUpload(manager.getFilesToUpload(transitionName));
		} else {
			workflowService.setTaskInstanceValues(id, transitionName, manager.updateFormElementsValuesWithNewer(), callbackSetTaskInstanceValues);
		}
	}

	@Override
	public void setTaskInstanceValues(double id, String transitionName, Collection<FileToUpload> filesToUpload) {
		manager.updateFilesToUpload(filesToUpload);
		workflowService.setTaskInstanceValues(id, transitionName, manager.updateFormElementsValuesWithNewer(), callbackSetTaskInstanceValues);
	}

	/**
	 * removeAllFormTableRows
	 */
	private void removeAllParametersTableRows() {
		// Deletes all table rows
		while (parameterTable.getRowCount() > 0) {
			parameterTable.removeRow(0);
		}
	}

	/**
	 * removeAllCommentsTableRows
	 */
	private void removeAllCommentsTableRows() {
		while (tableNotes.getRowCount() > 0) {
			tableNotes.removeRow(0);
		}
	}

	/**
	 * Writes the note
	 */
	private void writeComment(GWTWorkflowComment comment) {
		int row = tableNotes.getRowCount();
		tableNotes.setHTML(row, 0, "<b>" + comment.getActorId() + "</b>");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableNotes.setHTML(row, 1, dtf.format(comment.getTime()));
		tableNotes.getCellFormatter().setHorizontalAlignment(row, 1, HasAlignment.ALIGN_RIGHT);
		tableNotes.getRowFormatter().setStyleName(row, "okm-Notes-Title");
		tableNotes.getCellFormatter().setHeight(row, 1, "30px");
		tableNotes.getCellFormatter().setVerticalAlignment(row, 0, HasAlignment.ALIGN_BOTTOM);
		tableNotes.getCellFormatter().setVerticalAlignment(row, 1, HasAlignment.ALIGN_BOTTOM);
		row++;
		tableNotes.setHTML(row, 0, "");
		tableNotes.getCellFormatter().setHeight(row, 0, "6px");
		tableNotes.getRowFormatter().setStyleName(row, "okm-Notes-Line");
		tableNotes.getFlexCellFormatter().setColSpan(row, 0, 2);
		row++;
		tableNotes.setHTML(row, 0, comment.getMessage());
		tableNotes.getFlexCellFormatter().setColSpan(row, 0, 2);
	}

	/**
	 * writeAddNote
	 */
	private void writeAddComment() {
		int row = tableNotes.getRowCount();
		tableNotes.setWidget(row, 0, newNotePanel);
		tableNotes.getFlexCellFormatter().setColSpan(row, 0, 2);
		tableNotes.getCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
	}

	/**
	 * Callback addComment workflow
	 */
	final AsyncCallback<Object> callbackAddComment = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			GWTWorkflowComment comment = new GWTWorkflowComment();
			comment.setMessage(textArea.getText());
			comment.setTime(new Date());
			comment.setActorId(Main.get().workspaceUserProperties.getUser().getId());
			taskInstance.getProcessInstance().getRootToken().getComments().add(comment);
			tableNotes.removeRow(tableNotes.getRowCount() - 1); // Deletes last row = addComment
			writeComment(comment);
			writeAddComment();
			textArea.setText("");
			add.setEnabled(false);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("addComment", caught);
		}
	};

	/**
	 * addNote document
	 */
	private void addComment() {
		if (!textArea.getText().equals("")) {
			workflowService.addComment(taskInstance.getProcessInstance().getRootToken().getId(),
					textArea.getText(), callbackAddComment);
		}
	}

	/**
	 * writePath
	 */
	private void writePath(int row, final String docPath, final boolean isFolder) {
		Anchor link = new Anchor();
		link.setText(docPath);
		table.setWidget(10, 1, link);
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isFolder) {
					CommonUI.openPath(Util.getParent(docPath), docPath);
				} else {
					CommonUI.openPath(docPath, "");
				}
			}
		});

		link.setStyleName("okm-Hyperlink");

		// Clones link
		documentLink = new Anchor();
		documentLink.setText(docPath);
		documentLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!isFolder) {
					CommonUI.openPath(Util.getParent(docPath), docPath);
				} else {
					CommonUI.openPath(docPath, "");
				}
			}
		});

		documentLink.setStyleName("okm-Hyperlink");
		parameterTable.setWidget(row, 1, documentLink);
	}

	/**
	 * TitleWidget
	 */
	class TitleWidget extends HorizontalPanel implements HasClickHandlers {
		HTML title;
		Image zoomImage;
		boolean zoom = false;
		int[] relatedRows;

		/**
		 * TitleWidget
		 */
		public TitleWidget(String text, int[] relatedRows) {
			super();
			sinkEvents(Event.ONCLICK);

			title = new HTML("");
			setTitle(text);
			this.relatedRows = relatedRows;

			if (zoom) {
				zoomImage = new Image(OKMBundleResources.INSTANCE.zoomOut());
			} else {
				zoomImage = new Image(OKMBundleResources.INSTANCE.zoomIn());
			}

			zoomImage.setStyleName("okm-Hyperlink");

			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					zoom = !zoom;
					setVisibleRows(zoom);
				}
			});

			add(title);
			add(zoomImage);
			setCellHorizontalAlignment(title, HasAlignment.ALIGN_CENTER);
			setCellHorizontalAlignment(zoomImage, HasAlignment.ALIGN_LEFT);
			setCellWidth(zoomImage, "22");
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String)
		 */
		public void setTitle(String text) {
			title.setHTML("<b>" + text.toUpperCase() + "</b>");
		}

		/**
		 * setVisibleRows
		 *
		 * @param visible
		 */
		public void setVisibleRows(boolean zoom) {
			this.zoom = zoom;
			showRelatedRows(zoom);

			if (zoom) {
				zoomImage.setResource(OKMBundleResources.INSTANCE.zoomOut());
			} else {
				zoomImage.setResource(OKMBundleResources.INSTANCE.zoomIn());
			}
		}

		/**
		 * showRelatedRows
		 *
		 * @param visible
		 */
		private void showRelatedRows(boolean zoom) {
			for (int i = 0; i < relatedRows.length; i++) {
				table.getRowFormatter().setVisible(relatedRows[i], zoom);
			}
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
		 */
		@Override
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			return addHandler(handler, ClickEvent.getType());
		}
	}
	
	@Override
	public void validationWithPluginsFinished(boolean result) {
		// Submit form is not visible when other buttons are declared into form.xml, in these case must not be executed the setTaskInstanceValues
		if (result && submitForm.isVisible()) {
			validationPassed();
		}
	}
	
	/**
	 * validationPassed
	 */
	private void validationPassed() {
		setTaskInstanceValues(taskInstance.getId(), null);
		submitForm.setEnabled(false);
	}
}