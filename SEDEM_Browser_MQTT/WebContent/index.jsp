<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="shortcut icon" type="image/png" href="images/favicon.ico">
<title>MQTT SeDEM Browser</title>

<style type="text/css"></style>
<!-- <script -->
<!-- 	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script> -->
<!-- Bootstrap  -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css"
	integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ"
	crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.1.1.slim.min.js"
	integrity="sha384-A7FZj7v+d/sdmMqp/nOQwliLvUsJfDHW+k9Omg/a/EheAdgtzNs3hpfag6Ed950n"
	crossorigin="anonymous"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js"
	integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb"
	crossorigin="anonymous"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js"
	integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn"
	crossorigin="anonymous"></script>

<link rel="stylesheet" href="css/footer.css">
<link rel="stylesheet" href="css/main.css">
<script type="text/javascript">
	var errNum = 0;
	var conNum = 0;
	function bin2string(array) {
		var result = "";
		for (var i = 0; i < array.length; ++i) {
			result += (String.fromCharCode(array[i]));
		}
		return result;
	}
	function wsOpen(message, element) {
		var type = "enc";
		if (message.target.url.includes("decrypt")) {
			type = "dec";
		}
		// 		element.value += "Connected ... \n";
		conNum++;
		element.innerHTML += "<div id='div-conn"+type+conNum+"' class='p-1 bg-success text-center text-white'> Connected </div>";
		document.getElementById('div-conn' + type + conNum).scrollIntoView();
	}
	// function wsSendMessage(){
	// 	webSocket.send(message.value);
	// 	echoText.value += "Message sended to the server : " + message.value + "\n";
	// 	message.value = "";
	// }
	function wsCloseConnection() {
		webSocketEnc.close();
		webSocketDec.close();
		document.getElementById("closeConnectionBtn").style.display = "none";
		document.getElementById("openConnectionBtn").style.display = "inline";
	}
	function wsGetMessage(message, element) {
		console.log(message);
		var json = JSON.parse(message.data);
		console.log(json);
		var title = "Message number " + json.numMessage;
		var type = "enc";
		if (message.target.url.includes("decrypt")) {
			type = "dec";
		}
		//.padStart(35, "-").padEnd(57,"-") usato per i ---- prima e dopo del titolo
		element.innerHTML += "<div id='mexNum"+type+json.numMessage+"' class='bg-primary text-center text-white mt-0'>"
				+ title + "</div>";
		// 		element.value+=title.padStart(35, "-").padEnd(57,"-")+"\n";
		document.getElementById('mexNum' + type + json.numMessage)
				.scrollIntoView();
		for ( var property in json) {
			var textMessage = "";
			if (json.hasOwnProperty(property)) {

					var jsonProp = json[property];
					if (jsonProp.hasOwnProperty("bytes")) {
						var arrayB = jsonProp.bytes;
						var valueString = String.fromCharCode.apply(null,
								new Uint16Array(arrayB));
						// 			    		var text=property+" : "+valueString;
						textMessage += "<span class='text-primary pl-1 mb-0'>"
								+ property + " : " + " </span>" + valueString;
						// 			    		element.value += text +"\n"; 
						// 			    		textMessage+= text +"\n"; 
					} else {
						// 			    		element.value +=property+" : "+JSON.stringify(json[property]) +"\n"; 
						textMessage += "<span class='text-primary pl-1 mb-0'>"
								+ property + " : " + " </span>"
								+ JSON.stringify(json[property]);
						// 			    		textMessage+=property+" : "+JSON.stringify(json[property]) +"\n"; 
					}
				
			}
			element.innerHTML += "<p class='text-muted pl-1 mb-0'>"
					+ textMessage + " </p>";
		}

		document.getElementById('mexNum' + type + json.numMessage)
				.scrollIntoView();
		// 		element.value += message.data + "\n"

	}

	function wsClose(message, element) {
		// 		element.value += "Disconnect ... \n";
		var type = "enc";
		if (message.target.url.includes("decrypt")) {
			type = "dec";
		}
		element.innerHTML += "<div id='div-disc"+type+conNum+"' class='p-1 bg-danger text-center  text-white'> Disconnect </div>";
		document.getElementById('div-disc' + type + conNum).scrollIntoView();
	}

	function wserror(message, element) {
		// 		element.value += "Error ... \n";
		var type = "enc";
		if (message.target.url.includes("decrypt")) {
			type = "dec";
		}
		errNum++;
		element.innerHTML += "<div id='err"+type+errNum+"' class='p-1 bg-danger text-center  text-white'> Error </div>";
		document.getElementById('err' + type + errNum).scrollIntoView();
	}

	function wsOpenConnect() {
		webSocketEnc = new WebSocket("${socketServerEnc}");
		//	"ws://localhost:8080/SEDEM_Browser_MQTT/encryptedMessage");
		webSocketDec = new WebSocket("${socketServerDec}");
		//"ws://localhost:8080/SEDEM_Browser_MQTT/decryptedMessage");
		encText = document.getElementById("encryptTextBlock");
		decText = document.getElementById("decryptTextBlock");
		encText.innerHTML += '';
		decText.innerHTML += '';
		// 		encText.value = "";
		// 		decText.value = "";
		// 		message = document.getElementById("message");
		webSocketEnc.onopen = function(message) {
			wsOpen(message, encText);
		};
		webSocketEnc.onmessage = function(message) {
			wsGetMessage(message, encText);
		};
		webSocketEnc.onclose = function(message) {
			wsClose(message, encText);
		};
		webSocketEnc.onerror = function(message) {
			wsError(message, encText);
		};
		webSocketDec.onopen = function(message) {
			wsOpen(message, decText);
		};
		webSocketDec.onmessage = function(message) {
			wsGetMessage(message, decText);
		};
		webSocketDec.onclose = function(message) {
			wsClose(message, decText);
		};
		webSocketDec.onerror = function(message) {
			wsError(message, decText);
		};
		document.getElementById("openConnectionBtn").style.display = "none";
		document.getElementById("closeConnectionBtn").style.display = "inline";
	}

	var webSocketEnc = null;
	var webSocketDec = null;
	function ready() {
		wsOpenConnect();

	}
</script>
</head>
<body onload="ready()">
	<!-- 	<form> -->
	<!-- 		<input id="message" type="text"> -->
	<!-- 		<input onclick="wsSendMessage();" value="Echo" type="button"> -->
	<%@include file="header.jsp"%>
	<div class="container-fluid" id="container">
		<!-- 		<div class="row"> -->
		<!-- 			<div class="col-md-12 container-fluid text-center" id="header">SEDEM -->
		<!-- 				Browser</div> -->
		<!-- 		</div> -->
		<div class="row h-100">
			<div class="col-md-12 mb-1 ">
				<div class="row containerViewer">
					<div class="col-md-6 mb-3 text-center">
						<label for="encryptText"><b> Encrypted Message</b> </label><br />
						<div class="col-md-2"></div>
						<div
							class="messageViewer col-md-10 container-fluid text-left panel panel-default"
							id="encryptTextBlock"></div>

						<!-- 				<textarea id="encryptText" rows="15" cols="60"></textarea> -->
					</div>
					<div class="col-md-6 mb-3 text-center">
						<label for="decryptText"><b> Decrypted Message </b></label><br />
						<div
							class="messageViewer col-md-10 container-fluid text-left panel panel-default "
							id="decryptTextBlock"></div>
						<div class="col-md-2"></div>
						<!-- 				<textarea id="decryptText" rows="15" cols="60"></textarea> -->
					</div>
				</div>
				<div class="row">
					<div class="col-md-12 text-center">
						<input id="closeConnectionBtn" class="btn btn-danger"
							onclick="wsCloseConnection();" value="Disconnect" type="button">
						<input id="openConnectionBtn" class="btn btn-success"
							onclick="wsOpenConnect();" value="Connect" type="button">
					</div>


					<!-- 	</form> -->
					<!-- 	<br> -->
				</div>
			</div>
		</div>
	</div>
	<%@include file="footer.jsp"%>
	<!-- 		<div class="row " id="footer"> -->
	<!-- 			<div class="col-3"> -->
	<!-- 				<img src="images/logo-psy.png" id="logo_psy" /> -->
	<!-- 			</div> -->
	<!-- 			<div class="col-6 text-center"> -->
	<!-- 				<div class="row"> -->
	<!-- 					<div class="col-5 pr-0 text-right"> -->
	<!-- 						<img src="images/logo-cee.png" id="logo_ue" /> -->
	<!-- 					</div> -->
	<!-- 					<div class="col-7 pl-0 text-left"> -->
	<!-- 						<p>This project has received funding from FoF-05-2014: -->
	<!-- 							Innovative Product-Service design using manufacturing -->
	<!-- 							intelligence under grant agreement no 636804.</p> -->
	<!-- 					</div> -->
	<!-- 				</div> -->
	<!-- 			</div> -->
	<!-- 			<div class="col-3"> -->
	<!-- 				<img src="images/logo_fincons.jpg" id="logo_fincons" /> -->
	<!-- 			</div> -->
	<!-- 		</div> -->

</body>
</html>
