/**
 * Function creates loaded xml object
 * 
 * @param fileName
 * @return loaded xml object from given file 
 */
function loadXMLDoc(fileName) {
	//	Internet Explorer
	try {
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
	}
	catch(e) {
		//Firefox, Mozilla, Opera, etc.
		try {
			xmlDoc=document.implementation.createDocument("","",null);
	    } catch(e) {
	    	alert(e.message)
	    } 
	} try {
		xmlDoc.async=false;
		xmlDoc.load("../resources/data/"+fileName);
		return(xmlDoc);
	}
	catch(e) {
		alert(e.message)
	}
	return(null);
}


/**
 * Function gets a random word text from xml file
 * 
 * @return
 */
function doStart() {
	 
	var xmlDoc = loadXMLDoc("LVEN.xml");
	
	var nodes = xmlDoc.getElementsByTagName("word");
	//	Generate random index depends on xml nodes length
	var rand = Math.ceil(nodes.length*Math.random()-1);
	
	//	Question text
	document.getElementById("question").innerHTML =
		nodes[rand].getElementsByTagName("text")[0].childNodes[0].nodeValue;
	
	//	Translation text
	document.getElementById("translation").innerHTML =
		xmlDoc.getElementsByTagName("lang")[0].childNodes[0].nodeValue;
}

/**
 * Function checks user answer and compares it with xml
 * 
 * @return
 */
function checkAnswer() {
	var question = document.getElementById("question").innerHTML;
	var answer = document.getElementById("answer").value;
	
	var xmlDoc = loadXMLDoc("LVEN.xml");
	var nodes = xmlDoc.getElementsByTagName("word");
	
	for (i=0; i<nodes.length; i++) {
		if(nodes[i].getElementsByTagName("text")[0].childNodes[0].nodeValue == question) {
			if(nodes[i].getElementsByTagName("translation")[0].childNodes[0].nodeValue == answer) {
				return true;
			}
		}
	}
	return false;
}

/**
 * Function processes the given answer
 * 
 * @return
 */
function doAnswer() {
	if(checkAnswer()) {
		document.getElementById("err").innerHTML = "";
		document.getElementById("notif").innerHTML = "CORRECT";
	} else {
		document.getElementById("notif").innerHTML = "";
		document.getElementById("err").innerHTML = "MISTAKE";
	}
	doStart();
	//	clear all user input text
	document.getElementById("answer").value = "";
	
	//	Focus on input
	document.getElementById("answer").focus();
}

/**
 * Function add new word node to xml
 * 
 * @return
 */
function addNewWord() {
	var text = document.getElementById("text").value;
	var translation = document.getElementById("translation").value;
	
	var xmlDoc = loadXMLDoc("LVEN.xml");	
	var nodes = xmlDoc.getElementsByTagName("word");	
	for (i=0; i<nodes.length; i++) {		
		if(nodes[i].getElementsByTagName("text")[0].childNodes[0].nodeValue == text) {
			document.getElementById("notif").innerHTML = "";
			document.getElementById("err").innerHTML = "Data already exists!";
			return;
		}
	}
	if(text != "" && translation != "") {
		
//		var newWord = xmlDoc.createElement("word");
//		var newText = xmlDoc.createElement("text");
//		var newTextData = xmlDoc.createTextNode(text);
//		newWord.appendChild(newText.appendChild(newTextData));
//		
//		var newTrans=xmlDoc.createElement("translation");
//		var newTransData = xmlDoc.createTextNode(translation);
//		newWord.appendChild(newTrans.appendChild(newTransData));		
		
//		xmlDoc.appendChild(newWord);

	xmlDoc=loadXMLDoc("LVEN.xml");

	y=xmlDoc.getElementsByTagName("word")[0];
	x=xmlDoc.documentElement.removeChild(y);

	document.write("Removed node: " + x.nodeName);
		
		document.getElementById("err").innerHTML = "";
		document.getElementById("notif").innerHTML = "Data added sucessfuly";
	} else {
		document.getElementById("notif").innerHTML = "";
		document.getElementById("err").innerHTML = "Please provide data";
	}
	
}

function viewData() {
	
}
