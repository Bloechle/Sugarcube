var isPad = (navigator.userAgent.match(/iPad/i))
		|| (navigator.userAgent.match(/iPhone/i))
		|| (navigator.userAgent.match(/iPod/i));


function addLoadEvent(fn) {
	window.addEventListener('load', fn, false);
}

function addClickEvent(id, fn) {
	var box = el3(id);
	// preventDefaultTouch(box);
	if (isPad) {
		box.addEventListener("touchend", fn, false);
	} else {
		box.addEventListener('click', fn, false);
		box.addEventListener('mouseover', (function() {
			box.style.cursor = 'pointer';
		}), false);
	}
}

function el3(id) {
	return document.getElementById(id);
}

function style3(id) {
	return document.getElementById(id).style;
}

function show3(id) {
	style3(id).visibility = "visible";
}

function hide3(id) {
	style3(id).visibility = "hidden";
}

function replaceAll(text, find, replace) {
	while (text.indexOf(find) > -1) {
		text = text.replace(find, replace);
	}
	return text;
}

function htmlize(html) {
	html = replaceAll(html, "[[", "<");
	html = replaceAll(html, "]]", ">");
	return html;
}


function class3(id, cls) {
	if (cls !== undefined) {

		var adds = cls.replace("  ", " ").trim().split(" ");
		var el = el3(id);
		if (el == null)			
			return;
			
		var res = el.className;
		if (res == null)
			res = "";
		var olds = res.replace("  ", " ").trim().split(" ");
		res = "";

		for (var i = 0; i < olds.length; i++) {
			var keep = true;
			var old = olds[i];
			for (var j = 0; j < adds.length; j++) {
				if ("-" + old === adds[j] || old == adds[j]) {
					keep = false;
					break;
				}
			}
			if (keep)
				res = (res + " " + old).trim();
		}

		for (var j = 0; j < adds.length; j++) {
			var add = adds[j];
			if (add.indexOf("-") !=0 )
				res = (res + " " + add).trim();
		}

		el3(id).className = res;
	}
}

function classDelay3(id, cls, timeout) {
	if (timeout === undefined || timeout == 0) {
		class3(id, cls);
	} else {
		setTimeout(class3, timeout, id, cls);
	}
}
	
function classAndDelay3(id, cls, clsDelay, timeout) {
	class3(id,cls);
	classDelay3(id,clsDelay,timeout);
}

