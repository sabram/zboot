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
        "<tr>" +
        "<td>" + greeting.id + "</td>" +
        "<td>" + greeting.content + "</td>" +
        "</tr>");
}

//converts a JavaScript value to a JSON string
function toString(exception) {
    tabSize = 4;
    return JSON.stringify(exception, null, tabSize);
}