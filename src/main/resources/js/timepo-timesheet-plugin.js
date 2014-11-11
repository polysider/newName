var addWorklogPopupWidth = 130;
var showWorklogPopupWidth = 130;
var worklogMap = {};    //Map: worklogId->
var dayList = {};

function isTimeSpentValid(timeSpent) {
    return /^([0-9]+[hm]|[0-9]+[h]\s+[0-5]?[0-9][m])$/.test(timeSpent);
}

function isCommentValid(comment) {
    return comment != '';
}

function timeSpentOrCommentChanged() {
    var addWorklogPopup = findAddWorklogPopup();
    var timeSpent = addWorklogPopup.find("#timeSpent").val();
    var comment = addWorklogPopup.find("#comment").val();

    if (isTimeSpentValid(timeSpent) && isCommentValid(comment)) {
        addWorklogPopup.find("#ok").removeAttr("disabled");
    }
    else {
        addWorklogPopup.find("#ok").attr("disabled", "disabled");
    }
}

function updateCellView(cellId) {
    var sum = 0;
    cellWorklogMap[cellId].forEach(function (id) {
        sum += worklogMap[id][0];
    });
    if (sum == 0) {
        sum = '';
    }
    var cell = AJS.$(cellId);
    cell.text(convertSecondsToPrettyPrintTime(sum));

    // Update 'Total' rows content for dayIndex column
    var userName = extractUserName(cellId);
    var dayIndex = extractDayIndex(cellId);
    var cellIdTemplate = "[id^=" + userName + "-][id$=-number]";                //jQuery multiple selector
    var upTotalCellIdTemplate = "[id^=" + userName + "-][id$=-number-up]";
    var downTotalCellIdTemplate = "[id^=" + userName + "-][id$=-number-down]";

    var cells = AJS.$(cellIdTemplate.replace("number", dayIndex));
    var sum = 0;
    for (var i = 0; i < cells.length; i++) {
        sum += convertTimeTxtToSec(cells[i].textContent.trim());
    }

    var prettyPrintSum = convertSecondsToPrettyPrintTime(sum);
    AJS.$(upTotalCellIdTemplate.replace("number", dayIndex)).text(prettyPrintSum);
    AJS.$(downTotalCellIdTemplate.replace("number", dayIndex)).text(prettyPrintSum);

    // Update 'Total' column content
    var issueId = extractIssueId(cellId);
    var sum = 0;
    for (var dayIndex = 0; dayIndex < dayList.length; dayIndex++) {
        sum += convertTimeTxtToSec(AJS.$("#" + userName + "-" + issueId + "-" + dayIndex).text());
    }
    var prettyPrintSum = convertSecondsToPrettyPrintTime(sum);
    AJS.$("#" + userName + "-" + issueId + "-right").text(prettyPrintSum);

    // Update 'Total total' cell
    var rightTotalCellIdTemplate = "[id^=" + userName + "-][id$=-right]";
    var cells = AJS.$(rightTotalCellIdTemplate);
    var sum = 0;
    for (var i = 0; i < cells.length; i++) {
        sum += convertTimeTxtToSec(cells[i].textContent.trim());
    }
    var prettyPrintSum = convertSecondsToPrettyPrintTime(sum);
    AJS.$("#" + userName + "-total-up").text(prettyPrintSum);
    AJS.$("#" + userName + "-total-down").text(prettyPrintSum);
}

function convertSecondsToPrettyPrintTime(seconds) {
    var result = '';
    var hours = Math.floor(seconds / 3600);
    if (hours > 0) {
        seconds -= hours * 3600;
        result += hours + 'h';
    }
    var minutes = Math.floor(seconds / 60);
    if (minutes > 0) {
        seconds -= minutes * 60;
        result += minutes + 'm';
    }
    return result;
}

function updateView() {
    Object.keys(cellWorklogMap).forEach(function (cellId) {
        updateCellView(cellId);
    });
}

function initPopups(worklogMapFromWorklogContext, dayListFromWorklogContext) {
    // Each entry contains: worklogId, userName, issueId, dayIndex, timeSpent, comment

    cellWorklogMap = {};
    worklogMapFromWorklogContext.forEach(function (entry) {
        var worklogId = entry[0];
        var userName = entry[1];
        var issueId = entry[2];
        var dayIndex = entry[3];
        var timeSpent = entry[4];
        var comment = entry[5];

        worklogMap[worklogId] = [timeSpent, comment];

        var cellWorklogMapKey = '#' + userName + '-' + issueId + '-' + dayIndex;
        if (cellWorklogMap[cellWorklogMapKey] == null) {
            cellWorklogMap[cellWorklogMapKey] = [];
        }
        cellWorklogMap[cellWorklogMapKey].push(worklogId);

        // For issues gadget
        var cellWorklogMapKey = '#' + userName + '-' + issueId;
        if (cellWorklogMap[cellWorklogMapKey] == null) {
            cellWorklogMap[cellWorklogMapKey] = [];
        }
        cellWorklogMap[cellWorklogMapKey].push(worklogId);
    });

    dayList = dayListFromWorklogContext;

    updateView();

    // Restore style for fixed cell after gadget refresh
    var cell = AJS.$(fixedCellId);
    if (cell) {
        cell.removeClass("main_worklog");
        cell.addClass("main_edit");
        if (isThisCellWithoutWorklog(fixedCellId)) cell.text("+");
    }
}
//---------------------------------------------------------------------------------------------
// Only one popup can be shown at the same time
//---------------------------------------------------------------------------------------------
var POPUP = {
    NO: 0,
    ADD_WORKLOG: 1,
    SHOW_WORKLOG: 2
};
var fixedPopup = POPUP.NO;
var fixedCellId = 0;
var cellWorklogMap = {};

function isPopupFixed() {
    return fixedPopup != POPUP.NO;
}

function isPopupNotFixed() {
    return fixedPopup == POPUP.NO;
}
//---------------------------------------------------------------------------------------------
function cellClick(cellId) {
    if (isPopupNotFixed()) {
        showAndFixPopup(cellId);
    } else if (fixedCellId == cellId) {
        hideAndUnfixPopup(cellId);
    }
}

function cellMouseOver(cellId) {
    if (isPopupFixed()) return;

    if (isThisCellWithoutWorklog(cellId)) {
        AJS.$(cellId).text("+");
    } else {
        showShowWorklogPopup(cellId);
    }
}

function cellMouseOut(cellId) {
    if (isPopupFixed()) return;

    if (isThisCellWithoutWorklog(cellId)) {
        AJS.$(cellId).text("");
    } else {
        hideShowWorklogPopup(cellId);
    }
}
//---------------------------------------------------------------------------------------------
function isThisCellWithoutWorklog(cellId) {
    return cellWorklogMap[cellId] == undefined || cellWorklogMap[cellId].length == 0;
}
//---------------------------------------------------------------------------------------------
function extractUserName(cellId) {
    return cellId.split("-")[0].substring(1);   //remove '#' character
}
function extractIssueId(cellId) {
    return cellId.split("-")[1];
}
function extractDayIndex(cellId) {
    return cellId.split("-")[2];
}
function extractStartDate(cellId) {
    var dayIndex = extractDayIndex(cellId);
    return dateByDayIndex(dayIndex);
}
function dateByDayIndex(index) {
    return dayList[index];
}
//---------------------------------------------------------------------------------------------
function showAddWorklogPopup(cellId) {
    var issueId = extractIssueId(cellId);
    var startDate = extractStartDate(cellId);

    var addWorklogPopup = findAddWorklogPopup();
    addWorklogPopup.find("#issueId").val(issueId);
    addWorklogPopup.find("#startDate").val(startDate);
    addWorklogPopup.find("#cellId").val(cellId);
    addWorklogPopup.find("#timeSpent").val("8h");
    addWorklogPopup.find("#comment").val("");
    addWorklogPopup.find("#ok").attr("disabled", "disabled");

    moveAndOpenAddWorklogPopup(cellId);
}
function hideAddWorklogPopup() {
    findAddWorklogPopup().dialog("close");
}
//---------------------------------------------------------------------------------------------
function generateContentForShowWorklogPopup(cellId, showControls) {
    var worklogIds = cellWorklogMap[cellId]

    var htmlContent = '';
    if (worklogIds) {
        for (var i = 0; i < worklogIds.length; i++) {
            var id = worklogIds[i];
            if (worklogMap[id] == null) {
                continue;
            }

            var comment = worklogMap[id][1];
            var timeSpent = convertSecondsToPrettyPrintTime(worklogMap[id][0]);
            htmlContent += "" +
                "<tr>" +
                "<td class='worklogPopup' width='50'>" +
                (showControls ? generateInputElemt(cellId.substring(1) + '-' + id + '-comment', comment, cellId) : comment) +
                "</td>" +
                "<td class='worklogPopup'>" +
                (showControls ? generateInputElemt(cellId.substring(1) + '-' + id + '-timeSpent', timeSpent, cellId) : timeSpent) +
                "</td>";
            if (showControls) {
                htmlContent += "<td class='worklogPopup'>" +
                    "<input class='delete' type='button' onclick='deleteWorklog(\"" + cellId + "\",\"" + id + "\")' value='x'/>" +
                    "</td>";
            }
            htmlContent += "</tr>";
        }
    }
    if (htmlContent == '') return '';

    if (showControls) {
        htmlContent += "<tr class='controls'>" +
            "<td class='worklogPopup' colspan='3'>" +
            "<input id='new' type='button' onclick='newWorklog(\"" + cellId + "\")' value='new'/>" +
            "<input id='save' type='button' onclick='updateWorklogs(\"" + cellId + "\")' value='save'/>" +
            "</td>" +
            "</tr>";
    }
    return "<table>" + htmlContent + "</table>";
}

function generateInputElemt(id, value, cellId) {
    return "<input id='" + id + "' size='4' type='text' value='" + value + "' onkeyup='someFieldInWorklogListWasChanged(\"" + cellId + "\")'/>";
}

function showShowWorklogPopup(cellId) {
    var htmlContent = generateContentForShowWorklogPopup(cellId, false);
    if (htmlContent != '') {
        findShowWorklogPopup().html(htmlContent);
        moveAndOpenShowWorklogPopup(cellId);
    }
}
function showShowWorklogPopupWithControls(cellId) {
    var htmlContent = generateContentForShowWorklogPopup(cellId, true);
    if (htmlContent != '') {
        findShowWorklogPopup().html(htmlContent);
        moveAndOpenShowWorklogPopup(cellId);
    }
}
function hideShowWorklogPopup() {
    findShowWorklogPopup().dialog("close");
}
//---------------------------------------------------------------------------------------------
function showAndFixPopup(cellId) {
    var cellWithoutWorklogFlag = isThisCellWithoutWorklog(cellId);
    if (cellWithoutWorklogFlag) {
        showAddWorklogPopup(cellId);
    } else {
        showShowWorklogPopupWithControls(cellId);
        someFieldInWorklogListWasChanged(cellId);
    }
    fixedPopup = cellWithoutWorklogFlag ? POPUP.ADD_WORKLOG : POPUP.SHOW_WORKLOG;
    fixedCellId = cellId;

    var cell = AJS.$(cellId);
    cell.removeClass("main_worklog");
    cell.addClass("main_edit");
}

function hideAndUnfixPopup(cellId) {
    if (fixedPopup == POPUP.ADD_WORKLOG) {
        hideAddWorklogPopup();
    } else if (fixedPopup == POPUP.SHOW_WORKLOG) {
        hideShowWorklogPopup();
    }
    fixedPopup = POPUP.NO;
    fixedCellId = 0;

    var cell = AJS.$(cellId);
    cell.removeClass("main_edit");
    cell.addClass("main_worklog");
}

function findAddWorklogPopup() {
    return AJS.$(".addWorklogPopup");
}

function findShowWorklogPopup() {
    return AJS.$(".showWorklogPopup");
}
//---------------------------------------------------------------------------------------------
function moveAndOpenAddWorklogPopup(cellId) {
    moveAndOpenPopup(".addWorklogPopup", cellId, addWorklogPopupWidth);
}

function moveAndOpenShowWorklogPopup(cellId) {
    moveAndOpenPopup(".showWorklogPopup", cellId, showWorklogPopupWidth);
}

function moveAndOpenPopup(popupId, cellId, popupWidth) {
    var cell = AJS.$(cellId);
    var rightPadding = parseInt(cell.css("padding-right"), 10);
    var leftPadding = parseInt(cell.css("padding-left"), 10);

    var cellWidth = cell.width() + rightPadding + leftPadding;
    var left = cell.position().left + cellWidth;

    if (left + popupWidth > AJS.$(window).width()) {
        left = cell.position().left - popupWidth - leftPadding;
    }
    var top = cell.position().top - 1;  //adjust vertical position

    var popup = AJS.$(popupId);
    popup.dialog('option', 'position', [left, top]);
    popup.dialog("open");

    adjustGadgetsHeight();
}
//---------------------------------------------------------------------------------------------
// Processing of clicks on popups buttons
//---------------------------------------------------------------------------------------------
function addWorklogOk() {
    var addWorklogPopup = findAddWorklogPopup();
    var issueId = addWorklogPopup.find("#issueId").val();
    var startDate = addWorklogPopup.find("#startDate").val();
    var timeSpent = addWorklogPopup.find("#timeSpent").val();
    var comment = addWorklogPopup.find("#comment").val();
    var cellId = addWorklogPopup.find("#cellId").val();
    var userName = extractUserName(cellId);

    if (isTimeSpentValid(timeSpent) && isCommentValid(comment)) {
        AJS.$.ajax({
            url: "/rest/timepo-resource/1.0/addWorklog.json",
            data: {
                issueId: issueId,
                userName: userName,
                startDate: startDate,
                timeSpent: timeSpent.replace(" ", "%20"),
                comment: comment.replace(" ", "%20")
            },
            type: "GET",
            dataType: "json",
            success: function (data) {
                var worklogId = data.html;

                worklogMap[worklogId] = [convertTimeTxtToSec(timeSpent), comment];
                if (cellWorklogMap[cellId] == null) {
                    cellWorklogMap[cellId] = [];
                }
                cellWorklogMap[cellId].push(worklogId);
                updateCellView(cellId);
            }
        });
    } else {
        alert('Wrong timeSpent value!');
    }

    hideAndUnfixPopup(cellId);
}

function deleteWorklog(cellId, id) {
    AJS.$.ajax({
        url: "/rest/timepo-resource/1.0/deleteWorklog.json",
        data: {
            worklogId: id
        },
        type: "GET",
        dataType: "json",
        success: function () {
            worklogMap[id] = null;

            var elementIndex = cellWorklogMap[cellId].indexOf(id.toString());
            if (elementIndex > -1) {
                cellWorklogMap[cellId].splice(elementIndex, 1);
            }

            var cell = AJS.$(cellId);
            cell.trigger("click");      //unfix popup
            if (cellWorklogMap[cellId].length != 0) {
                cell.trigger("click");
            }
            updateCellView(cellId);
        }
    });
}

function newWorklog(cellId) {
    AJS.$(cellId).trigger("click");
    showAddWorklogPopup(cellId);
    fixedPopup = POPUP.ADD_WORKLOG;
    fixedCellId = cellId;
}

function updateWorklogs(cellId) {
    if (isFieldsInWorklogListValid(cellId)) {
        // Popup containts rows with next content:
        // Comment field with id=cellId-worklogId-comment
        // Time spent field with id=cellId-worklogId-timeSpent

        var showWorklogPopup = findShowWorklogPopup();
        var worklogIds = cellWorklogMap[cellId];
        for (var i = 0; i < worklogIds.length; i++) {
            var worklogId = worklogIds[i];
            var comment = showWorklogPopup.find(cellId + '-' + worklogId + '-comment').val();
            var timeSpent = showWorklogPopup.find(cellId + '-' + worklogId + '-timeSpent').val();

            //perform ajax call for worklog update
            AJS.$.ajax({
                url: "/rest/timepo-resource/1.0/updateWorklog.json",
                data: {
                    worklogId: worklogId,
                    timeSpent: timeSpent.replace(" ", "%20"),
                    comment: comment.replace(" ", "%20")
                },
                type: "GET",
                async: false,       //it's important for UI update!
                dataType: "json",
                success: function () {
                    worklogMap[worklogId] = [convertTimeTxtToSec(timeSpent), comment];
                    updateCellView(cellId);
                },
                error: function () {
                    alert('Error occurs during worklog saving!');
                }
            });
        }

        AJS.$(cellId).trigger("click");
    }
}

function isFieldsInWorklogListValid(cellId) {
    // Comment id = userName-issueId-dayIndex-worklogId-comment
    // Time spent id = userName-issueId-dayIndex-worklogId-timeSpent

    var showWorklogPopup = findShowWorklogPopup();
    var worklogIds = cellWorklogMap[cellId];
    for (var i = 0; i < worklogIds.length; i++) {
        var worklogId = worklogIds[i];
        var comment = showWorklogPopup.find(cellId + '-' + worklogId + '-comment').val();
        var timeSpent = showWorklogPopup.find(cellId + '-' + worklogId + '-timeSpent').val();

        if (!isTimeSpentValid(timeSpent) || !isCommentValid(comment)) return false;
    }

    return true;
}

function someFieldInWorklogListWasChanged(cellId) {
    var saveButton = findShowWorklogPopup().find("#save");
    if (isFieldsInWorklogListValid(cellId)) {
        saveButton.removeAttr("disabled");
    }
    else {
        saveButton.attr("disabled", "disabled");
    }
}
//---------------------------------------------------------------------------------------------
function convertTimeTxtToSec(timeSpent) {
    var hours = 0;
    var minutes = 0;

    var hIndex = timeSpent.search('h');
    if (hIndex != -1) {
        hours = timeSpent.substr(0, hIndex);
    }
    timeSpent = timeSpent.substr(hIndex + 1).trim();
    var mIndex = timeSpent.search('m');
    if (mIndex != -1) {
        minutes = timeSpent.substr(0, mIndex);
    }
    return 3600 * hours + 60 * minutes;
}
//---------------------------------------------------------------------------------------------
