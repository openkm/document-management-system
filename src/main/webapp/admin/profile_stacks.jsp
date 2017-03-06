<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Stacks</legend>
  <table>
    <tr>
      <td>Taxonomy</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.taxonomyVisible}">
            <input name="prf_stack_taxonomy_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_taxonomy_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Categories</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.categoriesVisible}">
            <input name="prf_stack_categories_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_categories_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Thesaurus</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.thesaurusVisible}">
            <input name="prf_stack_thesaurus_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_thesaurus_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Templates</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.templatesVisible}">
            <input name="prf_stack_templates_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_templates_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Personal</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.personalVisible}">
            <input name="prf_stack_personal_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_personal_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Mail</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.mailVisible}">
            <input name="prf_stack_mail_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_mail_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Metadata</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.metadataVisible}">
            <input name="prf_stack_metadata_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_metadata_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Trash</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfStack.trashVisible}">
            <input name="prf_stack_trash_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_trash_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>