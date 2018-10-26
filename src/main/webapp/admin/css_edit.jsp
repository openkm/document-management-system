<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.dao.bean.Css" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
  <link rel="stylesheet" type="text/css" href="../css/chosen.css" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <style type="text/css">
  .CodeMirror { width: 700px; height: 350px; background-color: #f8f6c2; }
  .activeline { background: #f0fcff !important; }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/addon/selection/active-line.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/css/css.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      var cm = CodeMirror.fromTextArea(document.getElementById('css_content'), {
        lineNumbers : true,
        matchBrackets : true,
        indentUnit : 4,
        mode : "text/css",
        onCursorActivity : function() {
          cm.setLineClass(hlLine, null);
          hlLine = cm.setLineClass(cm.getCursor().line, "activeline");
        }
      });
  
      var width = $(window).width() - 60;
      var height = $(window).height() - 300;
      cm.setSize(width, height);
  
      $('select#css_context').chosen({
        disable_search_threshold : 10
      });
    });
  </script>
  <title>Edit css</title>
</head>
<body>
  <u:constantsMap className="com.openkm.dao.bean.Css" var="Css"/>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">
          <a href="Css">CSS list</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create CSS</c:when>
            <c:when test="${action == 'edit'}">Edit CSS</c:when>
            <c:when test="${action == 'delete'}">Delete CSS</c:when>
          </c:choose>
        </li>
        </ul>
        <br/>
        <form action="Css" method="post" enctype="multipart/form-data">
          <input type="hidden" name="action" value="${action}"/>
          <input type="hidden" name="persist" value="${persist}"/>
          <input type="hidden" name="css_id" value="${css.id}"/>
          <table class="form" width="372px">
            <tr>
              <td>Name</td>
              <td width="95%"><input class=":required :only_on_blur" name="css_name" value="${css.name}"/></td>
            </tr>
            <tr>
              <td>Context</td>
              <td width="95%">
                <c:set var="frontend">${Css.CONTEXT_FRONTEND}</c:set>
                <c:set var="administration">${Css.CONTEXT_ADMINISTRATION}</c:set>
                <c:set var="extension">${Css.CONTEXT_EXTENSION}</c:set>
                <select name="css_context" id="css_context" style="width: 100px">
                  <c:choose>
                    <c:when test="${css.context == frontend}">
                      <option value="${Css.CONTEXT_FRONTEND}" selected="selected">${Css.CONTEXT_FRONTEND}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${Css.CONTEXT_FRONTEND}">${Css.CONTEXT_FRONTEND}</option>
                    </c:otherwise>
                  </c:choose>
                  <%--
                  <c:choose>
                    <c:when test="${css.context == administration}">
                      <option value="<%=Css.CONTEXT_ADMINISTRATION%>" selected="selected"><%=Css.CONTEXT_ADMINISTRATION%></option>
                    </c:when>
                    <c:otherwise>
                      <option value="<%=Css.CONTEXT_ADMINISTRATION%>"><%=Css.CONTEXT_ADMINISTRATION%></option>
                    </c:otherwise>
                  </c:choose>
                  --%>
                  <c:choose>
                    <c:when test="${css.context == extension}">
                      <option value="${Css.CONTEXT_EXTENSION}" selected="selected">${Css.CONTEXT_EXTENSION}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${Css.CONTEXT_EXTENSION}">${Css.CONTEXT_EXTENSION}</option>
                    </c:otherwise>
                  </c:choose>
                </select>
              </td>
            </tr>
            <tr>
              <td>Active</td>
              <td width="95%">
                <c:choose>
                  <c:when test="${css.active}">
                    <input name="css_active" type="checkbox" checked="checked"/>
                  </c:when>
                  <c:otherwise>
                    <input name="css_active" type="checkbox"/>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <textarea cols="80" rows="25" name="css_content" id="css_content">${css.content}</textarea>
              </td>
            </tr>
            <tr>
              <td colspan="2" align="right">
                <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
                <c:choose>
                  <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                  <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                  <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
                </c:choose>
              </td>
            </tr>
          </table>
        </form>
      </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>