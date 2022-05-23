function drawHTMLEditor(t_language, t_theme, t_plugins, t_toolbar1, t_toolbar2, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText, content) {
    document.getElementById('htmlEditor').contentWindow.drawHTMLEditor(t_language, t_theme, t_plugins, t_toolbar1, t_toolbar2, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText, content);
}

function confirmCancelCheckout() {
    document.getElementById('htmlEditor').contentWindow.confirmCancelCheckout();
}

function addDocumentHTMLEditor(uuid, name) {
    document.getElementById('htmlEditor').contentWindow.addDocumentHTMLEditor(uuid, name);
    return false;
}

function addFolderHTMLEditor(uuid, name) {
    document.getElementById('htmlEditor').contentWindow.addFolderHTMLEditor(uuid, name);
    return false;
}

function addImageHTMLEditor(src, params) {
    document.getElementById('htmlEditor').contentWindow.addImageHTMLEditor(src, params);
    return false;
}
