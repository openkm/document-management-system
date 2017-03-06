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
  <link rel="stylesheet" type="text/css" href="css/fixedTableHeader.css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/fixedTableHeader.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
        var checkboxes = $("input[type='checkbox']");
        checkboxes.click(function() {
            $('.addButton').attr("disabled", !checkboxes.is(":checked"));
        });
        
    	$('.addButton').click(function(event) {
    	    $(this).attr("disabled", true);
    		$("#dest").removeClass('ok').removeClass('error').empty();
    		$("input[name=msg_uid]:checked").map(function() {
    			$("#dest").append($('<div>').load('MailAccount', { action: "serverImport", ma_id: ${ma_id}, msg_id: $(this).attr('value') },
    	            function(response, status, xhr) {
    	            	if (response.indexOf('Success') === 0) {
    	            		$(this).removeClass('error').addClass('ok').css("text-align", "center");
    	            	} else {
    	            		$(this).removeClass('ok').addClass('error').css("text-align", "center");
    	            	}
    	            	
    	            	$('.addButton').attr("disabled", false);
    	            }
    			));
    		});
	   	});

    	TABLE.fixHeader('table.results');
	});
  </script>
  <title>Mail filters</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="MailAccount" var="urlMailAccountList">
        <c:param name="ma_user" value="${ma_user}"/>
      </c:url>
      <c:url value="MailAccount" var="urlServerMails">
        <c:param name="action" value="serverList"/>
        <c:param name="ma_id" value="${ma_id}"/>
        <c:param name="ma_user" value="${ma_user}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${urlMailAccountList}">Mail accounts</a>
        </li>
        <li class="path">Server email list</li>
        <li class="action">
          <a href="${urlServerMails}">
          	<img src="img/action/refresh.png" alt="Refresh" title="Refresh" style="vertical-align: middle;"/>
          	Refresh
          </a>
        </li>
      </ul>
      <br/>
      <table class="results" width="80%">
        <thead>
          <tr class="fuzzy">
            <td colspan="5" align="right">
              Max: ${max}
              &nbsp;
              <c:choose>
                <c:when test="${start > 1}">
                  <a href="MailAccount?action=serverList&ma_id=${ma_id}&ma_user=${ma_user}&start=1"><img src="img/action/first.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/first_disabled.png"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${start > 1}">
                  <a href="MailAccount?action=serverList&ma_id=${ma_id}&ma_user=${ma_user}&start=${start - limit}"><img src="img/action/previous.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/previous_disabled.png"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${start + limit < max}">
                  <a href="MailAccount?action=serverList&ma_id=${ma_id}&ma_user=${ma_user}&start=${start + limit}"><img src="img/action/next.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/next_disabled.png"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${start + limit < max}">
                  <a href="MailAccount?action=serverList&ma_id=${ma_id}&ma_user=${ma_user}&start=${max - max % limit + 1}"><img src="img/action/last.png"/></a>
                </c:when>
                <c:otherwise>
                  <img src="img/action/last_disabled.png"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <th>#</th><th>From</th><th>Subject</th><th>Received</th>
            <th width="25px"></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="sm" items="${serverMails}" varStatus="row">
            <c:choose>
              <c:when test="${sm.seen}">
                <c:set var="weight">normal</c:set>
              </c:when>
              <c:otherwise>
                <c:set var="weight">bold</c:set>
              </c:otherwise>
            </c:choose>
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}" style="font-weight: ${weight}">
              <td width="20px">${sm.msgNumber}</td>
              <td nowrap="nowrap">${sm.from}</td>
              <td>${sm.subject}</td>
              <td nowrap="nowrap"><u:formatDate date="${sm.receivedDate}"/></td>
              <td align="center">
                <input type="checkbox" name="msg_uid" value="${sm.uid}"/>
              </td>
            </tr>
          </c:forEach>
          <tr class="fuzzy">
            <td colspan="5" align="right">
              <input type="button" value="Import" class="addButton" disabled/>
            </td>
          </tr>
        </tbody>
      </table>
      <div style="text-align: center;" id="dest">
      </div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>