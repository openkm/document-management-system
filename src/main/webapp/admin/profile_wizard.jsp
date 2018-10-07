<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<fieldset>
  <legend>Wizard</legend>
  <table>
    <tr>
      <td>Metadata groups</td>
      <td>
        <select name="prf_wizard_property_groups" id="prf_wizard_property_groups" data-placeholder="Select metadata group" multiple="multiple" style="width: 250px">
          <c:forEach var="pg" items="${pgroups}">
            <c:choose>
              <c:when test="${u:contains(prf.prfWizard.propertyGroups, pg.name)}">
                <option value="${pg.name}" selected="selected">${pg.label}</option>
              </c:when>
              <c:otherwise>
                <option value="${pg.name}">${pg.label}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td>Workflows</td>
      <td>
        <select name="prf_wizard_workflows" id="prf_wizard_workflows" data-placeholder="Select workflow" multiple="multiple" style="width: 250px">
          <c:forEach var="wf" items="${wflows}">
            <c:choose>
              <c:when test="${u:contains(prf.prfWizard.workflows, wf.name)}">
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
    <tr>
      <td>Keywords</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfWizard.keywordsEnabled}">
            <input name="prf_wizard_keywords" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_wizard_keywords" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Categories</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfWizard.categoriesEnabled}">
            <input name="prf_wizard_categories" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_wizard_categories" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>