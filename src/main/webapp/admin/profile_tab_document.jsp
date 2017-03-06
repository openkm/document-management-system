<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Tab document</legend>
  <table>
    <tr>
      <td>Properties</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.propertiesVisible}">
            <input name="prf_tab_document_properties_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_properties_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Security</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.securityVisible}">
            <input name="prf_tab_document_security_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_security_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Notes</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.notesVisible}">
            <input name="prf_tab_document_notes_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_notes_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Versions</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.versionsVisible}">
            <input name="prf_tab_document_versions_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_versions_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Version download</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.versionDownloadVisible}">
            <input name="prf_tab_document_version_download_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_version_download_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Preview</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.previewVisible}">
            <input name="prf_tab_document_preview_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_preview_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Metadata groups</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.prfDocument.propertyGroupsVisible}">
            <input name="prf_tab_document_property_groups_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_property_groups_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>