<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <title>Configuration</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="Config">Configuration</a></li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create configuration</c:when>
            <c:when test="${action == 'edit'}">Edit configuration</c:when>
            <c:when test="${action == 'delete'}">Delete configuration</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Config" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="filter" value="${filter}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <table class="form" width="425px">
          <tr>
            <td nowrap="nowrap">Key</td>
            <td>
              <c:choose>
                <c:when test="${action != 'create'}">
                  <input size="50" class=":required :only_on_blur" name="cfg_key" value="${cfg.key}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input size="50" class=":required :only_on_blur" name="cfg_key" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Type</td>
            <td>
              <!-- http://stackoverflow.com/questions/368813/readonly-select-tag -->
              <select name="cfg_type" onfocus="this.defaultIndex=this.selectedIndex;" onchange="this.selectedIndex=this.defaultIndex;">
                <c:forEach var="type" items="${types}">
                  <c:choose>
                    <c:when test="${cfg.type == type.key}">
                      <option value="${type.key}" selected="selected">${type.value}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${type.key}">${type.value}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td>Value</td>
            <td>
              <c:choose>
                <c:when test="${cfg.type == 'string' || cfg.type == 'integer' || cfg.type == 'long'}">
                  <input size="50" name="cfg_value" value="${cfg.value}"/>
                </c:when>
                <c:when test="${cfg.type == 'boolean'}">
                  <c:choose>
                    <c:when test="${cfg.value == 'true'}">
                      <input name="cfg_value" type="checkbox" checked="checked"/>
                    </c:when>
                    <c:otherwise>
                      <input name="cfg_value" type="checkbox"/>
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:when test="${cfg.type == 'file'}">
                  <c:choose>
                    <c:when test="${action == 'create'}">
                      <input class=":required :only_on_blur" type="file" name="file"/>
                    </c:when>
                    <c:otherwise>
                      <c:url value="Config" var="urlView">
                        <c:param name="action" value="view"/>
                        <c:param name="cfg_key" value="${cfg.key}"/>
                      </c:url>
                      <table cellpadding="0" cellspacing="0"><tr><td><img src="${urlView}"/></td></tr><tr><td><input type="file" name="image"/></td></tr></table>
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:when test="${cfg.type == 'select'}">
                  <u:configStoredSelect key="${cfg.key}" value="${cfg.value}"/>
                </c:when>
                <c:otherwise>
                  <textarea rows="5" cols="50" name="cfg_value">${cfg.value}</textarea>
                </c:otherwise>
              </c:choose>
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