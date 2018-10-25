<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link rel="Shortcut icon" href="favicon.ico"/>
  <link rel="stylesheet" type="text/css" href="css/admin-style.css"/>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <title>Automation registered list</title>
</head>
<body>
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Automation?action=registeredList">
            Registered list
          </a>
        </li>
        <li class="action">
          <a href="Automation?action=reloadRegisteredList">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Reload
          </a>
        </li>
        <li class="action">
          <a href="Automation">
            <img src="img/action/generic.png" alt="Generic" title="Generic" style="vertical-align: middle;"/>
            Automation rules
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="70%">
          <thead>
            <tr>
              <th colspan="13">Automation registered plugins</th>
            </tr>
          </thead>
          <tr class="fuzzy">
            <td colspan="13" align="left">
              <table id="info" style="white-space: nowrap;" width="175px">
                <tr>
                  <td><b>Registered validations</b></td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <th>Name</th>
            <th>Class name</th>
            <th>Pre</th>
            <th>Post</th>
            <th>Type00</th>
            <th>Source00</th>
            <th>Description00</th>
            <th>Type01</th>
            <th>Source01</th>
            <th>Description01</th>
            <th>Type02</th>
            <th>Source02</th>
            <th>Description02</th>
          </tr>
          <c:forEach var="validation" items="${validations}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${validation.name}</td>
              <td>${validation.getClass().name}</td>
              <td align="center">
                <c:choose>
                  <c:when test="${validation.hasPre()}">
                    <img src="img/true.png" alt="True" title="True"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="False" title="False"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <c:choose>
                  <c:when test="${validation.hasPost()}">
                    <img src="img/true.png" alt="True" title="True"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="False" title="False"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>${validation.getParamType00()}</td>
              <td>${validation.getParamSrc00()}</td>
              <td>${validation.getParamDesc00()}</td>
              <td>${validation.getParamType01()}</td>
              <td>${validation.getParamSrc01()}</td>
              <td>${validation.getParamDesc01()}</td>
              <td>${validation.getParamType02()}</td>
              <td>${validation.getParamSrc02()}</td>
              <td>${validation.getParamDesc02()}</td>
            </tr>
          </c:forEach>
          <tr>
            <td colspan="13">&nbsp;</td>
          </tr>
          <tr class="fuzzy">
            <td colspan="13" align="left">
              <table id="info" style="white-space: nowrap;" width="175px">
                <tr>
                  <td><b>Registered actions</b></td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <th>Name</th>
            <th>Class name</th>
            <th>Pre</th>
            <th>Post</th>
            <th>Type00</th>
            <th>Source00</th>
            <th>Description00</th>
            <th>Type01</th>
            <th>Source01</th>
            <th>Description01</th>
            <th>Type02</th>
            <th>Source02</th>
            <th>Description02</th>
          </tr>
          <c:forEach var="action" items="${actions}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${action.name}</td>
              <td>${action.getClass().name}</td>
              <td align="center">
                <c:choose>
                  <c:when test="${action.hasPre()}">
                    <img src="img/true.png" alt="True" title="True"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="False" title="False"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td align="center">
                <c:choose>
                  <c:when test="${action.hasPost()}">
                    <img src="img/true.png" alt="True" title="True"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="False" title="False"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>${action.getParamType00()}</td>
              <td>${action.getParamSrc00()}</td>
              <td>${action.getParamDesc00()}</td>
              <td>${action.getParamType01()}</td>
              <td>${action.getParamSrc01()}</td>
              <td>${action.getParamDesc01()}</td>
              <td>${action.getParamType02()}</td>
              <td>${action.getParamSrc02()}</td>
              <td>${action.getParamDesc02()}</td>
            </tr>
          </c:forEach>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>