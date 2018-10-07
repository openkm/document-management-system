<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="../css/chosen.css" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <script src="../js/chosen.jquery.js" type="text/javascript"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $('select#users').chosen();
    });
  </script>
  <title>Document expiration group list</title>
</head>
<body>  
  <c:choose>
   <c:when test="${u:isAdmin()}">
     <ul id="breadcrumb">
        <li class="path">
          <a href="DocumentExpiration">Document expiration</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'groupCreate'}">Create group</c:when>
            <c:when test="${action == 'groupEdit'}">Edit group</c:when>
            <c:when test="${action == 'groupDelete'}">Delete group</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
     <form action="DocumentExpiration">
       <input type="hidden" name="action" value="${action}"/>
       <input type="hidden" name="persist" value="${persist}"/>
       <table class="form" width="250px">
         <tr>
           <td>Group name</td>
           <c:choose>
             <c:when test="${action == 'groupCreate'}">
               <td><input name="gru_name" class=":required :only_on_blur" value=""/></td>
             </c:when>
             <c:otherwise>
               <input type="hidden" name="gru_name" value="${group}"/>
               <td>${group}</td>
             </c:otherwise>
           </c:choose>
         </tr>
         <tr>
           <td>Users</td>
           <td>
             <select multiple="multiple" name="users" size="10">
               <c:forEach var="availableUser" items="${availableUsers}">
                 <c:choose>
                   <c:when test="${u:contains(users, availableUser)}">
                     <option value="${availableUser}"  selected="selected">${availableUser}</option>
                   </c:when>
                   <c:otherwise>
                     <option value="${availableUser}">${availableUser}</option>
                   </c:otherwise>
                 </c:choose>
               </c:forEach>
             </select>
           </td>
         </tr>
         <tr>
           <td colspan="2" align="right">
             <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton"/>
             <c:choose>
                <c:when test="${action == 'groupCreate'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'groupEdit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'groupDelete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
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