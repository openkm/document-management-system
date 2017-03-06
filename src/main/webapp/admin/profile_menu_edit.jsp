<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu edit</legend>
  <table>
    <tr>
      <td>Lock</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.lockVisible}">
            <input name="prf_menu_edit_lock_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_lock_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Unlock</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.unlockVisible}">
            <input name="prf_menu_edit_unlock_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_unlock_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Update</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.checkInVisible}">
            <input name="prf_menu_edit_check_in_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_check_in_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Edit</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.checkOutVisible}">
            <input name="prf_menu_edit_check_out_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_check_out_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Cancel Edit</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.cancelCheckOutVisible}">
            <input name="prf_menu_edit_cancel_check_out_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_cancel_check_out_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Delete</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.deleteVisible}">
            <input name="prf_menu_edit_delete_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_delete_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Rename</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.renameVisible}">
            <input name="prf_menu_edit_rename_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_rename_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Copy</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.copyVisible}">
            <input name="prf_menu_edit_copy_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_copy_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Move</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.moveVisible}">
            <input name="prf_menu_edit_move_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_move_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add subscription</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.addSubscriptionVisible}">
            <input name="prf_menu_edit_add_subscription_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_add_subscription_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove subscription</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.removeSubscriptionVisible}">
            <input name="prf_menu_edit_remove_subscription_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_remove_subscription_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add metadata group</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.addPropertyGroupVisible}">
            <input name="prf_menu_edit_add_property_group_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_add_property_group_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Update metadata group</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.updatePropertyGroupVisible}">
            <input name="prf_menu_edit_update_property_group_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_update_property_group_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove metadata group</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.removePropertyGroupVisible}">
            <input name="prf_menu_edit_remove_property_group_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_remove_property_group_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add note</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.addNoteVisible}">
            <input name="prf_menu_edit_add_note_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_add_note_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove note</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.removeNoteVisible}">
            <input name="prf_menu_edit_remove_note_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_remove_note_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add category</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.addCategoryVisible}">
            <input name="prf_menu_edit_add_category_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_add_category_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove category</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.removeCategoryVisible}">
            <input name="prf_menu_edit_remove_category_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_remove_category_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add keyword</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.addKeywordVisible}">
            <input name="prf_menu_edit_add_keyword_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_add_keyword_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Remove keyword</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.removeKeywordVisible}">
            <input name="prf_menu_edit_remove_keyword_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_remove_keyword_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Merge PDF</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfEdit.mergePdfVisible}">
            <input name="prf_menu_edit_merge_pdf_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_merge_pdf_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>