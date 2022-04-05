var game = {};

(function () {
	"use strict";

	game.ENTRY_ID = 'entry';
	game.OUTPUT_ID = 'output';

	game.ws = null;

	game.initPage = function () {
		document.getElementById(game.ENTRY_ID).focus();

		game.writeOutput('Enter game type:');
	};

    game.connect = function() {
    	
        var url = new URL('/ws', window.location.href);
        url.protocol = url.protocol.replace('http', 'ws');

        game.ws = new WebSocket(url.href);
        game.ws.onopen = function(evt) {
            console.log('Connection established');
        };

        game.ws.onclose = function(evt) {
            game.writeOutput('Disconnected from server');
        };

        game.ws.onmessage = function(evt) {
            if (evt.data !== '') {
            	console.log("From server:", evt.data)
                game.writeOutput(evt.data);
            }
        };

        game.ws.onerror = function(evt) {
            console.error("WebSocket Error", evt)
        }
    };

	game.onEntryKeyPress = function (oCtl, oEvent) {
		if (game.isEnterKeyPress(oEvent)) {
			var sEntry = oCtl.value.trim();

			oCtl.value = '';

			if (game.type === null && game.ws === null) {
				if (sEntry.length > 0) {
					game.type = sEntry;
                    game.connect();
				}
			}
			else {
				if (sEntry !== '') {
					console.log("To server:", sEntry)
    				game.ws.send(sEntry);
                }
			}
		}
	};

	game.isEnterKeyPress = function (oEvent) {
		let keynum;

		if (window.event) { // IE8 and earlier
			keynum = oEvent.keyCode;
		} else if (oEvent.which) { // IE9/Firefox/Chrome/Opera/Safari
			keynum = oEvent.which;
		}

		return ('\n' === String.fromCharCode(keynum) || '\r' === String.fromCharCode(keynum));
	};

	game.writeOutput = function (sOutput) {
		var oOutput, sPadding;
		oOutput = document.getElementById(game.OUTPUT_ID);

		sPadding = '\n';
		if (oOutput.value.length === 0) {
			sPadding = '';
		}

		oOutput.value += sPadding + sOutput;

		oOutput.scrollTop = oOutput.scrollHeight;
	};
}());
