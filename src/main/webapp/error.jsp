<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="bsh.TargetError" %>
<%@ page import="com.openkm.frontend.client.OKMException" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page isErrorPage="true" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <% if (FormatUtil.isMobile(request)) { %>
  <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/mobile.css" type="text/css" />
  <% } else { %>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/desktop.css" type="text/css" />
  <% } %>
  <title>OpenKM Error</title>
</head>
<body style="background-color: #f6f6ee;">
  <table border="0" width="100%" align="center" style="padding-top: 150px">
  <tr><td align="center">
  <table class="form">
    <tr class="fuzzy">
      <td colspan="2" style="text-align: center; font-weight: bold; font-size: 12px">Application error</td>
    </tr>
    <tr>
      <td><b>Class:</b></td>
      <td><%=exception.getClass().getName() %></td>
    </tr>
    <% if (exception instanceof OKMException) { %>
    <tr>
      <td><b>Code:</b></td>
      <td><%=((OKMException) exception).getCode() %></td>
    </tr>
    <tr>
      <td><b>Message:</b></td>
      <td><%=((OKMException) exception).getMessage() %></td>
    </tr>
    <% } else if (exception instanceof TargetError) { %>
    <tr>
      <td><b>Text:</b></td>
      <td><%=((TargetError) exception).getErrorText() %></td>
    </tr>
    <tr>
      <td><b>Source:</b></td>
      <td><%=((TargetError) exception).getErrorSourceFile() %></td>
    </tr>
    <tr>
      <td><b>Line:</b></td>
      <td><%=((TargetError) exception).getErrorLineNumber() %></td>
    </tr>
    <% } else { %>
    <tr>
      <td><b>Message:</b></td>
      <td><%=exception.getMessage() %></td>
    </tr>
    <% } %>
    <tr>
      <td><b>Date:</b></td>
      <td><%= new java.util.Date() %></td>
    </tr>
    <tr>
      <td colspan="2" align="center" style="padding-top: 10px;">
        <input type="button" value="Return" class="yesButton" onclick="javascript:history.go(-1)" />
      </td>
    </tr>
  </table>
  </td></tr></table>
</body>
</html>