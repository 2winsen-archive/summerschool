/**
 * Function creates loaded xml object
 * 
 * @param fileName
 * @return loaded xml object from given file 
 */
function createXMLObject(fileName) {
	//	 Internet Explorer
	try
	{
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
	} catch (e) {
		//	 Firefox, Mozilla, Opera, etc.
		try
		{
			xmlDoc = document.implementation.createDocument("", "", null);
		} catch (e) {
			alert(e.message);
			return false;
		}
	}
	xmlDoc.async = false;
	xmlDoc.load("../resources/data/"+fileName);
	return xmlDoc;
}

/**
 * Function gets a random word text from xml file
 * 
 * @return
 */
function doStart() {
	 
	var xmlDoc = createXMLObject("LVEN.xml");
	
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
	
	var xmlDoc = createXMLObject("LVEN.xml");
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
}

function addNewWord() {

}

function viewData() {
	
}