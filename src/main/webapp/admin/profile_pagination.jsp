<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Pagination</legend>
  <table>
    <tr>
      <td>Enabled</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfPagination.paginationEnabled}">
            <input name="prf_pagination_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_pagination_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Page list</td>
      <td>
        <input class=":required :only_on_blur" name="prf_pagination_page_list" value="${prf.prfPagination.pageList}"/>
      </td>
    </tr>
    <tr>
      <td>Type filter</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfPagination.typeFilterEnabled}">
            <input name="prf_pagination_type_filter_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_pagination_type_filter_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Misc filter</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfPagination.miscFilterEnabled}">
            <input name="prf_pagination_misc_filter_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_pagination_misc_filter_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Show folders by default</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfPagination.showFoldersEnabled}">
            <input name="prf_pagination_show_folders_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_pagination_show_folders_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Show documents by default</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfPagination.showDocumentsEnabled}">
            <input name="prf_pagination_show_documents_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_pagination_show_documents_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Show mails by default</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfPagination.showMailsEnabled}">
            <input name="prf_pagination_show_mails_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_pagination_show_mails_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>