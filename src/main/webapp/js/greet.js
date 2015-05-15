$(document).ready(function() {

    refreshGreetingsTable();

    $('#submit').click( function() {
        //alert("sent1");
        //TODO This initial submit call will probably become redundant when I get the POST working
        //$.ajax({
        //    url: 'http://localhost:8080/greeting',
        //    dataType: 'json',
        //    data: $('form#formoid').serialize()
        //}).then(function(data) {
        //    $('.greeting-id').text(data.id);
        //    $('.greeting-content').text(data.content);
        //});

        $.ajax({

            url: 'http://localhost:8080/greetings/',
            type: "POST",
            contentType: 'application/x-www-form-urlencoded',
            dataType: 'json',
            data: $('form#formoid').serialize(),
            success:function(result) {//we got the response
                //alert('Successfully called');
            },
            error:function(exception){
                alert(JSON.stringify(exception, null, 4));
            }
        }).then(function(greeting) {
            //alert(JSON.stringify(data, null, 4));
            ////TODO update to refresh table instead?
            //$('.greeting-id').text(data.id);
            //$('.greeting-content').text(data.content);
            appendGreeting(greeting);
        });
        //alert("sent2");
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