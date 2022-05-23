function drawMailEditor4(t_language, t_theme, t_plugins, toolbar1, toolbar2, sendMailText, cancelText, searchDocumentText, searchFolderText, searchImageText) {
  document.getElementById('okm_mail_tinymce').contentWindow.drawMailEditor4(t_language, t_theme, t_plugins, toolbar1, toolbar2, sendMailText, cancelText, searchDocumentText, searchFolderText, searchImageText);
}

function addDocumentToMailEditor(uuid, name) {
  document.getElementById('okm_mail_tinymce').contentWindow.addDocumentToMailEditor(uuid, name);
  return false;
}

function addFolderToMailEditor(uuid, name) {
  document.getElementById('okm_mail_tinymce').contentWindow.addFolderToMailEditor(uuid, name);
  return false;
}

function cancelMailEditor() {
  document.getElementById('okm_mail_tinymce').contentWindow.cancelMailEditor();
}

function loadContent(htmlText) {
  document.getElementById('okm_mail_tinymce').contentWindow.loadContent(htmlText);
}
