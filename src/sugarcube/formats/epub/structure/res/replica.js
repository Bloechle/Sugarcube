//var addEvent = (function () {
//  return function (el, type, fn) {
//    if (el && el.nodeName || el === window) {
//      el.addEventListener(type, fn, false);
//    } else if (el && el.length) {
//      for (var i = 0; i < el.length; i++) {
//        addEvent(el[i], type, fn);
//      }
//    }
//  };
//})();

//var prevent = function(event) {   
//  event.preventDefault();
//};

//function isEventSupported(eventName) {
//  var el = document.createElement('div');
//  eventName = 'on' + eventName;
//  var isSupported = (eventName in el);
//  if (!isSupported) {
//    el.setAttribute(eventName, 'return;');
//    isSupported = typeof el[eventName] == 'function';
//  }
//  el = null;
//  return isSupported;
//}

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

function displayBlock3(id) {
	style3(id).display = "block";
}

function displayNone3(id) {
	style3(id).display = "none";
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

var closeAudioIDHandler = function(event) {
	event.preventDefault();
	closeAudioID();
};

var closeVideoIDHandler = function(event) {
	event.preventDefault();
	closeVideoID();
};

function closeAudioID() {
	var audio = el3("audioID");
	if (audio != null) {
		audio.pause();
		audio.parentElement.style.visibility = "hidden";
		audio.parentElement.innerHTML = null;
	}
}

function closeVideoID() {
	var video = el3("videoID");
	if (video != null) {
		video.pause();
		video.parentElement.style.visibility = "hidden";
		video.parentElement.innerHTML = null;
	}
}

function addAudioID(boxID, html) {
	closeAudioID();
	var box = el3(boxID);
	box.innerHTML = null;
	box.innerHTML = htmlize(html);
	box.style.visibility = "visible";
	var audio = el3("audioID");
	if (audio != null) {
		// audio.setAttributeNS("http://apple.com/ibooks/html-extensions",
		// "pause-readaloud", "true");
		audio.load();
		audio.play();
	}
	addClickEvent("audioCloseID", closeAudioIDHandler);
}

function addVideoID(boxID, html) {
	closeVideoID();
	var box = el3(boxID);
	box.innerHTML = null;
	box.innerHTML = htmlize(html);
	box.style.visibility = "visible";
	var video = el3("videoID");
	if (video != null) {
		video.load();
		video.play();
	}
	addClickEvent("videoCloseID", closeVideoIDHandler);
}

function clickPlayAudio(clickID, boxID, html) {
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		addAudioID(boxID, html);
	});
}

function clickPlayVideo(clickID, boxID, html) {
	// preventDefaultTouch(el3(clickID));
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		addVideoID(boxID, html);
	});
}

function clickModal(clickID, modalID) {
	// preventDefaultTouch(el3(clickID));
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		Popup.showModal(modalID, null, null, {
			'screenColor' : '#99ff99',
			'screenOpacity' : .6
		});
		return false;
	});
}

function clickShow(clickID, showID) {
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		show3(showID);
	});
}

function clickHide(clickID, hideID) {
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		hide3(hideID);
	});
}

function clickPlay(clickID, mediaID) {
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		var media = el3(mediaID);
		if (media != null) {
			media.play();
		}
	});
}

function clickPause(clickID, mediaID) {
	addClickEvent(clickID, function(event) {
		event.preventDefault();
		var media = el3(mediaID);
		if (media != null) {
			media.pause();
		}
	});
}

function addClass(id, cls) {
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

function addClassTimeout(id, cls, timeout) {
	if (timeout === undefined || timeout == 0) {
		addClass(id, cls);
	} else {
		setTimeout(addClass, timeout, id, cls);
	}
}
