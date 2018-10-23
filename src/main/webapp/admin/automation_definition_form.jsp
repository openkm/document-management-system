<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<c:choose>
  <c:when test="${u:isAdmin()}">
    <br>
    <script type="text/javascript" src="../js/vanadium-min.js"></script>
    <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
    <script type="text/javascript">
      $(document).ready(function () {
        $dm = $('.ds').openDOMWindow({
          height: 300, width: 400,
          eventType: 'click',
          overlayOpacity: '57',
          windowSource: 'iframe', windowPadding: 0
        });
      });

      function dialogClose() {
        $dm.closeDOMWindow();
      }

      function hideValidation() {
        $('#validationForm').hide();
        $('#validationForm').html('');
      }

      function hideAction() {
        $('#actionForm').hide();
        $('#actionForm').html('');
      }
    </script>
    <c:set value="" var="readonly"></c:set>
    <c:if test="${fn:startsWith(action, 'delete')}">
      <script type="text/javascript">
        $(document).ready(function () {
          $("#am_active").attr('disabled', 'disabled');
          $("#am_order").attr('readonly', true);
        });
      </script>
      <c:set value="readonly=\"readonly\"" var="readonly"></c:set>
    </c:if>
    <form action="Automation" id="form" method="post">
      <input type="hidden" value="${action}" name="action"/>
      <input type="hidden" value="${ar_id}" name="ar_id"/>
      <input type="hidden" value="${am.className}" name="am_className"/>
      <input type="hidden" value="${aa_id}" name="aa_id"/>
      <input type="hidden" value="${av_id}" name="av_id"/>
      <table class="form" border="0" cellspacing="0" cellpadding="2">
        <tr>
          <th colspan="2" align="center">
            <c:choose>
              <c:when test="${action=='createValidation' || action=='createAction'}"><b>Create</b></c:when>
              <c:when test="${action == 'editAction' || action == 'editValidation'}"><b>Edit</b></c:when>
              <c:when test="${action == 'deleteAction' || action == 'deleteValidation'}"><b>Delete</b></c:when>
            </c:choose>
          </th>
        </tr>
        <tr>
          <td><b>Name</b></td>
          <td>${am.name}</td>
        </tr>
        <tr>
          <td><b>ClassName</b></td>
          <td>${am.className}</td>
        </tr>
        <tr>
          <td>Active</td>
          <td>
            <c:choose>
              <c:when test="${am.active}">
                <input name="am_active" type="checkbox" checked="checked" id="am_active"/>
              </c:when>
              <c:otherwise>
                <input name="am_active" type="checkbox" id="am_active"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td colspan="2"><b>Parameters:</b></td>
        </tr>
        <tr bgcolor="white">
          <td>Order</td>
          <td>
            <input class=":integer :required :only_on_blur" size="5" maxlength="4" name="am_order" id="am_order"
                   value="${am_order}" id="am_order">
          </td>
        </tr>
        <c:choose>
          <c:when test="${am.type00 !=null && am.description00 !=null && am.type00 !='' && am.description00 != ''}">
            <tr bgcolor="white">
              <td>${am.description00}</td>
              <td><u:automationFormElement type="${am.type00}" source="${am.source00}" value="${am_param00}"
                                           name="am_param00" readonly="${readonly!=''}"/></td>
            </tr>
          </c:when>
        </c:choose>
        <c:choose>
          <c:when test="${am.type01 !=null && am.description01 !=null && am.type01!='' && am.description01 !=''}">
            <tr bgcolor="white">
              <td>${am.description01}</td>
              <td><u:automationFormElement type="${am.type01}" source="${am.source01}" value="${am_param01}"
                                           name="am_param01" readonly="${readonly!=''}"/></td>
            </tr>
          </c:when>
        </c:choose>
        <c:choose>
          <c:when test="${am.type02 !=null && am.description02 !=null && am.type02!='' && am.description02 !=''}">
            <tr bgcolor="white">
              <td>${am.description02}</td>
              <td><u:automationFormElement type="${am.type02}" source="${am.source02}" value="${am_param02}"
                                           name="am_param02" readonly="${readonly!=''}"/></td>
            </tr>
          </c:when>
        </c:choose>
        <tr>
          <td colspan="2" align="right">
            <c:choose>
              <c:when test="${action=='createValidation' || action=='deleteValidation' || action=='editValidation'}">
                <input type="button" onclick="javascript:hideValidation()" value="Cancel" class="noButton"/>
              </c:when>
              <c:when test="${action=='createAction' || action=='deleteAction' || action=='editAction'}">
                <input type="button" onclick="javascript:hideAction()" value="Cancel" class="noButton"/>
              </c:when>
            </c:choose>
            <c:choose>
              <c:when test="${action=='createValidation' || action=='createAction'}">
              	<input type="submit" value="Create" class="yesButton"/>
              </c:when>
              <c:when test="${action == 'editAction' || action == 'editValidation'}">
              	<input type="submit" value="Edit" class="yesButton"/>
              </c:when>
              <c:when test="${action == 'deleteAction' || action == 'deleteValidation'}">
              	<input type="submit" value="Delete" class="yesButton"/>
              </c:when>
            </c:choose>
          </td>
        </tr>
      </table>
    </form>
    <br>
  </c:when>
  <c:otherwise>
    <div class="error"><h3>Only admin users allowed</h3></div>
  </c:otherwise>
</c:choose>