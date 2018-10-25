<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<u:constantsMap className="com.openkm.core.Config" var="Config"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link rel="Shortcut icon" href="favicon.ico"/>
  <link rel="stylesheet" type="text/css" href="../css/chosen.css"/>
  <link rel="stylesheet" type="text/css" href="css/admin-style.css"/>
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/chosen.jquery.js"></script>
  <script type="text/javascript">
    function hideAll() {
      $("#actionForm").hide();
      $("#validationForm").hide();
    }

    $(document).ready(function () {
      $('select#ma_classname_validation').chosen({disable_search_threshold: 10});
      $('select#ma_classname_action').chosen({disable_search_threshold: 10});
      $('#extra_info').hide();
      hideAll();

      $("#addValidation").click(function (event) {
        var value = $("#ma_classname_validation").val();
        if (value != '') {
          $.get('Automation', {
              action: "getMetadata",
              am_className: $("#ma_classname_validation").val()
            },
            function (data) {
              $.get('Automation', {
                  action: 'loadMetadataForm',
                  newAction: 'createValidation',
                  ar_id: '${ar.id}',
                  am_className: data.className
                },
                function (data, status, xhr) {
                  $("#validationForm").show();
                  $("#validationForm").html(data);
                });
            }, "json");
        } else {
          $("#actionForm").hide();
          alert('Please, select a validation');
        }
      });

      $("#addAction").click(function (event) {
        hideAll();
        var value = $("#ma_classname_action").val();
        if (value != '') {
          $.get('Automation', {
              action: "getMetadata",
              am_className: $("#ma_classname_action").val()
            },
            function (data) {
              $.get('Automation', {
                  action: 'loadMetadataForm',
                  newAction: 'createAction',
                  ar_id: '${ar.id}',
                  am_className: data.className
                },
                function (data, status, xhr) {
                  $("#actionForm").show();
                  $("#actionForm").html(data);
                });
            }, "json");
        } else {
          $("#actionForm").hide();
          alert('Please, select an action');
        }
      });
    });

    jQuery(window).load(function () {
      $('#show_hide').click(function () {
        if ($('#extra_info').is(":visible")) {
          $('#extra_info').hide();
        } else {
          $('#extra_info').show();
        }
      });
    });

    function deleteAction(am_className, aa_id) {
      hideAll();
      $.get('Automation', {
          action: 'loadMetadataForm',
          newAction: 'deleteAction',
          ar_id: '${ar.id}',
          am_className: am_className,
          aa_id: aa_id
        },
        function (data, status, xhr) {
          $("#actionForm").show();
          $("#actionForm").html(data);
        });
    }

    function editAction(am_className, aa_id) {
      hideAll();
      $.get('Automation', {
          action: 'loadMetadataForm',
          newAction: 'editAction',
          ar_id: '${ar.id}',
          am_className: am_className,
          aa_id: aa_id
        },
        function (data, status, xhr) {
          $("#actionForm").show();
          $("#actionForm").html(data);
        });
    }

    function deleteValidation(am_className, av_id) {
      hideAll();
      $.get('Automation', {
          action: 'loadMetadataForm',
          newAction: 'deleteValidation',
          ar_id: '${ar.id}',
          am_className: am_className,
          av_id: av_id
        },
        function (data, status, xhr) {
          $("#validationForm").show();
          $("#validationForm").html(data);
        });
    }

    function editValidation(am_className, av_id) {
      hideAll();
      $.get('Automation', {
          action: 'loadMetadataForm',
          newAction: 'editValidation',
          ar_id: '${ar.id}',
          am_className: am_className,
          av_id: av_id
        },
        function (data, status, xhr) {
          $("#validationForm").show();
          $("#validationForm").html(data);
        });
    }
  </script>
  <title>Automation definition</title>
</head>
<body>
  <c:choose>
    <c:when test="${u:isAdmin()}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Automation">Automation rules</a>
        </li>
        <li class="path">Automation definition</li>
      </ul>
      <br/>
      <c:if test="${!empty pathNotFound && pathNotFound == true}">
        <div class="error"><h3>Rule is associated with a node which no longer exists.</h3></div>
      </c:if>
      <table class="results-old" width="70%">
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
          <td colspan="7">
            <c:if test="${action ne 'viewDefinition'}">
              <table>
                <tr>
                  <td><b>Add validation:</b></td>
                  <td>
                    <select id="ma_classname_validation" style="width: 275px" data-placeholder="Select validation">
                      <option value="">&nbsp;</option>
                      <c:forEach var="mv" items="${metadaValidations}">
                        <option value="${mv.className}">${mv.name}</option>
                      </c:forEach>
                    </select>
                  </td>
                  <td>
                    <img id="addValidation" src="img/action/new.png" style="cursor:pointer; cursor:hand;" alt="New validation" title="New validation"/>
                  </td>
                </tr>
              </table>
            </c:if>
          </td>
        </tr>
        <tr>
          <th align="center" colspan="7">VALIDATIONS</th>
        </tr>
        <tr>
          <th>Order</th>
          <th>Type</th>
          <th>active</th>
          <th>Param0</th>
          <th>Param1</th>
          <th>Param2</th>
          <th></th>
        </tr>
        <c:forEach var="validation" items="${ar.validations}" varStatus="row">
          <c:set value="" var="description00"></c:set>
          <c:set value="" var="description01"></c:set>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${validation.order}</td>
            <td>
              <c:forEach var="mv" items="${metadaValidations}">
                <c:if test="${mv.className == validation.className}">
                  ${mv.name}
                  <c:set value="${mv.description00}" var="description00"></c:set>
                  <c:set value="${mv.description01}" var="description01"></c:set>
                  <c:set value="${mv.description02}" var="description02"></c:set>
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
            <td>
              <c:if test="${validation.params.size() > 2 && description02 !=null && description02 ne ''}">
                <c:set value="${validation.params.get(2)}" var="param02"></c:set>
                ${description02}: ${param02}
              </c:if>
            </td>
            <td align="center">
              <c:if test="${action ne 'viewDefinition'}">
                <a href="javascript:editValidation('${validation.className}', '${validation.id}')"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
                &nbsp;
                <a href="javascript:deleteValidation('${validation.className}', '${validation.id}')"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <tr>
          <td colspan="7" align="left">
            <div align="center" id="validationForm"></div>
          </td>
        </tr>
        <tr>
          <td colspan="7">
            <c:if test="${action ne 'viewDefinition'}">
              <table>
                <tr>
                  <td><b>Add action:</b></td>
                  <td>
                    <select id="ma_classname_action" style="width: 275px" data-placeholder="Select action">
                      <option value="">&nbsp;</option>
                      <c:forEach var="ma" items="${metadaActions}">
                        <option value="${ma.className}">${ma.name}</option>
                      </c:forEach>
                    </select>
                  </td>
                  <td>
                    <img id="addAction" src="img/action/new.png" style="cursor:pointer; cursor:hand;" alt="New action" title="New action"/>
                  </td>
                </tr>
              </table>
            </c:if>
          </td>
        </tr>
        <tr>
          <th align="center" colspan="7">ACTIONS</th>
        </tr>
        <tr>
          <th>Order</th>
          <th>Type</th>
          <th>active</th>
          <th>Param0</th>
          <th>Param1</th>
          <th>Param2</th>
          <th></th>
        </tr>
        <c:forEach var="action" items="${ar.actions}" varStatus="row">
          <c:set value="" var="description00"></c:set>
          <c:set value="" var="description01"></c:set>
          <c:set value="true" var="showParam0"></c:set>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${action.order}</td>
            <td>
              <c:forEach var="ma" items="${metadaActions}">
                <c:if test="${ma.className == action.className}">
                  ${ma.name}
                  <c:if test="${ma.name == 'ExecuteScripting'}">
                    <c:set value="false" var="showParam0"></c:set>
                  </c:if>
                  <c:set value="${ma.description00}" var="description00"></c:set>
                  <c:set value="${ma.description01}" var="description01"></c:set>
                  <c:set value="${ma.description02}" var="description02"></c:set>
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
            <td>
              <c:if test="${action.params.size() > 2 && description02 !=null && description02 ne ''}">
                <c:set value="${action.params.get(2)}" var="param02"></c:set>
                ${description02}: ${param02}
              </c:if>
            </td>
            <td align="center">
              <c:if test="${action ne 'viewDefinition'}">
                <a href="javascript:editAction('${action.className}', '${action.id}')"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
                &nbsp;
                <a href="javascript:deleteAction('${action.className}', '${action.id}')"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <tr>
          <td colspan="7" align="left">
            <div align="center" id="actionForm"></div>
          </td>
        </tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>