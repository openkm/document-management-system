<%@page import="com.openkm.api.OKMDocument"%>
<%@page import="com.openkm.api.OKMRepository"%>
<%@page import="com.openkm.bean.Repository"%>
<%@page import="com.openkm.spring.PrincipalUtils"%>
<%@page import="org.apache.commons.io.IOUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
<textarea id="elm1">
  <%
    String templatePath = "/" + Repository.PERSONAL + "/" + PrincipalUtils.getUser() + "/signature.html";
    if (OKMRepository.getInstance().hasNode(null, templatePath)) {
      String template = "<br/><br/>" + IOUtils.toString(OKMDocument.getInstance().getContent(null, templatePath, false));
      out.write(template);
    }
  %>
  </textarea>
<script type="text/javascript">
  function drawMailEditor4(t_language, t_theme, t_plugins, t_toolbar1, t_toolbar2, sendMailText, cancelText, searchDocumentText, searchFolderText, searchImageText) {
    var params = '{';
    params = params + '"language":"' + t_language + '"';
    params = params + ',"theme":"' + t_theme + '"';
    params = params + ',"menubar":"false"';
    params = params + ',"statusbar":"true"';
    params = params + ',"plugins":"' + t_plugins + '"';
    params = params + ',"toolbar1":"' + t_toolbar1 + '"';
    params = params + ',"toolbar2":"' + t_toolbar2 + '"';
    //params = params + ',"extended_valid_elements":"pre[id|name|class]"';
    params = params + ',"extended_valid_elements":"a[onclick|href|id|name|class],pre[id|name|class]"';
    params = params + ',"t_image_advtab":"true"';
    params = params + ',"theme_advanced_resizing":"false"';
    params = params + ',"spellchecker_rpc_url":"../../../../SpellChecker"';
    params = params + ',"spellchecker_languages":"Default=en"';
    params = params + '}';

    var json = $.parseJSON(params); // create json object from string value
    var jsonFnObj = {
      setup: function (ed) {
        // Add a custom buttons
        ed.addButton('okm_sendMail', {
          title: sendMailText,
          image: '../img/tinymce4/email_go.png',
          onclick: function () {
            parent.jsSendMail(getContent());
          }
        });

        ed.addButton('okm_cancelSendMail', {
          title: cancelText,
          image: '../img/tinymce4/cancel.png',
          onclick: function () {
            cancelMailEditor();
          }
        });

        ed.addButton('okm_searchDocument', {
          title: searchDocumentText,
          image: '../img/tinymce4/document_find.gif',
          onclick: function () {
            parent.jsSearchDocumentPopup('3');
          }
        });

        ed.addButton('okm_searchFolder', {
          title: searchFolderText,
          image: '../img/tinymce4/folder_find.gif',
          onclick: function () {
            parent.jsSearchFolderPopup('2');
          }
        });
      }
    };

    $.extend(json, jsonFnObj); // Extend json with function objects
    // resize textarea
    $("#elm1").width($(window).width());
    //$("#elm1").height($(window).height() - 140); --> 140 is the size of menubar
    $("#elm1").height($(window).height() - 75);
    $('#elm1').tinymce(json);
  }

  // When document is ready then can be drawn tiny mce
  $(document).ready(function () {
    parent.jsDrawMailEditor4();
  });

  function loadContent(htmlText) {
    tinyMCE.activeEditor.setContent(htmlText);
  }

  function addDocumentToMailEditor(uuid, name) {
    var docLink = '<a href="#" onclick="javascript:parent.jsOpenPathByUuid(\'' + uuid + '\');">' + name + '</a>';
    tinyMCE.execCommand('mceInsertContent', false, docLink);
    return false;
  }

  function addFolderToMailEditor(uuid, name) {
    var docLink = '<a href="#" onclick="javascript:parent.jsOpenPathByUuid(\'' + uuid + '\');">' + name + '</a>';
    tinyMCE.execCommand('mceInsertContent', false, docLink);
    return false;
  }

  function addImageToMailEditor(src, params) {
    var imageTag = '<img src="' + src + '" ' + params + ' data-mce-src="' + src + '" />';
    tinyMCE.execCommand('mceInsertContent', false, imageTag);
    return false;
  }

  function cancelMailEditor(text) {
    // Displays an confirm box and an alert message will be displayed depending on what you choose in the confirm
    var text = parent.jsI18n('maileditor.cancel');
    tinyMCE.activeEditor.windowManager.confirm(text, function (s) {
      if (s) {
        parent.jsHideMailEditorPopup();
      }
    });
  }

  function getContent() {
    return tinyMCE.get('elm1').getContent();
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
    var positionString = '<span id="' + elementID + '"></span>';
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
    if (leftPostTagStartCode >= 0 && leftPostTagStartCode > leftPostTagEndCode
      && rightPostTagEndCode >= 0 && (rightPostTagEndCode < rightPostTagStartCode || rightPostTagStartCode == -1)) {
      contentCode = leftContent.substring(leftPostTagStartCode + 11, leftContent.length);
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
    content = leftContent.substring(0, leftPostTagStartCode + 11) + HTMLEncode(newContentCode) + rightContent.substring(rightPostTagEndCode, rightContent.length);
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
    return str;
  }
</script>
</body>
</html>
