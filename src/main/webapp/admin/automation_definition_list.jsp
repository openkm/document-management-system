<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script type="text/javascript">
    function hideAll() {
    	$("#actionForm").hide();
    	$("#validationForm").hide();
    }
  
    $(document).ready(function() {
      $('#extra_info').hide();
      hideAll();
   	  $("#addValidation").click(function(event) {
       	var value = $("#ma_id_validation").val();
       	if (value!='') {
   	    	$.get('Automation', { 
   	    		action: "getMetadata",
   	    		amId : $("#ma_id_validation").val()
   	    		},
   	        	function(data) {
   	    			$.get('Automation', {
   	    				 action : 'loadMetadataForm',
   	    				 newAction : 'createValidation',
   	    				 ar_id : '${ar.id}',
   	    				 am_id : data.id,
   	    			},
   	    			function(data, status, xhr) {
   	    				$("#validationForm").show();
   	    				$("#validationForm").html(data);
   	    			});
   	        	}, "json");
       	} else {
       		$("#actionForm").hide();
       		alert('Should select any action to be added');
       	}
      });
    	
      $("#addAction").click(function(event) {
    	hideAll();
    	var value = $("#ma_id_action").val();
    	if (value!='') {
	    	$.get('Automation', { 
	    		action: "getMetadata",
	    		amId : $("#ma_id_action").val()
	    		},
	        	function(data) {
	    			$.get('Automation', {
	    				action : 'loadMetadataForm',
	    				newAction : 'createAction',
	    				ar_id : '${ar.id}',
	    				am_id : data.id,
	    			},
	    			function(data, status, xhr) {
	    				$("#actionForm").show();
	    				$("#actionForm").html(data);
	    			});
	        	}, "json");
    	} else {
    		$("#actionForm").hide();
    		alert('Should select any action to be added');
    	}
      });
    });
    
    jQuery(window).load(function() {
    	$('#show_hide').click(function() {
  			if ($('#extra_info').is(":visible")) {
  				$('#extra_info').hide();
  			} else {
  				$('#extra_info').show();
  			}
  		});
    });
    
    function deleteAction(am_id, aa_id) {
    	hideAll();
	  	$.get('Automation',	{
			action : 'loadMetadataForm',
			newAction : 'deleteAction',
			ar_id : '${ar.id}',
			am_id : am_id,
			aa_id : aa_id
			},
			function(data, status, xhr) {
				$("#actionForm").show();
				$("#actionForm").html(data);
		});
    }
    
    function editAction(am_id, aa_id) {
    	hideAll();
	  	$.get('Automation', {
			 action : 'loadMetadataForm',
			 newAction : 'editAction',
			 ar_id : '${ar.id}',
			 am_id : am_id,
			 aa_id : aa_id
			},
			function(data, status, xhr) {
				$("#actionForm").show();
				$("#actionForm").html(data);
		});
    }
    
    function deleteValidation(am_id, av_id) {
    	hideAll();
	  	$.get('Automation', {
			action : 'loadMetadataForm',
			newAction : 'deleteValidation',
			ar_id : '${ar.id}',
			am_id : am_id,
			av_id : av_id
			},
			function(data, status, xhr) {
				$("#validationForm").show();
				$("#validationForm").html(data);
		});
    }
    
    function editValidation(am_id, av_id) {
    	hideAll();
	  	$.get('Automation', {
			action : 'loadMetadataForm',
			newAction : 'editValidation',
			ar_id : '${ar.id}',
			am_id : am_id,
			av_id : av_id
			},
			function(data, status, xhr) {
				$("#validationForm").show();
				$("#validationForm").html(data);
		});
    }
    </script>
  <title>Automation definition</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Automation">Automation rules</a>
        </li>
        <li class="path">Automation definition</li>
      </ul>
      <br/>
        <table class="results" width="70%">
          <thead>
          	<tr class="fuzzy">
              <td colspan="7" align="left">
	          	<table id="info" style="white-space: nowrap;" width="175px">
	          	  <tr>
	          	  	<td valign="top">
	          	  	  <b>Automation rule</b> ${ar.name}
	          	  	</td>
	          		<td width="100%" align="right">
	          		  <a id="show_hide" href="#"><img title="Show / Hide" alt="Show / Hide" src="img/action/examine.png"/></a>
	          		</td>
	          	  </tr>
	          	  <tr>
	          	  	<td>
	          	  		<div id="extra_info">
	          	  		<table cellpadding="2" cellspacing="0">
	          	  		<tr>
	          	  			<td>Order</td>
	          	  			<td>${ar.order}</td>
	          	  		</tr>
	          	  		<tr>
	          	  			<td>Event</td>
	          	  			<td>${events.get(ar.event)}</td>
	          	  		</tr>
	          	  		<tr>
	          	  			<td>At</td>
	          	  			<td>${ar.at}</td>
	          	  		</tr>
	          	  		<tr>
	          	  			<td>Exclusive</td>
	          	  			<td>
	          	  				<c:choose>
				                  <c:when test="${ar.exclusive}">
				                    <img src="img/true.png" alt="Active" title="Active"/>
				                  </c:when>
				                  <c:otherwise>
				                    <img src="img/false.png" alt="Inactive" title="Inactive"/>
				                  </c:otherwise>
				                </c:choose>
	          	  			</td>
	          	  		</tr>
	          	  		<tr>
	          	  			<td>Active</td>
	          	  			<td>
	          	  				<c:choose>
				                  <c:when test="${ar.active}">
				                    <img src="img/true.png" alt="Active" title="Active"/>
				                  </c:when>
				                  <c:otherwise>
				                    <img src="img/false.png" alt="Inactive" title="Inactive"/>
				                  </c:otherwise>
				                </c:choose>
	          	  			</td>
	          	  		</tr>
	          	  		</table>
	          	  		</div>
	          	  	</td>
	          	  </tr>
	          	</table>
              </td>
            </tr>
          </thead>
    	  <tr>
    	  	<td colspan="6">
    	  	  <table>
		        <tr>
		          <td><b>Add validation:</b></td>
		          <td>
		            <select id="ma_id_validation">
		    		  <option value="">-</option>
		    		  <c:forEach var="mv" items="${metadaValidations}">
		    		    <option value="${mv.id}">${mv.name}</option>
		    		  </c:forEach>
		    	    </select>
		    	  </td>
		    	  <td>
		    	    <img id="addValidation" src="img/action/new.png" style="cursor:pointer; cursor:hand;" alt="New validation" title="New validation"/>
		    	  </td>
		        </tr>
		      </table>
		      <div align="center" id="validationForm"></div>
    	  	</td>
    	  </tr>
    	  <tr>
    	  	<th align="center" colspan="6">VALIDATIONS</th>
    	  </tr>
    	  <tr>
	      	<th>Order</th><th>Type</th><th>active</th><th>Param0</th><th>Param1</th><th></th>
		  </tr>
		  <c:forEach var="validation" items="${ar.validations}" varStatus="row">
    	    <c:set value="" var="description00"></c:set>
		    <c:set value="" var="description01"></c:set>
    	    <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
    	      <td>${validation.order}</td>
    	      <td>
    	      	<c:forEach var="mv" items="${metadaValidations}">
		    	  <c:if test="${mv.id == validation.type}">
		    	  	${mv.name}
		    	  	<c:set value="${mv.description00}" var="description00"></c:set>
		    	  	<c:set value="${mv.description01}" var="description01"></c:set>
		    	  </c:if>
		    	</c:forEach>
    	      </td>
    	      <td align="center">
                <c:choose>
                  <c:when test="${validation.active}">
                    <img src="img/true.png" alt="Active" title="Active"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="Inactive" title="Inactive"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
              	<c:if test="${validation.params.size() > 0 && description00 !=null && description00 ne ''}">
              	  <c:set value="${validation.params.get(0)}" var="param00"></c:set>
              	  ${description00}:${param00}
              	</c:if>
              </td>
              <td>
              	<c:if test="${validation.params.size() > 1 && description01 !=null && description01 ne ''}">
              	  <c:set value="${validation.params.get(1)}" var="param01"></c:set>
              	  ${description01}: ${param01}
              	</c:if>
              </td>
              <td align="center">
              <a href="javascript:editValidation(${validation.type}, ${validation.id})"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              &nbsp;
              <a href="javascript:deleteValidation(${validation.type}, ${validation.id})"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
            </td>
            </tr>
    	  </c:forEach>
    	  <tr>
    	  	<td colspan="6">
    	  	  <table>
		        <tr>
		          <td><b>Add action:</b></td>
		          <td>
		            <select id="ma_id_action">
		    		  <option value="">-</option>
		    		  <c:forEach var="ma" items="${metadaActions}">
		    		    <option value="${ma.id}">${ma.name}</option>
		    		  </c:forEach>
		    	    </select>
		    	  </td>
		    	  <td>
		    	    <img id="addAction" src="img/action/new.png" style="cursor:pointer; cursor:hand;" alt="New action" title="New action"/>
		    	  </td>
		        </tr>
		      </table>
		      <div align="center" id="actionForm"></div>
    	  	</td>
    	  </tr>
    	  <tr>
    	  	<th align="center" colspan="6">ACTIONS</th>
    	  </tr>
    	  <tr>
	      	<th>Order</th><th>Type</th><th>active</th><th>Param0</th><th>Param1</th><th></th>
		  </tr>
    	  <c:forEach var="action" items="${ar.actions}" varStatus="row">
    	    <c:set value="" var="description00"></c:set>
		    <c:set value="" var="description01"></c:set>
		    <c:set value="true" var="showParam0"></c:set>
    	    <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
    	      <td>${action.order}</td>
    	      <td>
    	      	<c:forEach var="ma" items="${metadaActions}">
		    	  <c:if test="${ma.id == action.type}">
		    	  	${ma.name}
		    	  	<c:if test="${ma.name == 'ExecuteScripting'}">
		    	  		<c:set value="false" var="showParam0"></c:set>
		    	  	</c:if>
		    	  	<c:set value="${ma.description00}" var="description00"></c:set>
		    	  	<c:set value="${ma.description01}" var="description01"></c:set>
		    	  </c:if>
		    	</c:forEach>
    	      </td>
    	      <td align="center">
                <c:choose>
                  <c:when test="${action.active}">
                    <img src="img/true.png" alt="Active" title="Active"/>
                  </c:when>
                  <c:otherwise>
                    <img src="img/false.png" alt="Inactive" title="Inactive"/>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
              	<c:if test="${action.params.size() > 0 && description00 !=null && description00 ne ''}">
              	  <c:set value="${action.params.get(0)}" var="param00"></c:set>
              	  <c:if test="${showParam0}">${description00}:${param00}</c:if>
              	</c:if>
              </td>
              <td>
              	<c:if test="${action.params.size() > 1 && description01 !=null && description01 ne ''}">
              	  <c:set value="${action.params.get(1)}" var="param01"></c:set>
              	  ${description01}: ${param01}
              	</c:if>
              </td>
              <td align="center">
              <a href="javascript:editAction(${action.type}, ${action.id})"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              &nbsp;
              <a href="javascript:deleteAction(${action.type}, ${action.id})"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
            </td>
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