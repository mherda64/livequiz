var client = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    var url = "ws://localhost:8080/liveQuiz-websocket";
    var client = Stomp.client(url);
    client.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        client.subscribe('/topic', function (message) {
            if (message.body) {
                // showVotingSession(message.body);
                let response = JSON.parse(message.body);
                data.datasets[0].data = Object.entries(response.result).map(([k,v]) => v);
                myChart.update();
                $("#voteCount").html(response.voteCount)
            }
        });
    })
}

function disconnect() {
    if (client !== null) {
        client.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showVotingSession(message) {
    $("#votingSessionBody").append("<tr><td>" + JSON.parse(message).result.values + "</td></tr>");
    $("#votingSessionBody").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

