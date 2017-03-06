<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Chat</legend>
  <table>
    <tr>
      <td>Enabled</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfChat.chatEnabled}">
            <input name="prf_chat_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_chat_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Auto login</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfChat.autoLoginEnabled}">
            <input name="prf_chat_auto_login" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_chat_auto_login" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>