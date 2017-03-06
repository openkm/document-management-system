<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page isErrorPage="true" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <% if (FormatUtil.isMobile(request)) { %>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/mobile.css" type="text/css" />
  <% } else { %>
  <link rel="stylesheet" href="<%=request.getContextPath() %>/css/desktop.css" type="text/css" />
  <% } %>
  <title>Login error</title>
</head>
<body>
  <table border="0" width="100%" align="center" style="padding-top: 125px">
  <tr><td align="center">
  <form name="login" method="post" action="j_security_check">
    <table>
      <tr>
      	<td colspan="2" align="center"><img src="<%=request.getContextPath() %>/logo/login" border="0" /></td>
      </tr>
      <tr>
        <td colspan="2" align="center" style="padding-top: 25px;">
        <h2><%=exception.toString()%></h2>
        <% session.invalidate(); %>
        </td>
      </tr>
      <tr>
        <td colspan="2" align="center"><a href="<%=request.getContextPath()%>">Go to login page</a></td>
      </tr>
    </table>
  </form>
  </td></tr></table>
</body>
</html>
