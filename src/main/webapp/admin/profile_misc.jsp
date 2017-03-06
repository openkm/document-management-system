<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<fieldset>
  <legend>Misc</legend>
  <table>
    <tr>
      <td>Quota limit (MB)</td>
      <td><input class=":integer :only_on_blur" name="prf_misc_user_quota" value="${prf.prfMisc.userQuota}" size="10"/></td>
    </tr>
    <tr>
      <td>Advanced filters</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.advancedFilters}">
            <input name="prf_misc_advanced_filter" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_advanced_filter" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Web skin</td>
      <td>
        <select name="prf_misc_web_skin">
          <c:choose>
            <c:when test="${prf.prfMisc.webSkin == 'default'}">
              <option value="default" selected="selected">Default</option>
            </c:when>
            <c:otherwise><option value="default">Default</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.prfMisc.webSkin == 'mediumfont'}">
              <option value="mediumfont" selected="selected">Medium font</option>
            </c:when>
            <c:otherwise><option value="mediumfont">Medium font</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.prfMisc.webSkin == 'bigfont'}">
              <option value="bigfont" selected="selected">Big font</option>
            </c:when>
            <c:otherwise><option value="bigfont">Big font</option></c:otherwise>
          </c:choose>
        </select>
      </td>
    </tr>
    <tr>
      <td>Print preview</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.printPreview}">
            <input name="prf_misc_print_preview" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_print_preview" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Keywords enabled</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.keywordsEnabled}">
            <input name="prf_misc_keywords_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_keywords_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Upload notify users</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.uploadNotifyUsers}">
            <input name="prf_misc_upload_notify_users" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_upload_notify_users" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Notify external users</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.notifyExternalUsers}">
            <input name="prf_misc_notify_external_users" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_notify_external_users" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Acrobat plugin preview</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.acrobatPluginPreview}">
            <input name="prf_misc_acrobat_plugin_preview" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_acrobat_plugin_preview" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Increase version</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMisc.increaseVersion}">
            <input name="prf_misc_increase_version" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_increase_version" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Extensions</td>
      <td>
        <select multiple="multiple" name="prf_misc_extensions" size="4">
          <c:forEach var="ext" items="${exts}">
            <c:choose>
              <c:when test="${u:contains(prf.prfMisc.extensions, ext.uuid)}">
                <option value="${ext.uuid}" selected="selected">${ext.name}</option>
              </c:when>
              <c:otherwise>
                <option value="${ext.uuid}">${ext.name}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td>Reports</td>
      <td>
        <select multiple="multiple" name="prf_misc_reports" size="4">
          <c:forEach var="rep" items="${reps}">
            <c:choose>
              <c:when test="${u:contains(prf.prfMisc.reports, rep.id)}">
                <option value="${rep.id}" selected="selected">${rep.name}</option>
              </c:when>
              <c:otherwise>
                <option value="${rep.id}">${rep.name}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td>Workflows</td>
      <td>
        <select multiple="multiple" name="prf_misc_workflows" size="4">
          <c:forEach var="wf" items="${wflows}">
            <c:choose>
              <c:when test="${u:contains(prf.prfMisc.workflows, wf.name)}">
                <option value="${wf.name}" selected="selected">${wf.name}</option>
              </c:when>
              <c:otherwise>
                <option value="${wf.name}">${wf.name}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
  </table>
</fieldset>