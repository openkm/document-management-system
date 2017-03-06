<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <title>Mail filter rule</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="MailAccount" var="urlMailAccountList">
        <c:param name="ma_user" value="${ma_user}"/>
      </c:url>
      <c:url value="MailAccount" var="urlMailFilterList">
        <c:param name="action" value="filterList"/>
        <c:param name="ma_user" value="${ma_user}"/>
        <c:param name="ma_id" value="${ma_id}"/>
      </c:url>
      <c:url value="MailAccount" var="urlMailFilterRuleList">
        <c:param name="action" value="filterRuleList"/>
        <c:param name="ma_user" value="${ma_user}"/>
        <c:param name="ma_id" value="${ma_id}"/>
        <c:param name="mf_id" value="${mf_id}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${urlMailAccountList}">Mail accounts</a>
        </li>
        <li class="path">
          <a href="${urlMailFilterList}">Mail filters</a>
        </li>
        <li class="path">
          <a href="${urlMailFilterRuleList}">Mail filters rules</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'ruleCreate'}">Create mail filter rule</c:when>
            <c:when test="${action == 'ruleEdit'}">Edit mail filter rule</c:when>
            <c:when test="${action == 'ruleDelete'}">Delete mail filter rule</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="MailAccount" id="form">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ma_id" value="${ma_id}"/>
        <input type="hidden" name="mf_id" value="${mf_id}"/>
        <input type="hidden" name="mfr_id" value="${mfr.id}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td nowrap="nowrap">Field</td>
            <td>
              <select name="mfr_field">
                <c:forEach var="fld" items="${fields}">
                  <c:choose>
                    <c:when test="${fld == mfr.field}">
                      <option value="${fld}" selected="selected">${fld}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${fld}">${fld}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Operation</td>
            <td>
              <select name="mfr_operation">
                <c:forEach var="ope" items="${operations}">
                  <c:choose>
                    <c:when test="${ope == mfr.operation}">
                      <option value="${ope}" selected="selected">${ope}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${ope}">${ope}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Value</td>
            <td><input name="mfr_value" value="${mfr.value}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${mfr.active}">
                  <input name="mfr_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="mfr_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'ruleCreate'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'ruleEdit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'ruleDelete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
              </c:choose>
            </td>
          </tr>
        </table>
      </form>
      <br/>
      <div class="warn" style="text-align: center;" id="dest"></div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>