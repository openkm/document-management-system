<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Dashboard</legend>
  <table>
    <tr>
      <td>User</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfDashboard.userVisible}">
            <input name="prf_dashboard_user_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_user_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Mail</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfDashboard.mailVisible}">
            <input name="prf_dashboard_mail_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_mail_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>News</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfDashboard.newsVisible}">
            <input name="prf_dashboard_news_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_news_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>General</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfDashboard.generalVisible}">
            <input name="prf_dashboard_general_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_general_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Workflow</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfDashboard.workflowVisible}">
            <input name="prf_dashboard_workflow_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_workflow_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Keywords</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfDashboard.keywordsVisible}">
            <input name="prf_dashboard_keywords_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_keywords_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>