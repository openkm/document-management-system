<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<fieldset>
  <legend>File browser</legend>
  <table>
  	<tr>
      <td>Width</td>
      <td></td>
      <td></td>
  	</tr>
    <tr>
      <td> 
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_status_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.statusWidth}"/>
      </td>
      <td>Status</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.statusVisible}">
            <input name="prf_filebrowser_status_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_status_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_massive_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.massiveWidth}"/>
      </td>
      <td>Massive</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.massiveVisible}">
            <input name="prf_filebrowser_massive_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_massive_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_icon_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.iconWidth}"/>
      </td>
      <td>Icon</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.iconVisible}">
            <input name="prf_filebrowser_icon_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_icon_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_name_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.nameWidth}"/>
      </td>
      <td>Name</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.nameVisible}">
            <input name="prf_filebrowser_name_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_name_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_size_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.sizeWidth}"/>
      </td>
      <td>Size</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.sizeVisible}">
            <input name="prf_filebrowser_size_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_size_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_lastmod_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.lastModifiedWidth}"/>
      </td>
      <td>Last modified</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.lastModifiedVisible}">
            <input name="prf_filebrowser_lastmod_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_lastmod_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_author_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.authorWidth}"/>
      </td>
      <td>Author</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.authorVisible}">
            <input name="prf_filebrowser_author_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_author_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_version_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.versionWidth}"/>
      </td>
      <td>Version</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfFileBrowser.versionVisible}">
            <input name="prf_filebrowser_version_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_filebrowser_version_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    
    <!-- Additional columns -->
    <tr>
      <td colspan="3">Column 1</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column0_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column0Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column0" id="prf_filebrowser_column0" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column0 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 2</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column1_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column1Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column1" id="prf_filebrowser_column1" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column1 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 3</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column2_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column2Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column2" id="prf_filebrowser_column2" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column2 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 4</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column3_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column3Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column3" id="prf_filebrowser_column3" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column3 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 5</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column4_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column4Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column4" id="prf_filebrowser_column4" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column4 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 6</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column5_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column5Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column5" id="prf_filebrowser_column5" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column5 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 7</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column6_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column6Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column6" id="prf_filebrowser_column6" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column6 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 8</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column7_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column7Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column7" id="prf_filebrowser_column7" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column7 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 9</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column8_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column8Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column8" id="prf_filebrowser_column8" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column8 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td colspan="3">Column 10</td>
    </tr>
    <tr>
      <td>
      	<div id="error_integer" style="display:none; color : red;">Expected integer.</div>
      	<input class=":required;; :integer;;error_integer :only_on_blur" name="prf_filebrowser_column9_width" size="3" maxlength="3" type="text" value="${prf.prfFileBrowser.column9Width}"/>
      </td>
      <td colspan="2">
        <select name="prf_filebrowser_column9" id="prf_filebrowser_column9" data-placeholder="Select property" style="width: 200px">
          <option/>
          <c:forEach var="prop" items="${pgprops}">
            <c:choose>
              <c:when test="${prf.prfFileBrowser.column9 == prop}">
                <option value="${prop}" selected="selected">${prop}</option>
              </c:when>
              <c:otherwise>
                <option value="${prop}">${prop}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
  </table>
</fieldset>