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

    $('body').on('click', '.btn', function () {
        var greetingId = $(this)
            .closest("tr")
            .find("td:first")
            .text();
        console.log("Deleting greetingId " + greetingId + "...");
        $.ajax({
            url: 'http://localhost:8080/greetings/' + greetingId,
            type: "DELETE",
            success:function(result) {
                var rowId = "rowId" + greetingId;
                $("#" + rowId).remove();
                console.log('greeting deleted OK: ' + toString(result));
            },
            error:function(exception){
                console.log("Problem deleting row\n" + toString(exception));
            }
        }).then(function(greeting) {
            //appendGreeting(greeting);
        });
    });

});

function refreshGreetingsTable() {
    $.get("http://localhost:8080/greetings", function (data) {
        $.each(data, function (i, greeting) {
            appendGreeting(greeting);
        });

    });
}

function appendGreeting(greeting) {
    $("#greetings").append(
        "<tr id=rowId" + greeting.id + ">" +
            "<td>" + greeting.id + "</td>" +
            "<td>" + greeting.content + "</td>" +
            "<td>" +
                '<button id="deleteButton" type="button" class="btn btn-default" aria-label="Left Align">' +
                    '<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>' +
                '</button>' +
            "</td>" +
        "</tr>");
}

//converts a JavaScript value to a JSON string
function toString(exception) {
    tabSize = 4;
    return JSON.stringify(exception, null, tabSize);
}
