/**
 * Scroll to bottom permanently
 */
function scrollToBottom() {
	var bottom = document.body.scrollHeight;
	var current = window.innerHeight + document.body.scrollTop;
	if (bottom-current > 0) {
		window.scrollTo(0, bottom);
		setTimeout('scrollToBottom()', 1000);
	}
}

/**
 * Browser detection
 */
function getUserAgent() {
  try {
    if (window.opera) return 'opera';
    var ua = navigator.userAgent.toLowerCase();
    if (ua.indexOf('chrome') != -1) return 'chrome';
    if (ua.indexOf('webkit' ) != -1) return 'safari';
    if (ua.indexOf('msie 6.0') != -1) return 'ie6';
    if (ua.indexOf('msie 7.0') != -1) return 'ie7';
    if (ua.indexOf('msie 8.0') != -1) return 'ie8';
    if (ua.indexOf('msie 9.0') != -1) return 'ie9';
    if (ua.indexOf('msie 10.0') != -1) return 'ie10';
    if (ua.indexOf('rv:11.0') != -1) return 'ie11';
    if (ua.indexOf('gecko') != -1) return 'gecko';
    if (ua.indexOf('opera') != -1) return 'opera';
    return 'unknown';
  } catch (e) { return 'unknown' }
}

/**
 * Utility method
 */
if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str) {
    return this.slice(0, str.length) == str;
  };
}

/**
 * Append CSS
 */
function addCss(css) {
  var head = document.getElementsByTagName('head')[0];
  var link = document.createElement('link');
  link.rel = 'stylesheet';
  link.type = 'text/css';
  link.href = css;
  link.media = 'all';
  head.appendChild(link);
}

/**
 * showBackgrounGrayEffect
 */
function showBackgrounGrayEffect() {
    $('#screenGrayBackground').show();
    $('#screenGrayBackground').css({"display": "block", opacity: 0.5, "width":$(document).width(),"height":$(document).height()});
}

/**
 * showBackgrounGrayEffect
 */
function hideBackgrounGrayEffect() {
    $('#screenGrayBackground').css({"display": "none"});
}

/**
 * Datatable utilities
 */
function dataTableAddRows(obj, ost) {
  var tableRows = obj.find('tbody tr'); // grab the existing data rows
  var numberColumns = obj.children('thead').children('tr').children('th').length;
  var targetRows = ost._iDisplayLength; // grab the scroll entries
  var numberNeeded = targetRows - tableRows.length; // how many blank rows are needed to fill up to targetRows
  var lastRow = tableRows.last(); // cache the last data row
  var lastRowCells = lastRow.children('td'); // how many visible columns are there?
  var cellString;
  var highlightColumn;
  var rowClass;

  // The first row to be added actually ends up being the last row of the table.
  // Check to see if it should be odd or even.
  if (targetRows % 2) {
    rowClass= "odd";
  } else {
    rowClass = "even"; //
  }

  // We only sort on 1 column, so let's find it based on its classname
  lastRowCells.each(function(index) {
    if ($(this).hasClass('sorting_1')) {
      highlightColumn = index;
    }
  });

  /* Iterate through the number of blank rows needed, building a string that will
   * be used for the HTML of each row. Another iterator inside creates the desired
   * number of columns, adding the sorting class to the appropriate TD.
   */
  for (i = 0; i < numberNeeded; i++) {
    cellString = "";
    for (j = 0; j < numberColumns; j++) {
      if (j == highlightColumn) {
        cellString += '<td class="sorting_1">&nbsp;</td>';
      } else {
        cellString += '<td>&nbsp;</td>';
      }
    }

    // Add the TR and its contents to the DOM, then toggle the even/odd class
    // in preparation for the next.
    lastRow.after('<tr class="' + rowClass + '">' + cellString + '</tr>');
    rowClass = (rowClass == "even") ? "odd" : "even";
  }
}
