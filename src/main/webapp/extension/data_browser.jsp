<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <link rel="stylesheet" type="text/css" href="../css/fixedTableHeader.css" />
  <style type="text/css">body, html { padding:0; margin:0; }</style>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      TABLE.fixHeader('table');

      $('.btn-select').click(function() {
        var dst = $(this).data('dst');
        var path = $(this).data('path');
        $('#' + dst, window.parent.document).val(path);
        window.parent.dialogClose();
      });
	});
  </script>
  <title>Data Browser</title>
</head>
<body>
  <c:set var="row">1</c:set>
  <table class="results" width="100%">
    <thead>
      <tr>
        <th>Name</th><th width="50px"></th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="fld" items="${folders}">
        <c:url value="DataBrowser" var="urlBrowse">
          <c:param name="action" value="${action}"/>
          <c:param name="dst" value="${dst}"/>
          <c:param name="sel" value="${sel}"/>
          <c:param name="root" value="${root}"/>
          <c:param name="path" value="${fld.path}"/>
        </c:url>
        <c:set var="row">${row + 1}</c:set>
        <tr class="${row % 2 == 0 ? 'even' : 'odd'}">
          <td><a href="${urlBrowse}">${fld.name}</a></td>
          <td align="center">
            <c:if test="${fld.sel == 'true'}">
              <a href="javascript:void(0)" class="btn-select" data-dst="${dst}" data-path="${fld.path}"><img src="img/select.png" alt="Select" title="Select"/></a>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <c:forEach var="doc" items="${documents}">
        <c:url value="DataBrowser" var="urlBrowse">
          <c:param name="action" value="${action}"/>
          <c:param name="dst" value="${dst}"/>
          <c:param name="sel" value="${sel}"/>
          <c:param name="root" value="${root}"/>
          <c:param name="path" value="${doc.path}"/>
        </c:url>
        <c:set var="row">${row + 1}</c:set>
        <tr class="${row % 2 == 0 ? 'even' : 'odd'}">
          <td>${doc.name}</td>
          <td align="center">
            <c:if test="${doc.sel == 'true'}">
              <a href="javascript:void(0)" class="btn-select" data-dst="${dst}" data-path="${doc.path}"><img src="img/select.png" alt="Select" title="Select"/></a>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</body>
</html>
