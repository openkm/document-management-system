<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>General</legend>
  <table>
    <tr>
      <td>Name</td>
      <td>
        <c:choose>
          <c:when test="${action == 'delete' || prf.name eq 'Default'}">
            <input class=":required :only_on_blur" name="prf_name" value="${prf.name}" readonly="readonly"/>
          </c:when>
          <c:otherwise>
            <input class=":required :only_on_blur" name="prf_name" value="${prf.name}"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Active</td>
      <td>
        <c:choose>
          <c:when test="${prf.active}">
            <input name="prf_active" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_active" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>