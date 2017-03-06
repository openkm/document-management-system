<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu help</legend>
  <table>
    <tr>
      <td>Documentation</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.documentationVisible}">
            <input name="prf_menu_help_documentation_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_documentation_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Bug tracking</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.bugTrackingVisible}">
            <input name="prf_menu_help_bug_tracking_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_bug_tracking_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Support</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.supportVisible}">
            <input name="prf_menu_help_support_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_support_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Forum</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.forumVisible}">
            <input name="prf_menu_help_forum_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_forum_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Changelog</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.changelogVisible}">
            <input name="prf_menu_help_changelog_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_changelog_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Web site</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.webSiteVisible}">
            <input name="prf_menu_help_web_site_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_web_site_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>About</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.aboutVisible}">
            <input name="prf_menu_help_about_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_about_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>