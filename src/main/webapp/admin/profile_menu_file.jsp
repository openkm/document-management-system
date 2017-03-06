<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu file</legend>
  <table>
    <tr>
      <td>Create folder</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.createFolderVisible}">
            <input name="prf_menu_file_create_folder_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_create_folder_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Find folders</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.findFolderVisible}">
            <input name="prf_menu_file_find_folder_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_find_folder_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Find documents</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.findDocumentVisible}">
            <input name="prf_menu_file_find_document_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_find_document_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Similar documents</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.similarDocumentVisible}">
            <input name="prf_menu_file_similar_document_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_similar_document_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Go folder</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.goFolderVisible}">
            <input name="prf_menu_file_go_folder_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_go_folder_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Download</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.downloadVisible}">
            <input name="prf_menu_file_download_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_download_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Download PDF</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.downloadPdfVisible}">
            <input name="prf_menu_file_download_pdf_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_download_pdf_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Add document</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.addDocumentVisible}">
            <input name="prf_menu_file_add_document_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_add_document_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Purge</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.purgeVisible}">
            <input name="prf_menu_file_purge_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_purge_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Purge trash</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.purgeTrashVisible}">
            <input name="prf_menu_file_purge_trash_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_purge_trash_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Restore</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.restoreVisible}">
            <input name="prf_menu_file_restore_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_restore_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Start workflow</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.startWorkflowVisible}">
            <input name="prf_menu_file_start_workflow_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_start_workflow_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Refresh</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.refreshVisible}">
            <input name="prf_menu_file_refresh_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_refresh_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Export</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.exportVisible}">
            <input name="prf_menu_file_export_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_export_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Create from template</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.createFromTemplateVisible}">
            <input name="prf_menu_file_create_from_template_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_create_from_template_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Send document link</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.sendDocumentLinkVisible}">
            <input name="prf_menu_file_send_document_link_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_send_document_link_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Send document attachment</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.sendDocumentAttachmentVisible}">
            <input name="prf_menu_file_send_document_attachment_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_send_document_attachment_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Forward e-mail</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfFile.forwardMailVisible}">
            <input name="prf_menu_file_forward_mail_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_forward_mail_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>