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
  <link rel="stylesheet" type="text/css" href="../css/chosen.css"/>
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js" ></script>
  <script type="text/javascript" src="../js/chosen.jquery.js" ></script>
  <!-- Load TinyMCE -->
  <script type="text/javascript" src="../js/tiny_mce/tiny_mce.js"></script>
  <script type="text/javascript" src="../js/tiny_mce/jquery.tinymce.js"></script>
  <title>Configuration</title>
  <u:constantsMap className="com.openkm.dao.bean.Config" var="Config"/>
  <script type="text/javascript">
    $(document).ready(function() {
      $('select#cfg_type').chosen({disable_search_threshold: 10});
      $('select#cfg_type').prop('disabled', true).trigger("chosen:updated");
      $('form').submit(function() {
        $('select#cfg_type').prop('disabled', false).trigger("chosen:updated");
      });

      <c:if test="${cfg.type == Config.HTML}">
		   var toolbar = 'bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image code';      
		   var params = '{';
		   params = params + '"elements":"textarea"';
		   params = params + ',"language":"en"';
		   params = params + ',"theme":"advanced"';
		   params = params + ',"plugins":""';
		   params = params + ',"toolbar":"' + toolbar + '"';
		   params = params + ',"height":"300"';        
		   params = params + ',"menubar":"false"';		   
		   params = params + '}';
		   var json = $.parseJSON(params); // create json object from string value		   
		   $('textarea').tinymce(json);
      </c:if>
    });
  </script>
</head>
<body>  
<c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="Config">Configuration</a></li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create configuration</c:when>
            <c:when test="${action == 'edit'}">Edit configuration</c:when>
            <c:when test="${action == 'delete'}">Delete configuration</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Config" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="filter" value="${filter}"/>   
        <c:choose>
          <c:when test="${cfg.type == Config.TEXT || cfg.type == Config.LIST}">
            <c:set var="tableWidth" value="500px"></c:set>
          </c:when>
          <c:when test="${cfg.type == Config.HTML}">
            <c:set var="tableWidth" value="700px"></c:set>
          </c:when>
          <c:otherwise>
            <c:set var="tableWidth" value="400px"></c:set>
          </c:otherwise>
        </c:choose>             
        <table class="form" width="${tableWidth}">
          <tr>
            <td nowrap="nowrap">Key</td>
            <td>
              <c:choose>
                <c:when test="${action != 'create'}">
                  <input size="50" class=":required :only_on_blur" name="cfg_key" value="${cfg.key}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input size="50" class=":required :only_on_blur" name="cfg_key" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Type</td>
            <td>
              <!-- http://stackoverflow.com/questions/368813/readonly-select-tag -->
              <select name="cfg_type" id="cfg_type" style="width: 100px">
                <c:forEach var="type" items="${types}">
                  <c:choose>
                    <c:when test="${cfg.type == type.key}">
                      <option value="${type.key}" selected="selected">${type.value}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${type.key}">${type.value}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td>Value</td>
            <td>
              <c:choose>
                <c:when test="${cfg.type == Config.STRING || cfg.type == Config.INTEGER || cfg.type == Config.LONG}">
                  <input size="50" name="cfg_value" value="${cfg.value}"/>
                </c:when>
                <c:when test="${cfg.type == Config.BOOLEAN}">
                  <c:choose>
                    <c:when test="${cfg.value == 'true'}">
                      <input name="cfg_value" type="checkbox" checked="checked"/>
                    </c:when>
                    <c:otherwise>
                      <input name="cfg_value" type="checkbox"/>
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:when test="${cfg.type == Config.FILE}">
                  <c:choose>
                    <c:when test="${action == 'create'}">
                      <input class=":required :only_on_blur" type="file" name="file"/>
                    </c:when>
                    <c:otherwise>
                      <c:url value="Config" var="urlView">
                        <c:param name="action" value="view"/>
                        <c:param name="cfg_key" value="${cfg.key}"/>
                      </c:url>
                      <table cellpadding="0" cellspacing="0"><tr><td><img src="${urlView}"/></td></tr><tr><td><input type="file" name="image"/></td></tr></table>
                    </c:otherwise>
                  </c:choose>
                </c:when>
                <c:when test="${cfg.type == Config.SELECT}">
                  <u:configStoredSelect key="${cfg.key}" value="${cfg.value}"/>
                </c:when>
                <c:otherwise>
                  <textarea rows="15" cols="78" name="cfg_value">${u:replace(cfg.value, "</#list>", "&amp;lt;/#list&amp;gt;")}</textarea>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <c:url value="Config" var="urlCancel">
                <c:param name="filter" value="${filter}"/>
              </c:url>
              <input type="button" onclick="javascript:window.location.href='${urlCancel}'" value="Cancel" class="noButton"/>
              <c:choose>
                <c:when test="${action == 'create'}"><input type="submit" value="Create" class="yesButton"/></c:when>
                <c:when test="${action == 'edit'}"><input type="submit" value="Edit" class="yesButton"/></c:when>
                <c:when test="${action == 'delete'}"><input type="submit" value="Delete" class="yesButton"/></c:when>
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