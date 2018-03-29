<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/jquery-ui-1.10.3/jquery-ui-1.10.3.css" /> 
  <link rel="stylesheet" type="text/css" href="../css/chosen.css"/>
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script src="../js/jquery-ui-1.10.3/jquery-ui-1.10.3.js" type="text/javascript"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js"></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $('select#pluginToSelect').chosen({disable_search_threshold: 20});
      
      $('#pluginToSelect').change(function () {
          $('#pluginForm').submit();
      });
    
    });
  </script>
  <title>Plugin list</title>
</head>
<body>
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">Plugin list</li>
      </ul>
      <br/>
        <table class="results" width="70%">
          <thead>
            <tr class="header">
              <td align="right" colspan="3">
                <form action="Plugin?action=pluginList" name="pluginForm" id="pluginForm" method="get" class="pluginListSelect">
                  <b>Plugins</b>
                  <select name="pluginToSelect" id="pluginToSelect" style="width: 350px" data-placeholder="&nbsp;">
                    <c:forEach var="plugin" items="${pluginList}" varStatus="row">
                      <c:choose>
                        <c:when test="${plugin == pluginSelected}">
                          <option value="${plugin}" selected="selected">${plugin}</option>
                        </c:when>
                        <c:otherwise>
                          <option value="${plugin}">${plugin}</option>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>
                  </select>
                </form>
                <c:if test="${showReloadButton}">
                  <div class="action pluginListReloadButton">
                    <a href="Plugin?action=reloadRegisteredList&pluginToSelect=${pluginSelected}">
                      <img src="img/action/refresh.png" alt="Reload plugins" title="Reload plugins" style="vertical-align: middle;"/>
                    </a>
                  </div>
                </c:if>
              </td>
            </tr>
            <tr>
              <th width="35%">Name</th>
              <th width="60%">Class name</th>
              <th width="5%">Status</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="plugin" items="${plugins}" varStatus="row">
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td class="${plugin.isLoaded() ? '' : 'pluginNotLoaded'}" title="${plugin.isLoaded() ? '' : 'Plugin not loaded'}">
                  ${plugin.getPlugin().getClass().simpleName}
                </td>
                <td class="${plugin.isLoaded() ? '' : 'pluginNotLoaded'}" title="${plugin.isLoaded() ? '' : 'Plugin not loaded'}">
                  ${plugin.getPlugin().getClass().name}
                </td>
                <td align="center">
                  <c:if test="${plugin.isLoaded()}">
                    <c:set var="found" value="true"/>
                    <c:set var="url" value="Plugin?action=changeStatus&className=${plugin.getPlugin().getClass().name}&pluginToSelect=${pluginSelected}"/>
                    <c:forEach var="pluginstatus" items="${pluginStatus}" varStatus="row">
                      <c:if test="${pluginstatus.className == plugin.getPlugin().getClass().name && !pluginstatus.active}">
                        <c:set var="found" value="false"/>
                      </c:if>
                    </c:forEach>
                    <c:choose>
                      <c:when test="${found}">
                        <a href="${url}"><img src="img/true.png" alt="Active" title="Active"/> </a>
                      </c:when>
                      <c:otherwise>
                        <a href="${url}"><img src="img/false.png" alt="Inactive" title="Inactive"/> </a>
                      </c:otherwise>
                    </c:choose>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>

    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>  
</body>
</html>