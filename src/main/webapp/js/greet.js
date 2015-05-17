$(document).ready(function() {

    refreshGreetingsTable();

    onAddClick();

    onDeleteClick();

    onEditClick();

    onNavigationClick();
});

function refreshGreetingsTable() {
    $.get("http://localhost:8080/greetings", function (data) {
        $.each(data, function (i, greeting) {
            appendGreeting(greeting);
        });
    });
    maxIndex = $('#greetings').find('tr').length;
}

function appendGreeting(greeting) {
    var greetingID = greeting.id;
    var greetingContentId = "greeting" + greeting.id + "Content";
    $("#greetings").append(
        "<tr id=greetingRow" + greeting.id + ">" +
            "<td id=greetingID" + greeting.id + ">" + greeting.id + "</td>" +
            "<td>" +
                "<input id='" + greetingContentId + "' type='text' value='" + greeting.content + "' class='field left' readonly></td>" +
            "<td>" +
                '<button id="deleteBtn" type="button" class="btn btn-default" aria-label="Left Align">' +
                    '<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>' +
                '</button>' +
            "</td>" +
            "<td>" +
                '<button id="greeting' + greeting.id + 'EditBtn" type="button" class="btn btn-default editButton" aria-label="Left Align">' +
                    '<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>' +
                '</button>' +
            "</td>" +
        "</tr>");
    $("#" + greetingContentId).focusout(function() {
        $.ajax({
            url: 'http://localhost:8080/greetings/' + greetingID,
            type: "PUT",
            contentType: 'application/json',
            dataType: "json",
            data: formToJSON(greeting.id),
            success:function() {
                $('#greeting' + greeting.id + 'EditBtn').prop('disabled', false);
                var myid = '#greeting' + greeting.id + 'Content';
                $(myid).attr("readonly", true);
            },
            error:function(exception){
                console.log("Problem updating row\n" + toString(exception));
            }
        });
    })
}

//converts a JavaScript value to a JSON string
function toString(exception) {
    tabSize = 4;
    return JSON.stringify(exception, null, tabSize);
}

// Helper function to serialize all the form fields into a JSON string
function formToJSON(greetingID) {
    return JSON.stringify({
        "id": $('#greetingID' + greetingID).text(),
        "content": $('#greeting' + greetingID + "Content").val()
    });
}

function getGreetingAtRow(row) {
    var greetingId = $('#greetings').find('tbody').find('tr:eq('+row+')').find('td:eq(0)').text();
    return $("#greeting" + greetingId + "Content").val();
}

function onAddClick() {
    $('#addBtn').click(function () {
        $.ajax({
            url: 'http://localhost:8080/greetings/',
            type: "POST",
            contentType: 'application/x-www-form-urlencoded',
            dataType: 'json',
            data: $('form#greetingForm').serialize(),
            success: function (greeting) {
                appendGreeting(greeting);
            },
            error: function (exception) {
                console.log("Problem submitting form\n" + toString(exception));
            }
        });
    });
}

function onDeleteClick() {
    $('body').on('click', '#deleteBtn', function () {
        var greetingId = $(this)
            .closest("tr")
            .find("td:first")
            .text();
        $.ajax({
            url: 'http://localhost:8080/greetings/' + greetingId,
            type: "DELETE",
            success: function (result) {
                var greetingRowId = "greetingRow" + greetingId;
                $("#" + greetingRowId).remove();
                console.log('greeting deleted OK: ' + toString(result));
            },
            error: function (exception) {
                console.log("Problem deleting row\n" + toString(exception));
            }
        });
    });
}

function onEditClick() {
    $('body').on('click', '.editButton', function () {
        var greetingId = $(this)
            .closest("tr")
            .find("td:first")
            .text();
        var greetingContentID = '#greeting' + greetingId + 'Content';
        $(greetingContentID).attr("readonly", false);
        $(greetingContentID).focus();
        $('#greeting' + greetingId + 'EditBtn').prop('disabled', true);
    });
}

function onNavigationClick() {
    var i = 1;
    $(".navnext").click(function () {
        var maxIndex = $('#greetings').find('tr').length - 1;
        if (i > maxIndex) i = 1;
        var greetingContent = getGreetingAtRow(i);
        $("#maintitle").text(greetingContent);
        i++;
    });

    $(".navprev").click(function () {
        var maxIndex = $('#greetings').find('tr').length - 1;
        if (i < 1) i = maxIndex;
        var greetingContent = getGreetingAtRow(i);
        $("#maintitle").text(greetingContent);
        i--;
    });
}