//富文本类型
const TYPE_TEXT = 0;		//纯文本
const TYPE_IMAGE = 1;		//图片
const TYPE_IMAGE_TEXT = 2;	//加文字

//更多按钮：删除按钮
const MORE_BTN_TYPE_DELETE = -1;
const MORE_BTN_TEXT_DELETE = '删除';

var mImageMoreBtnList;//图片长按更多按钮数据列表

var contentJson;//存储内容数据

var mIsEdit = false;//是否为编辑状态

var mFontSize = 0;//字体大小

//发言人相关
var mIsShowSpeaker = false;
var mLastSpeakerId;
var mSpeakerColorIndex = 0;
var mSpeakerColorMap = new Map();
var mSpeakerColorArray = ['#057DFF','#F58F00','#9B96FF','#45CC9D'];

//设置文本
function setContent(json) {
	console.log('setContent() json = ' + json);
	var obj = JSON.parse(json);
	if (null == obj || null == obj.datalist) {
		console.log('setContent() json Exception!');
		return;
	}

	contentJson = obj;
	refreshContent(obj.datalist);
}

//刷新内容
function refreshContent(datalist) {
	console.log('refreshContent() datalist = ' + datalist);
	if (null == datalist || 0 == datalist.length) {
		clearContent();
		return;
	}

	var type;//插入类型：0（纯文本），1（图片），2（图片加文字）
	for (var i = 0; i < datalist.length; i++) {
		type = datalist[i].type;
		if (TYPE_IMAGE == type) {
			addImage(datalist[i].imageUrl, datalist[i].index, datalist[i].startTime, false);
		} else if (TYPE_IMAGE_TEXT == type) {
			addImageWithText(datalist[i].imageUrl, datalist[i].index, datalist[i].startTime, datalist[i].content, false);
		} else {
			addText(datalist[i].content, datalist[i].index, datalist[i].startTime, datalist[i].endTime, 
				datalist[i].speakerInfo.id, datalist[i].speakerInfo.name, false);
		}
		
	}
}

//清除内容
function clearContent() {
	console.log('clearContent()');
	var contentNode = document.getElementById('content');
	var childs = contentNode.childNodes;
	if (null == childs) {
		return;
	}

	while (childs.length > 0) {	
  		contentNode.removeChild(childs[0]);
	}
}

//更新进度
function updateProgress(currentTime) {
	console.log('updateProgress() currentTime = ' + currentTime);
	if (mIsEdit) {
		console.log('updateProgress() isEdit = true, do nothing');
		return;
	}

	currentTime = parseFloat(currentTime);
	var isNeedChange = true;
	var lightList = document.getElementsByClassName('light-text');
	for (var i = 0; i < lightList.length; i++) {
		var startTime = parseFloat(lightList[i].dataset['startTime']);
		var endTime = parseFloat(lightList[i].dataset['endTime']);
		if (currentTime >= startTime && currentTime < endTime) {
			isNeedChange = false;
		} else {
			lightList[i].className = 'common-text';
		}
	}

	console.log('updateProgress() isNeedChange = ' + isNeedChange);
	if (!isNeedChange) {
		return;
	}

	var commonList = document.getElementsByClassName('common-text');
	for (var i = 0; i < commonList.length; i++) {
		var startTime = parseFloat(commonList[i].dataset['startTime']);
		var endTime = parseFloat(commonList[i].dataset['endTime']);
		if (currentTime >= startTime && currentTime < endTime) {
			commonList[i].className = 'light-text';
			//高亮位置不可见，则需要滚动
			if (!checkVisible(commonList[i])) {
				var topPos = commonList[i].offsetTop;
				console.log('updateProgress() isVisible = false, scrollTop = ' + topPos);
    			window.scrollTo(0, topPos);
			}
			break;
		}
	}
}

//编辑文档
function editContent() {
	console.log('editContent()');
	if (mIsEdit) {
		console.log('editContent() isEdit = true, do nothing');
		return;
	}

	mIsEdit = true;

	changeContentState(true);
}

//保存编辑
function saveEdit() {
	console.log('saveEdit()');
	if (!mIsEdit) {
		console.log('saveEdit() isEdit = false, do nothing');
		return;
	}

	mIsEdit = false;

	//更新数据
	contentJson = getCurrentData();
	console.log("saveEdit() contentJson = " + JSON.stringify(contentJson));

	//重新加载内容
	clearContent();
	refreshContent(contentJson.datalist);

	//通知安卓端
	window.jsInterface.updateContent(JSON.stringify(contentJson));
}

//取消修改
function cancelEdit() {
	console.log('cancelEdit()');
	if (!mIsEdit) {
		console.log('cancelEdit() isEdit = false, do nothing');
		return;
	}

	mIsEdit = false;

	//重新加载内容
	clearContent();
	refreshContent(contentJson.datalist);
}

//添加文本
function addText(content, index, startTime, endTime, speakerId, speakerName, isScroll) {
	console.log('addText() index = ' + index + ", startTime = " + startTime 
		+ ", endTime = " + endTime + ", isScroll = " + isScroll 
		+ ", speakerId = " + speakerId + ", speakerName = " + speakerName + ", content = " + content);
	
	var span = createTextElement(content, index, startTime, endTime, speakerId, speakerName, mFontSize, mIsEdit); 
	document.getElementById('content').appendChild(span);
	if (null != speakerId && null != speakerName && mLastSpeakerId != speakerId) {
		var speakerSpan = createSpeakerElement(speakerId, speakerName, index, mFontSize, mIsEdit, mIsShowSpeaker);
		document.getElementById('content').insertBefore(speakerSpan, span);
		insertOrRemoveSpeakerLineFeed(mIsShowSpeaker, speakerSpan);
		mLastSpeakerId = speakerId;
	}

	//滚动以确保元素显示
	if (isScroll) {
		showElement(span);
	}
}

//插入文本（根据startTime插入指定位置）
function insertText(content, index, startTime, endTime, isScroll) {
	console.log('insertText() index = ' + index + ", startTime = " + startTime 
		+ ", endTime = " + endTime + ", isScroll = " + isScroll + ", content = " + content);

	var nextElement = getInsertPositionElement(startTime);
	var span = createTextElement(content, index, startTime, endTime, null, null, mFontSize, mIsEdit);
	document.getElementById('content').insertBefore(span, nextElement);

	if (isScroll) {
		showElement(span);
	}
}

//添加图片
function addImage(url, index, startTime, isScroll) {
	console.log('addImage() url = ' + url + ", index = " + index + ", startTime = " + startTime + ", isScroll = " + isScroll);
	var img = createImageElement(url, index, startTime, mIsEdit); 

	document.getElementById('content').appendChild(document.createElement('br'));
	document.getElementById('content').appendChild(img);
	document.getElementById('content').appendChild(document.createElement('br'));

	//滚动以确保元素显示
	if (isScroll) {
		showElement(img);
	}
}

//插入图片（根据startTime插入指定位置）
function insertImage(url, index, startTime, isScroll) {
	console.log('insertImage() url = ' + url + ", index = " + index + ", startTime = " + startTime + ", isScroll = " + isScroll);
	
	var nextElement = getInsertPositionElement(startTime);
	var img = createImageElement(url, index, startTime, mIsEdit); 

	document.getElementById('content').insertBefore(document.createElement('br'), nextElement);
	document.getElementById('content').insertBefore(img, nextElement);
	document.getElementById('content').insertBefore(document.createElement('br'), nextElement);

	//滚动以确保元素显示
	if (isScroll) {
		showElement(img);
	}
}

//添加图片和文字
function addImageWithText(url, index, startTime, content, isScroll) {
	console.log('addImageWithText() url = ' + url + ", index = " + index 
		+ ", startTime = " + startTime + ", isScroll = " + isScroll + ", content = " + content);
	
	var p = createImageWithText(url, index, startTime, content, mIsEdit);
	document.getElementById('content').appendChild(p);

	//滚动以确保元素显示
	if (isScroll) {
		showElement(p);
	}
}

//插入图片和文字（根据startTime插入指定位置）
function insertImageWithText(url, index, startTime, content, isScroll) {
	console.log('insertImageWithText() url = ' + url + ", index = " + index 
		+ ", startTime = " + startTime + ", isScroll = " + isScroll + ", content = " + content);
	
	var nextElement = getInsertPositionElement(startTime);
	var p = createImageWithText(url, index, startTime, content, mIsEdit);
	document.getElementById('content').insertBefore(p, nextElement);

	if (isScroll) {
		showElement(p);
	}
}

//插入临时的高亮文本
function insertTempLight(content) {
	console.log('insertTempLight() content = ' + content);
	removeTempLight();

	var span = document.createElement('span');
	span.innerHTML = content;
	span.className = 'insert-temp-light';
	if (mFontSize > 0) span.style.fontSize = mFontSize + 'px';

	document.getElementById('content').appendChild(span);

	//滚动以确保元素显示
	showElement(span);
}

//删除高亮文本
function removeTempLight() {
	console.log('removeTempLight()');
	var tempElements = document.getElementsByClassName("insert-temp-light");
	while (tempElements.length > 0) {
		tempElements[0].parentNode.removeChild(tempElements[0]);
	}
}

//保存当前文件内容
function saveContent() {
	console.log('saveContent()');
	contentJson = getCurrentData();
	//通知安卓端
	window.jsInterface.updateContent(JSON.stringify(contentJson));
}

//设置更多按钮
function setMoreBtn(imageMoreBtnList) {
	console.log('setMoreBtn() imageMoreBtnList = ' + imageMoreBtnList);
	var imageBtnList = JSON.parse(imageMoreBtnList);
	if (null == imageBtnList) {
		console.log('setMoreBtn() json Exception!');
		return;
	}

	if (null != mImageMoreBtnList) {
		mImageMoreBtnList.length = 0;//清空数组
	}
	
	mImageMoreBtnList = imageBtnList;
	//默认插入删除按钮
	mImageMoreBtnList.push({'type': MORE_BTN_TYPE_DELETE,"text": MORE_BTN_TEXT_DELETE});
}

//文本编辑态和非编辑态切换
function changeContentState(isEdit) {
	if (isEdit) {
		var lightList = document.getElementsByClassName('light-text');
		while (lightList.length > 0) {
			lightList[0].setAttribute("contenteditable", "true");
			lightList[0].className = 'edit-text';//直接修改className会导致lightList动态变化，需要改用while循环
		}

		var commonList = document.getElementsByClassName('common-text');
		while (commonList.length > 0) {
			commonList[0].setAttribute("contenteditable", "true");
			commonList[0].className = 'edit-text';//属性修改放在最后，否则会导致commonList[0]为空
		}

	} else {
		var editList = document.getElementsByClassName('edit-text');
		while (editList.length > 0) {
			editList[0].setAttribute("contenteditable", "false");
			editList[0].className = 'common-text';
		}
	}

	//图片设置为可编辑
	var imageList = document.getElementsByClassName('insert-image');
	for (var i = 0; i < imageList.length; i++) {
		imageList[i].setAttribute("contenteditable", isEdit);
	}

	var imageTextList = document.getElementsByClassName('insert-imgtxt-txt');
	for (var i = 0; i < imageTextList.length; i++) {
		imageTextList[i].setAttribute("contenteditable", isEdit);
	}
}

//设置显示或隐藏发言人
function setShowSpeaker(isShow) {
	console.log('setShowSpeaker() isShow = ' + isShow);
	if (isShow == mIsShowSpeaker) {
		return;
	}

	mIsShowSpeaker = isShow;
	var speakerList = document.getElementsByClassName('speaker-text');
	for (var i = 0; i < speakerList.length; i++) {
		speakerList[i].style.display = isShow ? "inline" : "none";	
		insertOrRemoveSpeakerLineFeed(isShow, speakerList[i]);
	}
}


//发言人重命名
function ranameSpeaker(id, newname) {
	console.log('ranameSpeaker() id = ' + id + ', newname = ' + newname);
	var speakerList = document.getElementsByClassName('speaker-text');
	for (var i = 0; i < speakerList.length; i++) {
		if (id == speakerList[i].dataset['speakerId']) {
			speakerList[i].dataset['speakerName'] = newname;
			speakerList[i].innerHTML = newname  + ": ";
		}
	}

	var commonList = document.getElementsByClassName('common-text');
	for (var i = 0; i < commonList.length; i++) {
		if (id == commonList[i].dataset['speakerId']) {
			commonList[i].dataset['speakerName'] = newname;
		}
	}
}

//设置发言人颜色
function setSpeakerColor(colorArray) {
	console.log('setSpeakerColor() colorArray = ' + colorArray);
	if (null == colorArray || 0 == colorArray.length) {
		return;
	}

	mSpeakerColorArray = colorArray;
}

//设置字体大小
function setFontSize(size) {
	console.log('setFontSize() size = ' + size);
	if (size <= 0) {
		return;
	}

	mFontSize = size;
	var commonList = document.getElementsByClassName('common-text');
	for (var i = 0; i < commonList.length; i++) {
		commonList[i].style.fontSize = size + 'px';
	}

	var lightList = document.getElementsByClassName('light-text');
	for (var i = 0; i < lightList.length; i++) {
		lightList[i].style.fontSize = size + 'px';
	}

	var editList = document.getElementsByClassName('edit-text');
	for (var i = 0; i < editList.length; i++) {
		editList[i].style.fontSize = size + 'px';
	}

	var speakerList = document.getElementsByClassName('speaker-text');
	for (var i = 0; i < speakerList.length; i++) {
		speakerList[i].style.fontSize = size + 'px';
	}

}

//插入或移除发音人换行符
function insertOrRemoveSpeakerLineFeed(isInsert, speakerNode) {
	var preNode = speakerNode.previousSibling;  //得到上一个兄弟节点
	if (isInsert && null != preNode) {
		var span = createLineFeedElement("speaker-line-feed", 20);
		document.getElementById('content').insertBefore(span, speakerNode);
	} else {
		if (null != preNode && preNode.id == "speaker-line-feed") {
			document.getElementById('content').removeChild(preNode);
		}
	}
}

function textClick(event) {
	console.log('textClick()');
	event.stopPropagation();//span需要禁止冒泡事件
	var span = event.target;
	//通知安卓端
	window.jsInterface.clickText(JSON.stringify(getTextData(span)));
}

function speakerClick(event) {
	console.log('speakerClick()');
	event.stopPropagation();//span需要禁止冒泡事件
	var span = event.target;
	//通知安卓端
	window.jsInterface.clickSpeaker(JSON.stringify(getSpeakerData(span)));
}

function imageClick(event) {
	console.log('imageClick()');
	var img = event.target;

	console.log('imageClick() img = ' + img);
	//通知安卓端
	window.jsInterface.clickImage(JSON.stringify(getImgData(img)));
}

function imageLongClick(event) {
	console.log('imageLongClick()');
	var img = event.target;


	if (null == mImageMoreBtnList) {
		mImageMoreBtnList = new Array();
		mImageMoreBtnList.push({'type': MORE_BTN_TYPE_DELETE,"text": MORE_BTN_TEXT_DELETE});
	}
	// var btnList = new Array();
	// btnList[0] = {'type': 1,"text": "查看图片"};
	// btnList[1] = {'type': 2,"text": "文字识别"};
	// btnList[2] = {'type': MORE_BTN_TYPE_DELETE,"text": MORE_BTN_TEXT_DELETE};
	
	var clientY = event.changedTouches[0].clientY;
	
	showMoreBtn(img, mImageMoreBtnList, clientY);
}

function imageTextClick(event) {
	console.log('imageTextClick()');
	var elm = event.target;
	if ('insert-imgtxt-bg' != elm.className) {
		elm = elm.parentNode;//若点击的不是图文的根节点，则需要获取父节点（即根节点）
	}
	//通知安卓端
	window.jsInterface.clickImageText(JSON.stringify(getImageTextData(elm)));
}

//获取需要插入位置的下一个节点
function getInsertPositionElement(startTime) {
	var nextElement;
	var textList = document.getElementsByClassName(mIsEdit ? 'edit-text' : 'common-text');
	for (var i = 0; i < textList.length; i++) {
		if (startTime < textList[i].dataset['startTime']) {
			nextElement = textList[i];
			break;
		}
	}

	return nextElement;
}

function createTextElement(content, index, startTime, endTime, speakerId, speakerName, size, isEdit) {
	var span = document.createElement('span');
	span.dataset['index'] = index;
	span.dataset['startTime'] = startTime;
	span.dataset['endTime'] = endTime;
	span.innerHTML = content;
	span.className = isEdit ? 'edit-text' : 'common-text';
	span.setAttribute("contenteditable", isEdit);
	span.addEventListener("click", textClick); 
	if (size > 0) span.style.fontSize = size + 'px';
	if (null != speakerId) span.dataset['speakerId'] = speakerId;
	if (null != speakerName) span.dataset['speakerName'] = speakerName;

	return span;
}

function createSpeakerElement(speakerId, speakerName, index, size, isEdit, isShow) {
	var span = document.createElement('span');
	span.dataset['speakerId'] = speakerId;
	span.dataset['speakerName'] = speakerName;
	span.innerHTML = speakerName + ": ";
	span.className = 'speaker-text';
	span.style.color = getSpeakerColor(speakerId);
	span.style.display = isShow ? "inline" : "none";
	span.setAttribute("contenteditable", isEdit);
	span.addEventListener("click", speakerClick); 
	if (size > 0) span.style.fontSize = size + 'px';

	return span;
}

function createImageElement(url, index, startTime, isEdit) {
	var img = document.createElement('img');
	img.dataset['index'] = index;
	img.dataset['startTime'] = startTime;
	img.className = 'insert-image';
	img.src = url;
	img.setAttribute("contenteditable", isEdit);
	// img.addEventListener("click", imageClick); 
	addClickListener(img, imageClick, imageLongClick);

	return img;
}

function createImageWithText(url, index, startTime, content, isEdit) {
	var p = document.createElement('p');
	p.className = 'insert-imgtxt-bg';
	p.addEventListener("click", imageTextClick); 

	var img = document.createElement('img');
	img.dataset['index'] = index;
	img.dataset['startTime'] = startTime;
	img.className = 'insert-imgtxt-img';
	img.src = url;

	var span = document.createElement('span');
	span.innerHTML = content;
	span.className = 'insert-imgtxt-txt';

	p.appendChild(img);
	p.appendChild(document.createElement('br'));
	p.appendChild(span);
	p.setAttribute("contenteditable", isEdit);

	return p;
}

function createLineFeedElement(id, marginTop) {
	var span = document.createElement('span');
	span.id = id;
	span.style.display = "block";	
	span.style.marginTop = marginTop + "px";

	return span;
}

//获取发言人颜色
function getSpeakerColor(speakerId) {
	var color = mSpeakerColorMap.get(speakerId);
	if (null != color) {
		return color;
	}

	if (mSpeakerColorIndex >= mSpeakerColorArray.length) {
		mSpeakerColorIndex = 0;
	}

	color = mSpeakerColorArray[mSpeakerColorIndex++];
	mSpeakerColorMap.set(speakerId, color);
	return color;
}

//获取当前文档数据
function getCurrentData() {
	console.log('getCurrentData()');
	var contentNode = document.getElementById('content');
	var childs = contentNode.childNodes;
	var datalist = [];
	for (var i = 0; i < childs.length; i++) {	
		var itemObj;
		if('common-text' == childs[i].className || 'edit-text' == childs[i].className) {
			itemObj = getTextData(childs[i]);

		} else if ('insert-image' == childs[i].className) {
			itemObj = getImgData(childs[i]);

		} else if ('insert-imgtxt-bg' == childs[i].className) {
			itemObj = getImageTextData(childs[i]);

		} else {
			continue;
		}

		datalist.push(itemObj);
	}

	return {"datalist" : datalist};
}

//获取文本元素数据
function getTextData(elm) {
	return {
		"type": TYPE_TEXT, 
		"index": elm.dataset['index'], 
		"startTime": elm.dataset['startTime'], 
		"endTime": elm.dataset['endTime'],
		"speakerInfo": getSpeakerData(elm),
		"content": elm.innerHTML};
}

//获取发言人元素数据
function getSpeakerData(elm) {
	return {
		"id": elm.dataset['speakerId'],
		"name": elm.dataset['speakerName']};
}

//获取图片元素数据
function getImgData(elm) {
	return {
		"type": TYPE_IMAGE, 
		"index": elm.dataset['index'], 
		"startTime": elm.dataset['startTime'], 
		"imageUrl": elm.src};
}

//获取图文元素数据
function getImageTextData(elm) {
	var imgtxtChildren = elm.childNodes;
	if (null == imgtxtChildren) {
		return;		
	}

	var index, url, startTime, content;
	for (var k = 0; k < imgtxtChildren.length; k++) {
		if ('insert-imgtxt-img' == imgtxtChildren[k].className) {
			url = imgtxtChildren[k].src;
			index = imgtxtChildren[k].dataset['index'];
			startTime = imgtxtChildren[k].dataset['startTime'];
					
		} else if ('insert-imgtxt-txt' == imgtxtChildren[k].className) {
			content = imgtxtChildren[k].innerHTML;
		}
	}

	return {
		"type": TYPE_IMAGE_TEXT, 
		"index": index, 
		"startTime": startTime, 
		"imageUrl": url,
		"content": content};
}

//获取更多按钮元素数据
function getMoreBtnData(elm) {
	return {
		"type": elm.dataset['type'], 
		"text": elm.dataset['text']};
}

function getDataObj(index) {
	if (null == contentJson || null == contentJson.datalist) {
		return null;
	}

	var data = contentJson.datalist[index];
	if (null != data && data.index == index) {
		return data;
	}

	for (var i = 0; i < contentJson.datalist.length; i++) {
		data = contentJson.datalist[index];
		if (null != data && data.index == index) {
			return data;
		}
	}

	return null;
}

/**
 * 显示更多按钮
 *
 * @param elem    宿主元素
 * @param btnList 按钮数据列表
 * @param touchY  显示位置（y坐标）
 */
function showMoreBtn(elem, btnList, touchY) {
	console.log('showMoreBtn()');

	var moreBtnPop = document.getElementById("img-more-btn-pop");
	moreBtnPop.style.visibility="visible";
	moreBtnPop.addEventListener("click", function(event) {
		moreBtnPop.style.visibility="hidden";
	}); 
	moreBtnPop.addEventListener("touchmove", function(event) {
		moreBtnPop.style.visibility="hidden";
	});


	var btnElements = document.getElementById('img-more-btn-bg')
	var childs = btnElements.childNodes; 
	for(var i = childs.length - 1; i >= 0; i--) {
  		btnElements.removeChild(childs[i]);//清空原先的按钮
	}

	for (var i = 0; i < btnList.length; i++) {
		var span = document.createElement('span');
		var text = btnList[i].text;
		if (i != btnList.length - 1) {
			text += ' | ';
		}
		span.innerHTML = text;
		span.dataset['type'] = btnList[i].type;
		span.dataset['text'] = btnList[i].text;
		span.className = 'img-more-btn-txt';
		btnElements.appendChild(span);
		span.addEventListener("click", function(event) {
			var span = event.target;
			var type = span.dataset['type'];
			console.log('showMoreBtn() click type = ' + type);
			//event.stopPropagation();
			if (MORE_BTN_TYPE_DELETE == span.dataset['type']) {
				//删除
				document.getElementById('content').removeChild(elem);
			}

			window.jsInterface.clickMoreBtnItem(JSON.stringify(getMoreBtnData(span)), JSON.stringify(getImgData(elem)));
		});
	}


	var moreBtnBg = document.getElementById("img-more-btn-bg");
	moreBtnBg.style.marginTop = (touchY - moreBtnBg.getBoundingClientRect().height + "px");
}

/**
 * 用于测试
 */
function testMain() {
	var content = '{"datalist":[{"content":"滚滚长江东逝水，浪花淘尽英雄。","endTime":1.1,"index":0,"startTime":0.1,"type":0},{"imageUrl":"http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg","endTime":0.0,"index":1,"startTime":1.1,"type":1},{"content":"是非成败转头空。青山依旧在，几度夕阳红。","endTime":2.1,"index":1,"startTime":1.1,"type":0}]}';
	setContent(content);
}
