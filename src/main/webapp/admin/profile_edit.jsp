<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/jqTabs.css" />
  <link rel="stylesheet" type="text/css" href="../css/chosen.css" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <script type="text/javascript" src="js/jqTabs.js" ></script>
  <script type="text/javascript" src="../js/chosen.jquery.js" ></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $('#scroll').height($(window).height() - 21);
      $('select#prf_misc_web_skin').chosen({disable_search_threshold: 10});
      $('select#prf_misc_extensions').chosen();
      $('select#prf_misc_reports').chosen();
      $('select#prf_misc_workflows').chosen();      
      
      $('select#prf_wizard_property_groups').chosen();
      $('select#prf_wizard_workflows').chosen();
      
      $('select#prf_tab_default').chosen({disable_search_threshold: 10});      
      
      $('select#prf_filebrowser_column0').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column1').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column2').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column3').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column4').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column5').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column6').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column7').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column8').chosen({disable_search_threshold: 10, allow_single_deselect: true});
      $('select#prf_filebrowser_column9').chosen({disable_search_threshold: 10, allow_single_deselect: true});

      // Needed at end because of the chosen plugin
      TABS.init('ul.tabs');
	});
  </script>
  <title>User Profile</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Profile">User profiles</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create profile</c:when>
            <c:when test="${action == 'edit'}">Edit profile</c:when>
            <c:when test="${action == 'delete'}">Delete profile</c:when>
            <c:when test="${action == 'clone'}">Clone profile</c:when>
          </c:choose>
        </li>
      </ul>      
      <br/>
      <div id="scroll" style="width: 100%; height: 100%; overflow: auto;">
      <form action="Profile">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="prf_id" value="${prf.id}"/>
        <table width="765px" border="0" style="margin-left: auto; margin-right: auto; margin-bottom: -3px;">
          <tr>
            <td>
              <ul class='tabs'>
                <li><a href='#general'>General</a></li>
                <li><a href='#components'>Components</a></li>
			    <li><a href='#menu'>Menu</a></li>
			    <li><a href='#other'>Other</a></li>
		      </ul>
		    </td>
		  </tr>
		</table>
		
        <div id='general'>
          <table class="form" width="765px" border="0">
            <tr>
              <td valign="top" width="50%">
                <!-- GENERAL -->
                <jsp:include page="profile_general.jsp"/>
                <!-- MISC -->
                <jsp:include page="profile_misc.jsp"/>
              </td>
              <td valign="top">
                <!-- CHAT -->
                <jsp:include page="profile_chat.jsp"/>
                <!-- WIZARD -->
                <jsp:include page="profile_wizard.jsp"/>
                <!-- PAGINATION -->
                <jsp:include page="profile_pagination.jsp"/>
              </td>
            </tr>
            <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
                <c:when test="${action == 'clone'}"><input type="submit" value="Clone" class="yesButton"/></c:when>
              </c:choose>
            </td>
          </tr>
          </table>
        </div>
        
        <div id='components'>
          <table class="form" width="765px" border="0">
            <tr>
              <td valign="top" width="33%">
                <!-- STACKS -->
                <jsp:include page="profile_stacks.jsp"/>
                <!-- DASHBOARD -->
                <jsp:include page="profile_dashboard.jsp"/>
              </td>
              <td valign="top" width="33%">
                <!-- TAB -->
                <jsp:include page="profile_tab.jsp"/>
                <!-- TAB DOCUMENT -->
                <jsp:include page="profile_tab_document.jsp"/>
              </td>
              <td valign="top">
                <!-- TAB FOLDER -->
                <jsp:include page="profile_tab_folder.jsp"/>
                <!-- TAB MAIL -->
                <jsp:include page="profile_tab_mail.jsp"/>
              </td>
            </tr>
            <tr>
            <td colspan="3" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
                <c:when test="${action == 'clone'}"><input type="submit" value="Clone" class="yesButton"/></c:when>
              </c:choose>
            </td>
          </tr>
          </table>
        </div>
        
        <div id='menu'>
          <table class="form" width="765px" border="0">
            <tr>
              <td valign="top" width="33%">
                <!-- MENU -->
                <jsp:include page="profile_menu.jsp"/>
                <!-- MENU BOOKMARK -->
                <jsp:include page="profile_menu_bookmark.jsp"/>
                <!-- MENU TOOL -->
                <jsp:include page="profile_menu_tool.jsp"/>
                <!-- MENU HELP -->
                <jsp:include page="profile_menu_help.jsp"/>
              </td>
              <td valign="top" width="33%">
                <!-- MENU FILE -->
                <jsp:include page="profile_menu_file.jsp"/>
              </td>
              <td valign="top">
                <!-- MENU EDIT -->
                <jsp:include page="profile_menu_edit.jsp"/>
              </td>
            </tr>
            <tr>
            <td colspan="3" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
                <c:when test="${action == 'clone'}"><input type="submit" value="Clone" class="yesButton"/></c:when>
              </c:choose>
            </td>
          </tr>
          </table>
        </div>
        
        <div id='other'>
          <table class="form" width="765px" border="0">
            <tr>
              <td valign="top" width="50%">
                <!-- TOOLBAR -->
                <jsp:include page="profile_toolbar.jsp"/>
              </td>
              <td valign="top">
                <!-- FILE BROWSER -->
                <jsp:include page="profile_filebrowser.jsp"/>
              </td>
            </tr>
            <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
                <c:when test="${action == 'clone'}"><input type="submit" value="Clone" class="yesButton"/></c:when>
              </c:choose>
            </td>
          </tr>
          </table>
        </div>
      </form>
      </div>	
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>