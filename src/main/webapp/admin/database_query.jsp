<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="java.util.concurrent.TimeUnit" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/mode/plsql/plsql.css" />
  <style type="text/css">
    .CodeMirror { width: 700px; height: 300px; background-color: #f8f6c2; }
    .activeline { background: #f0fcff !important; }
    .results .cellEditing { padding: 0; }
    .results .cellEditing input[type=text] { width: 100%; border: 0; background-color: rgb(255,253,210); }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/plsql/plsql.js"></script>
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      var cm = CodeMirror.fromTextArea(document.getElementById('qs'), {
          lineNumbers: true,
      	  matchBrackets: true,
          indentUnit: 4,
          mode: "text/x-plsql",
          onCursorActivity: function() {
        	cm.setLineClass(hlLine, null);
            hlLine = cm.setLineClass(cm.getCursor().line, "activeline");
          }
        }
      );
      
      var hlLine = cm.setLineClass(0, "activeline");
      var width = $(window).width() - 60;
      var height = $(window).height() / 3;
      $('.CodeMirror').css({"width": width});
      $('.CodeMirror').css({"height": height});
      
      if ($('#type').val() == 'jdbc') {
        $('#divTables').show();
        $('#divVTables').hide();
        $('#divShowSql').hide();
      } else if ($('#type').val() == 'metadata') {
        $('#divTables').hide();
        $('#divVTables').show();
        $('#divShowSql').hide();
      } else if ($('#type').val() == 'hibernate') {
    	$('#divTables').hide();
        $('#divVTables').hide();
        $('#divShowSql').show();
      } else {
    	$('#divTables').hide();
        $('#divVTables').hide();
        $('#divShowSql').hide();
      }
      
      $('#type').change(function() {
        if ($(this).val() == 'jdbc') {
          $('#divTables').show();
          $('#divVTables').hide();
          $('#divShowSql').hide();
        } else if ($(this).val() == 'metadata') {
          $('#divTables').hide();
          $('#divVTables').show();
          $('#divShowSql').hide();
        } else if ($('#type').val() == 'hibernate') {
          $('#divTables').hide();
          $('#divVTables').hide();
          $('#divShowSql').show();
        } else {
          $('#divTables').hide();
          $('#divVTables').hide();
          $('#divShowSql').hide();
        }
      });
      
      $('#tables').change(function() {
        if ($(this).val() == '') {
          cm.setValue('');
        } else {
          cm.setValue('SELECT * FROM ' + $(this).val() + ';');
        }
      });
      
      $('#vtables').change(function() {
        if ($(this).val() == '') {
          cm.setValue('');
        } else {
          cm.setValue('SELECT|' + $(this).val());
        }
      });
      
      $('#edit').click(function() {
        var mtable = $('#vtables').val();
        
        if (mtable != '') {
          window.location = 'DatabaseQuery?action=list&mtable=' + mtable;
        } else {
          alert('Please, select a metatable');
        }
      });
      
      $("td").dblclick(function () {
        if ($('#type').val() == 'metadata' && '${vtable}' != '') {
          var OriginalContent = $(this).text();
          
          $(this).addClass("cellEditing");
          $(this).html("<input type='text' value='" + OriginalContent + "'/>");
          $(this).children().first().focus();
          $(this).children().first().keypress(function (e) {
            if (e.which == 13) {
              var newContent = $(this).val();
              var parent = $(this).parent();
              var id = parent.closest('tr').find('td:first');
              var header = parent.closest('table').find('th').eq(parent.index());
              var params = { "action" : "edit", "vtable": "${vtable}", "column": header.text(), "id": id.text(), "value": newContent };
              
              $.ajax({
                url: 'DatabaseQuery',
                type: 'post',
                data: params
              }).fail(function(jqXHR, textStatus) {
                alert("Error updating value: " + textStatus);
              });
              
              $(this).parent().text(newContent);
              $(this).parent().removeClass("cellEditing");
            }
          });
          
          $(this).children().first().blur(function() {
            $(this).parent().text(OriginalContent);
            $(this).parent().removeClass("cellEditing");
          });
        }
      });
    });
    
    function keepSessionAlive() {
    	$.ajax({ type:'GET', url:'../SessionKeepAlive', cache:false, async:false });
    }
    
	window.setInterval('keepSessionAlive()', <%=TimeUnit.MINUTES.toMillis(Config.KEEP_SESSION_ALIVE_INTERVAL)%>);
  </script>
  <title>Database Query</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
   <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="DatabaseQuery">Database query</a>
        </li>
      </ul>
      <br/>
      <table class="form">
        <tr>
          <td>
            <form action="DatabaseQuery" method="post" enctype="multipart/form-data">
              <table>
                <tr>
                  <td colspan="2">
                    <textarea cols="80" rows="25" name="qs" id="qs">${qs}</textarea>
                  </td>
                </tr>
                <tr>
                  <td align="left">
                    <div id="divTables">
                      Table
                      <select id="tables" name="tables">
                        <option></option>
                        <c:forEach var="table" items="${tables}">
                          <option value="${table}">${table}</option>
                        </c:forEach>
                      </select>
                    </div>
                    <div id="divVTables">
                      Metatable
                      <select id="vtables" name="vtables">
                        <option></option>
                        <c:forEach var="vtable" items="${vtables}">
                          <option value="${vtable}">${vtable}</option>
                        </c:forEach>
                      </select>
                    </div>
                    <div id="divShowSql">
                      <table cellpadding="0" cellspacing="0">
                        <tr>
                          <td>Show SQL</td>
                          <td>
                            <c:choose>
                              <c:when test="${showSql}">
                                <input name="showSql" type="checkbox" checked="checked" style="margin: 0px 0px 0px 3px;"/>
                              </c:when>
                              <c:otherwise>
                                <input name="showSql" type="checkbox" style="margin: 0px 0px 0px 3px;"/>
                              </c:otherwise>
                            </c:choose>
                          </td>
                        </tr>
                      </table>
                    </div>
                  </td>
                  <td align="right">
                    Type
                    <select name="type" id="type">
                      <option></option>
                      <c:choose>
                        <c:when test="${type == 'jdbc'}">
                          <option value="jdbc" selected="selected">JDBC</option>
                        </c:when>
                        <c:otherwise>
                          <option value="jdbc">JDBC</option>
                        </c:otherwise>
                      </c:choose>
                      <c:choose>
                        <c:when test="${type == 'hibernate'}">
                          <option value="hibernate" selected="selected">Hibernate</option>
                        </c:when>
                        <c:otherwise>
                          <option value="hibernate">Hibernate</option>
                        </c:otherwise>
                      </c:choose>
                      <c:choose>
                        <c:when test="${type == 'metadata'}">
                          <option value="metadata" selected="selected">Metadata</option>
                        </c:when>
                        <c:otherwise>
                          <option value="metadata">Metadata</option>
                        </c:otherwise>
                      </c:choose>
                    </select>
                    <input type="submit" value="Execute" class="executeButton"/>
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
        <tr class="fuzzy">
          <td colspan="4" align="right">
            <form action="DatabaseQuery" method="post" enctype="multipart/form-data">
              <input type="hidden" name="action" value="import"/>
              <table>
                <tr>
                  <td><input class=":required :only_on_blur" type="file" name="sql-file"/></td>
                  <td><input type="submit" value="Import SQL script" class="loadButton"/></td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
      <br/>
      <c:if test="${exception != null}">
        <div class="error">
          <table align="center">
            <tr>
              <td><b>Class:</b></td>
              <td>${exception['class'].name}</td>
            </tr>
            <tr>
              <td><b>Message:</b></td>
              <td>${exception.message}</td>
            </tr>
            <c:if test="${exception.cause != null}">
              <tr>
                <td><b>Cause:</b></td>
                <td>${exception.cause.message}</td>
              </tr>
            </c:if>
          </table>
        </div>
      </c:if>
      <br/>
      <c:forEach var="gResult" items="${globalResults}">
        <c:if test="${gResult.sql != null}">
          <div class="ok">
            <center>
              <c:out value="${gResult.sql}"/><br/><br/>
              <c:if test="${gResult.extra!= null}">
                <div class="warn">
                  <center>
                    <c:out value="${gResult.extra}"/><br/><br/>
                  </center>
                </div>
              </c:if>
              Time: <u:formatMiliSeconds time="${gResult.time}"/>
            </center>
          </div>
          <br/>
        </c:if>
        <c:choose>
          <c:when test="${gResult.rows != null}">
            <div class="ok"><center>Row Count: ${gResult.rows}</center></div>
            <br/>
          </c:when>
          <c:when test="${not empty gResult.errors}">
            <table class="results" width="95%">
              <tr><th>Line</th><th>SQL</th><th>Error</th></tr>
              <c:forEach var="error" items="${gResult.errors}" varStatus="row">
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${error.ln}</td>
                <td>${error.sql}</td>
                <td>${error.msg}</td>
              </tr>
            </c:forEach>
            </table>
          </c:when>
          <c:otherwise>
            <table class="results" width="95%">
              <tr>
                <c:if test="${type == 'metadata'}">
                  <th>#</th>
                </c:if>
                <c:forEach var="col" items="${gResult.columns}">
                  <th>${col}</th>
                </c:forEach>
              </tr>
              <c:forEach var="result" items="${gResult.results}" varStatus="row">
                <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                  <c:forEach var="col" items="${result}" varStatus="status">
                    <c:choose>
                      <c:when test="${type == 'metadata' && status.first}">
                        <td>${col}</td>
                      </c:when>
                      <c:otherwise>
                        <td>${col}</td>
                      </c:otherwise>
                    </c:choose>
                  </c:forEach>
                </tr>
              </c:forEach>
            </table>
            <br/>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>