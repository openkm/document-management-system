<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <link rel="stylesheet" type="text/css" href="../css/chosen.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $('select#ma_mprotocol').chosen({
        disable_search_threshold : 10
      });
  
      $("#check").click(function(event) {
        $("#dest").removeClass('ok').removeClass('error').html('Checking....');
        $("#dest").load('MailAccount', {
          action : "check",
          ma_mprotocol : $('[name=ma_mprotocol]').val(),
          ma_mhost : $('[name=ma_mhost]').val(),
          ma_muser : $('[name=ma_muser]').val(),
          ma_mpassword : $('[name=ma_mpassword]').val(),
          ma_mfolder : $('[name=ma_mfolder]').val()
        }, function(response, status, xhr) {
          if (response == 'Success!') {
            $(this).removeClass('error').addClass('ok');
          } else {
            $(this).removeClass('ok').addClass('error');
          }
        });
      });
    });
  </script>
  <title>Mail account</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="MailAccount" var="urlMailAccountList">
        <c:param name="ma_user" value="${ma.user}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${urlMailAccountList}">Mail accounts</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create mail account</c:when>
            <c:when test="${action == 'edit'}">Edit mail account</c:when>
            <c:when test="${action == 'delete'}">Delete mail account</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="MailAccount" id="form" autocomplete="off">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ma_id" value="${ma.id}"/>
        <input type="hidden" name="ma_user" value="${ma.user}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td nowrap="nowrap">Mail protocol</td>
            <td>
              <select name="ma_mprotocol" id="ma_mprotocol" style="width: 85px">
                <c:forEach var="proto" items="${protocols}">
                  <c:choose>
                    <c:when test="${proto == ma.mailProtocol}">
                      <option value="${proto}" selected="selected">${proto}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${proto}">${proto}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Mail host</td>
            <td><input class=":required :only_on_blur" name="ma_mhost" value="${ma.mailHost}" size="26"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Mail user</td>
            <td><input class=":required :only_on_blur" name="ma_muser" value="${ma.mailUser}" size="26"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Mail password</td>
            <td><input class=":required :only_on_blur" name="ma_mpassword" type="password" value="${ma.mailPassword}" autocomplete="off"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Mail folder</td>
            <td><input name="ma_mfolder" value="${ma.mailFolder}"/></td>
          </tr>
          <tr>
            <td>Mail mark seen</td>
            <td>
              <c:choose>
                <c:when test="${ma.mailMarkSeen}">
                  <input name="ma_mmark_seen" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ma_mmark_seen" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Mail mark deleted</td>
            <td>
              <c:choose>
                <c:when test="${ma.mailMarkDeleted}">
                  <input name="ma_mmark_deleted" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ma_mmark_deleted" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${ma.active}">
                  <input name="ma_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ma_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" id="check" value="Check" class="executeButton"/>
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
      <br/>
      <div style="text-align: center;" id="dest"></div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>