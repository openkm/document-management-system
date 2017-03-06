<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Tab</legend>
  <table>
  <tr>
      <td>Default tab</td>
      <td>
        <select name="prf_tab_default">
          <c:choose>
            <c:when test="${prf.prfTab.defaultTab == 'desktop'}">
              <option value="desktop" selected="selected">Desktop</option>
            </c:when>
            <c:otherwise><option value="desktop">Desktop</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.prfTab.defaultTab == 'search'}">
              <option value="search" selected="selected">Search</option>
            </c:when>
            <c:otherwise><option value="search">Search</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.prfTab.defaultTab == 'dashboard'}">
              <option value="dashboard" selected="selected">Dashboard</option>
            </c:when>
            <c:otherwise><option value="dashboard">Dashboard</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.prfTab.defaultTab == 'administration'}">
              <option value="administration" selected="selected">Administration</option>
            </c:when>
            <c:otherwise><option value="administration">Administration</option></c:otherwise>
          </c:choose>
        </select>
      </td>
    </tr>
    <tr>
      <td>Desktop</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.desktopVisible}">
            <input name="prf_tab_desktop_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_desktop_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Search</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.searchVisible}">
            <input name="prf_tab_search_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_search_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Dashboard</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.dashboardVisible}">
            <input name="prf_tab_dashboard_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_dashboard_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Administration</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfTab.administrationVisible}">
            <input name="prf_tab_administration_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_administration_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>