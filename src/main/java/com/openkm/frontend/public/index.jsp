<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ page errorPage="/general-error.jsp" %>
<%
	HttpSessionManager.getInstance().add(request);
	com.openkm.api.OKMAuth.getInstance().login();
	
	Cookie cookie = new Cookie("ctx", request.getContextPath());
	cookie.setMaxAge(365 * 24 * 60 * 60); // One year
	response.addCookie(cookie);
%>
<!DOCTYPE html>
	<head>
		<title><%=Config.TEXT_TITLE%></title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
    	<meta http-equiv="Pragma" content="no-cache" />
    	<meta http-equiv="Expires" content="0" />
		<link rel="Shortcut icon" href="../logo/favicon">
		<link type="text/css" rel="stylesheet" href="styles.css" title="default">
		<link type="text/css" rel="alternate stylesheet" href="mediumfont.css" title="mediumfont">
		<link type="text/css" rel="alternate stylesheet" href="bigfont.css" title="bigfont">
		<script type="text/javascript" src="../js/swfobject/swfobject.js"></script>
		<script type="text/javascript" src="../js/flexpaper/flexpaper_flash.js"></script>
		<script type="text/javascript" src="../js/digitalsignature/digitalsignature.js"></script>
    	
		<!-- Load TinyMCE -->
		<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
		<script type="text/javascript" src="../js/tiny_mce/jquery.tinymce.js"></script>
		<script type="text/javascript" src="../js/okm_tinymce.js"></script>
    	
		<!-- /TinyMCE -->
		<script type="text/javascript">
			// Disable contextual menus in NS 4.
			function right(e) {
				if (navigator.appName == 'Netscape' && (e.which == 3 || e.which == 2)) {
					return false;
				}
				return true;
			}
			
			// Disable contextual menus in NS 6 and IE.
			function context() {
				return false;
			}
			
			document.oncontextmenu = function() { return false; };
			
			function changeCss(title) {
				var i, a;
				
				for (i=0; (a = document.getElementsByTagName("link")[i]); i++) {
					if (a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title")) {
						a.disabled = true;
						if (a.getAttribute("title") == title) a.disabled = false;
					}
				}
			}
			
	   		function printFile() {
	   			window.frames['__print'].focus(); 
	   			parent['__print'].print();
	   		}
			
			document.onmousedown = right;
			document.oncontextmenu = context;
			
			// Workaround for Applets and Firefox with recent JDKs
			function jsGetActualPathFix() {				
			    return jsGetActualPath();
			}
			
			function refreshFolderFix() {
				return refreshFolder();
			}
			
			function cryptographyLoadedFix() {
				return cryptographyLoaded();
			}
			
			function destroyScannerAppletFix() {
				return destroyScannerApplet();
			}
			
			function destroyUploaderAppletFix() {
				return destroyUploaderApplet();
			}
			
			function jsWizardFix(docPath, response) {
				return jsWizard(docPath, response);
			}
		</script>
	</head>
	<body>
		<script type="text/javascript" src="frontend.nocache.js"></script>
		<div style="width:0; height:0; border:0; position:absolute; top:1px; left:1px;">
			<div id="scannerApplet"></div>
			<div id="uploaderApplet"></div>
			<div id="digitalSignatureApplet"></div>
			<div id="cryptoApplet"></div>
			<div id="editorApplet"></div>
		</div>
		<!-- Download & Print documents needs this -->
		<div style="display:none; visibility:hidden;">
			<iframe src="" name="__download" id="__download" style="width:0; height:0; border:0;"></iframe>
			<iframe src="" name="__print" id="__print" style="width:0; height:0; border:0;"></iframe>
		</div>
		<div style="display:none;" id="screenGrayBackground"></div>
	</body>
</html>