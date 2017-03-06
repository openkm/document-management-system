<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Tab mail</legend>
  <table>
    <tr>
      <td>Properties</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfMail.propertiesVisible}">
            <input name="prf_tab_mail_properties_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_mail_properties_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>View</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfMail.previewVisible}">
            <input name="prf_tab_mail_preview_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_mail_preview_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Security</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfMail.securityVisible}">
            <input name="prf_tab_mail_security_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_mail_security_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Notes</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfMail.notesVisible}">
            <input name="prf_tab_mail_notes_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_mail_notes_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>