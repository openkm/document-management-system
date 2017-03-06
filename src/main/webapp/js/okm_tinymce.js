// Image parameters information at
// http://www.tinymce.com/wiki.php/Configuration:document_base_url
//params = params + ',"relative_urls":"false"';
//params = params + ',"remove_script_host":"true"';
//params = params + ',"document_base_url":"/"';
//params = params + ',"convert_urls": "true"';
function drawHTMLEditor(t_language, t_theme, t_skin, t_skin_variant, t_plugins, t_buttons1, t_buttons2,
		t_buttons3, t_buttons4, checkinText, cancelCheckoutText, searchDocumentText, searchFolderText, searchImageText) {
	var params = '{';
	params = params + '"script_url":"../js/tiny_mce/tiny_mce.js"';
	params = params + ',"language":"' + t_language + '"';
	params = params + ',"theme":"' + t_theme + '"';
	params = params + ',"plugins":"' + t_plugins + '"';
	params = params + ',"theme_advanced_buttons1":"' + t_buttons1 + '"';
	params = params + ',"theme_advanced_buttons2":"' + t_buttons2 + '"';
	params = params + ',"theme_advanced_buttons3":"' + t_buttons3 + '"';
	params = params + ',"theme_advanced_buttons4":"' + t_buttons4 + '"';
	params = params + ',"extended_valid_elements":"pre[id|name|class]"';
	params = params + ',"remove_linebreaks":"true"';
	params = params + ',"theme_advanced_toolbar_location":"top"';
	params = params + ',"theme_advanced_toolbar_align":"left"';
	params = params + ',"theme_advanced_statusbar_location":"bottom"';
	params = params + ',"theme_advanced_resizing":"false"';
	params = params + ',"theme_advanced_resizing_use_cookie":"false"';
	params = params + ',"content_css":"../style/extension/htmlEditor"';
	params = params + ',"template_external_list_url":"lists/template_list.js"';
	params = params + ',"external_link_list_url":"lists/link_list.js"';
	params = params + ',"external_image_list_url":"lists/image_list.js"';
	params = params + ',"media_external_list_url":"lists/media_list.js"';
	params = params + ',"template_replace_values" : {"username" : "Some User","staffid" : "991234"}';
	
	if (t_skin != '') {
		params = params + ',"skin":"' + t_skin + '"';
		
		if (t_skin_variant != '') {
			params = params + ',"skin_variant":"' + t_skin_variant + '"';
		}
	}
	
	params = params +'}'; 
	var json = $.parseJSON(params); // create json object from string value
	var jsonFnObj = {
			setup : function (ed) {
				// Add a custom buttons
				ed.addButton('okm_checkin', {
					title : checkinText,
					image : '../img/tinymce/checkin.gif',
					onclick : function() {
						jsHTMLEditorCheckin(tinyMCE.get('elm1').getContent());
					}
				});
				
				ed.addButton('okm_cancelcheckout', {
					title : cancelCheckoutText,
					image : '../img/tinymce/cancel_checkout.gif',
					onclick : function() {
						confirmCancelCheckout();
					}
				});
				
				ed.addButton('okm_searchDocument', {
					title : searchDocumentText,
					image : '../img/tinymce/document_find.gif',
					onclick : function() {
						jsSearchDocumentHTMLEditorPopup();
					}
				});
				
				ed.addButton('okm_searchFolder', {
					title : searchFolderText,
					image : '../img/tinymce/folder_find.gif',
					onclick : function() {
						jsSearchFolderHTMLEditorPopup();
					}
				});
				
				ed.addButton('okm_searchImage', {
					title : searchImageText,
					image : '../img/tinymce/image_find.png',
					onclick : function() {
						jsSearchImageHTMLEditorPopup();
					}
				});
			}
	};
    
    $.extend(json, jsonFnObj); // Extend json with function objects
    $('textarea.tinymce').tinymce(json);
}

function addDocumentHTMLEditor(uuid, name) {
	var docLink = '<a href="#" onclick="javascript:jsOpenPathByUuid(\''+uuid+'\');">'+name+'</a>';
	tinyMCE.execCommand('mceInsertContent', false, docLink);
	return false;
}

function addFolderHTMLEditor(uuid, name) {
	var docLink = '<a href="#" onclick="javascript:jsOpenPathByUuid(\''+uuid+'\');">'+name+'</a>';
	tinyMCE.execCommand('mceInsertContent', false, docLink);
	return false;
}

function addImageHTMLEditor(src, params) {
	var imageTag = '<img src="'+src+'" '+params+' data-mce-src="'+src+'" />';
	tinyMCE.execCommand('mceInsertContent', false, imageTag);
	return false;
}

function confirmCancelCheckout(text) {
	// Displays an confirm box and an alert message will be displayed depending on what you choose in the confirm
	var text = jsI18n('confirm.cancel.checkout');
	tinyMCE.activeEditor.windowManager.confirm(text, function(s) {
	   if (s) {
		   jsCancelCheckout();
		   jsHideHTMLEditorPopup();
	   }
	});
}