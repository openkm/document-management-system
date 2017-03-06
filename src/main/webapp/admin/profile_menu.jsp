<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu</legend>
  <table>
    <tr>
      <td>File</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.fileVisible}">
            <input name="prf_menu_file_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Edit</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.editVisible}">
            <input name="prf_menu_edit_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Tools</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.toolsVisible}">
            <input name="prf_menu_tools_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tools_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Bookmarks</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.bookmarksVisible}">
            <input name="prf_menu_bookmarks_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_bookmarks_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Templates</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.templatesVisible}">
            <input name="prf_menu_templates_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_templates_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Help</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.helpVisible}">
            <input name="prf_menu_help_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>