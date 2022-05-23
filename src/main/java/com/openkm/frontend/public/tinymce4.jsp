<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <!-- Load jQuery -->
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>

  <!-- Load TinyMCE -->
  <script type="text/javascript" src="../js/tinymce4/tinymce.min.js"></script>
  <script type="text/javascript" src="../js/tinymce4/jquery.tinymce.min.js"></script>
</head>
<body style="border: none; margin: 0; padding: 0">
	<textarea id="elm1"></textarea>
	<script type="text/javascript">
        function drawHTMLEditor(t_language, t_theme, t_plugins, t_toolbar1, t_toolbar2, checkinText, cancelCheckoutText,
                searchDocumentText, searchFolderText, searchImageText, content) {
            var params = '{';
            params = params + '"language":"' + t_language + '"';
            params = params + ',"theme":"' + t_theme + '"';
            params = params + ',"plugins":"' + t_plugins + '"';
            params = params + ',"toolbar1":"' + t_toolbar1 + '"';
            params = params + ',"toolbar2":"' + t_toolbar2 + '"';
            params = params + ',"t_image_advtab":"true"';
            params = params + ',"theme_advanced_resizing":"false"';
            params = params + ',"extended_valid_elements":"a[onclick|href|id|name|class],pre[id|name|class|target]"';
            params = params + ',"spellchecker_rpc_url":"../../../../SpellChecker"';
            params = params + ',"spellchecker_languages":"Default=en"';
            params = params + '}';
            var json = $.parseJSON(params); // create json object from string value
            var jsonFnObj = {
                setup : function(ed) {
                    // Add a custom buttons
                    ed.addButton('okm_checkin', {
                        title : checkinText,
                        image : '../img/tinymce4/checkin.gif',
                        onclick : function() {
                            parent.jsHTMLEditorCheckin(getContentHTMLEditor());
                        }
                    });
                    ed.addButton('okm_cancelcheckout', {
                        title : cancelCheckoutText,
                        image : '../img/tinymce4/cancel_checkout.gif',
                        onclick : function() {
                            confirmCancelCheckout();
                        }
                    });
                    ed.addButton('okm_searchDocument', {
                        title : searchDocumentText,
                        image : '../img/tinymce4/document_find.gif',
                        onclick : function() {
                            parent.jsSearchDocumentHTMLEditorPopup();
                        }
                    });
                    ed.addButton('okm_searchFolder', {
                        title : searchFolderText,
                        image : '../img/tinymce4/folder_find.gif',
                        onclick : function() {
                            parent.jsSearchFolderHTMLEditorPopup();
                        }
                    });
                    ed.addButton('okm_searchImage', {
                        title : searchImageText,
                        image : '../img/tinymce4/image_find.png',
                        onclick : function() {
                            parent.jsSearchImageHTMLEditorPopup();
                        }
                    });
                    ed.addButton('okm_codeHighlight', {
                        title : 'Source Code',
                        image : '../img/tinymce4/codehighlight.png',
                        onclick : function() {
                            var contentCode = getContentCode(ed);
                            // Opens a HTML page inside a TinyMCE dialog
                            ed.windowManager.open({
                                title : "Code",
                                body : [ {
                                    type : "textbox",
                                    name : "code",
                                    value : contentCode,
                                    style : "direction: ltr; text-align: left; font-size: 11px; font-family: monospace;",
                                    multiline : !0,
                                    minHeight : 450,
                                    spellcheck: !1
                                } ],
                                width : 600,
                                height : 500,
                                onsubmit: function(e) {
                                    var contentCode = getContentCode(ed);
                                    if (contentCode == null || contentCode.length == 0) {
                                      //tinyMCE.execCommand('mceInsertContent', false, '<pre><code>' + HTMLEncode(e.data.code) + '</code></pre>');
                                      tinyMCE.get('elm1').setContent(getContentToSet(ed, e.data.code));
                                    } else {
                                      tinyMCE.get('elm1').setContent(getContentUpdated(ed, e.data.code));
                                    }
                                }
                            });
                        }
                    });
                }
            };

            $.extend(json, jsonFnObj); // Extend json with function objects
            $('#elm1').text(content); // Use .text() not .html() to prevent conversion &lt; to >

            // resize textarea
            $("#elm1").width($(window).width());
            $("#elm1").height($(window).height() - 140);
            $('#elm1').tinymce(json);
        }

        // When document is ready then can be drawn tiny mce
        $(document).ready(function() {
            parent.jsDrawHTMLEditor();
        });

        function addDocumentHTMLEditor(uuid, name) {
            var docLink = '<a href="#" onclick="javascript:jsOpenPathByUuid(\'' + uuid + '\');">' + name + '</a>';
            tinyMCE.execCommand('mceInsertContent', false, docLink);
            return false;
        }

        function addFolderHTMLEditor(uuid, name) {
            var docLink = '<a href="#" onclick="javascript:jsOpenPathByUuid(\'' + uuid + '\');">' + name + '</a>';
            tinyMCE.execCommand('mceInsertContent', false, docLink);
            return false;
        }

        function addImageHTMLEditor(src, params) {
            var imageTag = '<img src="'+src+'" '+params+' data-mce-src="'+src+'" />';
            tinyMCE.execCommand('mceInsertContent', false, imageTag);
            return false;
        }

        function confirmCancelCheckout() {
            // Displays an confirm box and an alert message will be displayed depending on what you choose in the confirm
            var text = parent.jsI18n('confirm.cancel.checkout');
            tinyMCE.activeEditor.windowManager.confirm(text, function(s) {
                if (s) {
                    parent.jsCancelCheckout();
                }
            });
        }

        function getContentHTMLEditor() {
            return tinyMCE.get('elm1').getContent();
        }

        function setContentHTMLEditor(content) {
            return tinyMCE.get('elm1').setContent(content);
        }

        // More information at http://blog.squadedit.com/tinymce-and-cursor-position/
        function getCursorPosition(editor) {
            //set a bookmark so we can return to the current position after we reset the content later
            var bm = editor.selection.getBookmark(0);

            //select the bookmark element
            var selector = "[data-mce-type=bookmark]";
            var bmElements = editor.dom.select(selector);

            //put the cursor in front of that element
            editor.selection.select(bmElements[0]);
            editor.selection.collapse();

            //add in my special span to get the index...
            //we won't be able to use the bookmark element for this because each browser will put id and class attributes in different orders.
            var elementID = "######cursor######";
            var positionString = '<span id="'+elementID+'"></span>';
            editor.selection.setContent(positionString);

            //get the content with the special span but without the bookmark meta tag
            var content = editor.getContent({format: "html"});
            //find the index of the span we placed earlier
            var index = content.indexOf(positionString);

            //remove my special span from the content
            editor.dom.remove(elementID, false);

            //move back to the bookmark
            editor.selection.moveToBookmark(bm);

            return index;
    	}

        function getContentCode(editor) {
            var cursorPosition = getCursorPosition(editor);
            var content = tinyMCE.get('elm1').getContent();
            var leftContent = content.substring(0, cursorPosition);
            var rightContent = content.substring(cursorPosition, content.length);
            var leftPostTagStartCode = leftContent.lastIndexOf('<pre><code>');
            var leftPostTagEndCode = leftContent.lastIndexOf('</code></pre>');
            var rightPostTagStartCode = rightContent.indexOf('<pre><code>');
            var rightPostTagEndCode = rightContent.indexOf('</code></pre>');
            var contentCode = '';
            if (leftPostTagStartCode>=0 && leftPostTagStartCode>leftPostTagEndCode
                    && rightPostTagEndCode>=0 && (rightPostTagEndCode<rightPostTagStartCode || rightPostTagStartCode==-1)) {
                contentCode = leftContent.substring(leftPostTagStartCode+11,leftContent.length);
                contentCode += rightContent.substring(0, rightPostTagEndCode);
            }
            return HTMLDecode(contentCode);
        }

        function getContentUpdated(editor, newContentCode) {
            var cursorPosition = getCursorPosition(editor);
            var content = tinyMCE.get('elm1').getContent();
            var leftContent = content.substring(0, cursorPosition);
            var rightContent = content.substring(cursorPosition, content.length);
            var leftPostTagStartCode = leftContent.lastIndexOf('<pre><code>');
            var rightPostTagEndCode = rightContent.indexOf('</code></pre>');
            content = leftContent.substring(0, leftPostTagStartCode+11) + HTMLEncode(newContentCode) + rightContent.substring(rightPostTagEndCode, rightContent.length);
            return content;
        }

        function getContentToSet(editor, newContentCode) {
            var cursorPosition = getCursorPosition(editor);
            var content = tinyMCE.get('elm1').getContent();
            var leftContent = content.substring(0, cursorPosition);
            var rightContent = content.substring(cursorPosition, content.length);
            content = leftContent + '<pre><code>' + HTMLEncode(newContentCode) + '</code></pre>' + rightContent;
            return content;
        }

        function HTMLDecode(str) {
            var el = document.createElement("div");
            el.innerHTML = str;
            str = el.textContent || el.innerText;
            el = null;
         	return str;
        }

        function HTMLEncode(str) {
            var el = document.createElement("div");
            el.innerText = el.textContent = str;
            str = el.innerHTML;
            el = null;
            return str.replace(/<br\s*\/?>/ig, "\r\n");
        }
    </script>
</body>
</html>
