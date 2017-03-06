<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu bookmark</legend>
  <table>
    <tr>
      <td>Manage bookmarks</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfBookmark.manageBookmarksVisible}">
            <input name="prf_menu_bookmark_manage_bookmarks_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_bookmark_manage_bookmarks_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add bookmark</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfBookmark.addBookmarkVisible}">
            <input name="prf_menu_bookmark_add_bookmark_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_bookmark_add_bookmark_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Set home</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfBookmark.setHomeVisible}">
            <input name="prf_menu_bookmark_set_home_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_bookmark_set_home_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Go home</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfBookmark.goHomeVisible}">
            <input name="prf_menu_bookmark_go_home_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_bookmark_go_home_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>