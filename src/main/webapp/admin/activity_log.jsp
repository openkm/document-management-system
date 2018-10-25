<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
  <link rel="stylesheet" type="text/css" href="../css/jquery-ui-1.10.3/jquery-ui-1.10.3.css" />
  <link rel="stylesheet" type="text/css" href="../css/chosen.css"/>
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/utils.js"></script>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/jquery-ui-1.10.3/jquery-ui-1.10.3.js"></script>
  <script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js" ></script>
  <script type="text/javascript">
    $(document).ready(function () {
      $('#results').dataTable({
        "bStateSave": true,
        "iDisplayLength": 15,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });

      $("#dbegin").datepicker({
        showOn: "button",
        buttonImage: "img/action/calendar.png",
        buttonImageOnly: true,
        dateFormat: "yy-mm-dd",
        defaultDate: "-1w",
        changeMonth: true,
        changeYear: true,
        numberOfMonths: 1,
        showWeek: false,
        firstDay: 1,
        onClose: function (selectedDate) {
          $("#dend").datepicker("option", "minDate", selectedDate);
          $('.ui-datepicker-trigger').css("vertical-align", "middle");
        }
      });

      $("#dend").datepicker({
        showOn: "button",
        buttonImage: "img/action/calendar.png",
        buttonImageOnly: true,
        dateFormat: "yy-mm-dd",
        changeMonth: true,
        changeYear: true,
        numberOfMonths: 1,
        showWeek: false,
        firstDay: 1,
        onClose: function (selectedDate) {
          $("#dbegin").datepicker("option", "maxDate", selectedDate);
          $('.ui-datepicker-trigger').css("vertical-align", "middle");
        }
      });

      $('.ui-datepicker-trigger').css('vertical-align', 'middle');
      $('select#user').chosen({disable_search_threshold: 10});
      $('select#action').chosen({disable_search_threshold: 10});

      $("#resetButton").click(function () {
        $('#dbegin').val("");
        $('#dend').val("");
        $('#user option:eq(0)').prop('selected', true);
        $('#action option:eq(0)').prop('selected', true);
        $('#user').trigger("chosen:updated");
        $('#action').trigger("chosen:updated");
        $('#item').val("");
      });

    });
  </script>
  <title>Activity Log</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="ActivityLog">Activity log</a>
        </li>
      </ul>
      <br/>
      <div style="width:95%; margin-left:auto; margin-right:auto;">
       <table id="results" class="results">
        <thead>
			<tr class="header">
	           <td align="right" colspan="9">
	              <form action="ActivityLog">
	              <b>From</b> <input type="text" name="dbegin" id="dbegin" value="${dbeginFilter}" size="15" readonly="readonly" />
	              <b>To</b> <input type="text" name="dend" id="dend" value="${dendFilter}" size="15" readonly="readonly" />
	              <b>User</b>
	              <select name="user" id="user" style="width: 125px" data-placeholder="&nbsp;">
	                <option value="">All</option>
	                <c:forEach var="user" items="${users}" varStatus="row">
	                  <c:choose>
	                    <c:when test="${user == userFilter}">
	                      <option value="${user}" selected="selected">${user}</option>
	                    </c:when>
	                    <c:otherwise>
	                      <option value="${user}">${user}</option>
	                    </c:otherwise>
	                  </c:choose>
	                </c:forEach>
	              </select>
	              <b>Action</b>
	              <select name="action" id="action" style="width: 350px" data-placeholder="&nbsp;">
	                <option value="">All</option>
	                <c:forEach var="act" items="${actions}">
	                  <c:choose>
	                    <c:when test="${act == 'Auth' || act == 'Document' || act == 'Folder' || act == 'Mail' || act == 'Repository' || act == 'Admin'
	                                  || act == 'Misc'}">
	                      <optgroup label="${act}" />
	                    </c:when>
	                    <c:otherwise>
	                      <c:choose>
	                        <c:when test="${act == actionFilter}">
	                          <option value="${act}" selected="selected">${act}</option>
	                        </c:when>
	                        <c:otherwise>
	                          <option value="${act}">${act}</option>
	                        </c:otherwise>
	                      </c:choose>
	                    </c:otherwise>
	                  </c:choose>
	                </c:forEach>
	                </select>
	                <b>Item</b> <input type="text" name="item" id="item" value="${itemFilter}" size="42" />
	                <input type="button" value="Reset" class="resetButton" id="resetButton"/>
	                <input type="submit" value="Filter" class="searchButton"/>
	              </form>
	           </td>
          	</tr>        	
          	<tr>
	          <th>Date</th>
	          <th>User</th>
	          <th>Action</th>
	          <th>Item</th>
	          <th>Path</th>
	          <th>Parameters</th>
          	</tr>
        </thead>
        <tbody>
          <c:forEach var="act" items="${results}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td nowrap="nowrap"><u:formatDate calendar="${act.date}"/></td>
              <td>${act.user}</td>
              <td>${act.action}</td>
              <td>${act.item}</td>
              <td>${act.path}</td>
              <td>${act.params}</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>     
      </div>      
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>