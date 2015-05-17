$(document).ready(function() {

    refreshGreetingsTable();

    $('#submit').click( function() {
        $.ajax({
            url: 'http://localhost:8080/greetings/',
            type: "POST",
            contentType: 'application/x-www-form-urlencoded',
            dataType: 'json',
            data: $('form#formoid').serialize(),
            success:function(result) {
                console.log('form submitted OK: ' + toString(result));
            },
            error:function(exception){
                console.log("Problem submitting form\n" + toString(exception));
            }
        }).then(function(greeting) {
            appendGreeting(greeting);
        });
    });

    $('body').on('click', '#deleteButton', function () {
        var greetingId = $(this)
            .closest("tr")
            .find("td:first")
            .text();
        console.log("Deleting greetingId " + greetingId + "...");
        $.ajax({
            url: 'http://localhost:8080/greetings/' + greetingId,
            type: "DELETE",
            success:function(result) {
                var greetingRowId = "greetingRow" + greetingId;
                $("#" + greetingRowId).remove();
                console.log('greeting deleted OK: ' + toString(result));
            },
            error:function(exception){
                console.log("Problem deleting row\n" + toString(exception));
            }
        }).then(function(greeting) {
            //appendGreeting(greeting);
        });
    });

    $('body').on('click', '.editButton', function () {
        var greetingId = $(this)
            .closest("tr")
            .find("td:first")
            .text();
        console.log("Edit button for " + greetingId + " clicked");
        var myid = '#greeting' + greetingId + 'Content';
        $(myid).attr("readonly", false);
        $(myid).focus();
        $('#greeting' + greetingId + 'EditBtn').prop('disabled', true);
    });

    var i = 1;
    $(".navnext").click( function() {
        var maxIndex = $('#greetings tr').length - 1;
        if (i > maxIndex) i=1;
        var greetingContent = getGreetingAtRow(i);
        $("#maintitle").text(greetingContent);
        i++;
    });

    $(".navprev").click( function() {
        var maxIndex = $('#greetings tr').length - 1;
        if (i < 1) i=maxIndex;
        var greetingContent = getGreetingAtRow(i);
        $("#maintitle").text(greetingContent);
        i--;
    });
});

function refreshGreetingsTable() {
    $.get("http://localhost:8080/greetings", function (data) {
        $.each(data, function (i, greeting) {
            appendGreeting(greeting);
        });
    });
    maxIndex = $('#greetings tr').length;
    console.log("maxIndex = " + maxIndex);
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
                '<button id="deleteButton" type="button" class="btn btn-default" aria-label="Left Align">' +
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
            success:function(result) {
                console.log('greeting updated OK: ' + toString(result));
            },
            error:function(exception){
                console.log("Problem updating row\n" + toString(exception));
            }
        }).then(function(greeting) {
            //alert("rrr");
            $('#greeting' + greeting.id + 'EditBtn').prop('disabled', false);
            var myid = '#greeting' + greeting.id + 'Content';
            $(myid).attr("readonly", true);
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
    //alert("formToJSON " + greetingID)
    return JSON.stringify({
        "id": $('#greetingID' + greetingID).text(),
        "content": $('#greeting' + greetingID + "Content").val()
    });
}

function getGreetingAtRow(row) {
    var greetingId = $('#greetings').find('tbody').find('tr:eq('+row+')').find('td:eq(0)').text();
    var greetingContent = $("#greeting" + greetingId + "Content").val();
    return greetingContent;
}