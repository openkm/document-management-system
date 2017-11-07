<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fieldset>
  <legend>Toolbar</legend>
  <table>
    <tr>
      <td>Create folder</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.createFolderVisible}">
            <input name="prf_toolbar_create_folder_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_create_folder_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Find folders</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.findFolderVisible}">
            <input name="prf_toolbar_find_folder_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_find_folder_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Find documents</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.findDocumentVisible}">
            <input name="prf_toolbar_find_document_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_find_document_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Similar documents</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.similarDocumentVisible}">
            <input name="prf_toolbar_similar_document_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_similar_document_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Download</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.downloadVisible}">
            <input name="prf_toolbar_download_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_download_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Download PDF</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.downloadPdfVisible}">
            <input name="prf_toolbar_download_pdf_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_download_pdf_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Print</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.printVisible}">
            <input name="prf_toolbar_print_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_print_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Lock</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.lockVisible}">
            <input name="prf_toolbar_lock_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_lock_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Unlock</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.unlockVisible}">
            <input name="prf_toolbar_unlock_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_unlock_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add document</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.addDocumentVisible}">
            <input name="prf_toolbar_add_document_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_add_document_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Edit</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.checkoutVisible}">
            <input name="prf_toolbar_checkout_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_checkout_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Update</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.checkinVisible}">
            <input name="prf_toolbar_checkin_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_checkin_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Cancel edit</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.cancelCheckoutVisible}">
            <input name="prf_toolbar_cancel_checkout_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_cancel_checkout_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Delete</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.deleteVisible}">
            <input name="prf_toolbar_delete_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_delete_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add metadata group</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.addPropertyGroupVisible}">
            <input name="prf_toolbar_add_property_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_add_property_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove metadata group</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.removePropertyGroupVisible}">
            <input name="prf_toolbar_remove_property_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_remove_property_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Start workflow</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.startWorkflowVisible}">
            <input name="prf_toolbar_start_workflow_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_start_workflow_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add subscription</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.addSubscriptionVisible}">
            <input name="prf_toolbar_add_subscription_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_add_subscription_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove subscription</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.removeSubscriptionVisible}">
            <input name="prf_toolbar_remove_subscription_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_remove_subscription_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Refresh</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.refreshVisible}">
            <input name="prf_toolbar_refresh_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_refresh_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Home</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.homeVisible}">
            <input name="prf_toolbar_home_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_home_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Splitter resize</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.splitterResizeVisible}">
            <input name="prf_toolbar_splitter_resize_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_splitter_resize_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td nowrap>OMR</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfToolbar.omrVisible}">
            <input name="prf_toolbar_omr_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_toolbar_omr_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>