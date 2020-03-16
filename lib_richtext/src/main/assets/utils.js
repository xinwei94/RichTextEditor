

/**
 * 添加上短按事件监听
 *
 * @param elem       元素
 * @param shortPress 短按回调
 * @param longPress  长按回调
 */
function addClickListener(elem, shortPress, longPress) {
	console.log('addClickListener');
	var time;
	var isLong = false;
	elem.addEventListener("touchstart", function (e) {
		timer = setTimeout(function () {
			e.preventDefault();
			isLong = true;
			longPress(e);
		}, 800);
	});

	elem.addEventListener("touchmove", function (e) {
		clearTimeout(timer);
		timer = 0;
		if (isLong) {
			e.preventDefault();
		}
	});
	elem.addEventListener("touchend", function (e) {
		clearTimeout(timer);
		if (!isLong) {
			e.preventDefault();
			shortPress(e);
		}
		isLong = false;
 });
}

/**
 * 显示元素（滚动确保元素显示）
 * @param {Object} elm
 */
function showElement(elm) {
	//元素位置可见，不需要滚动
	if (checkVisible(elm)) {
		return;	
	}

	var topPos = elm.offsetTop + elm.offsetHeight;
	console.log('showElement() isVisible = false, scrollTop = ' + topPos);
    window.scrollTo(0, topPos);
}

/**
 * 判断元素是否可见
 * @param {Object} elm
 */
function checkVisible(elm) {
  	var rect = elm.getBoundingClientRect();
  	//获取当前浏览器的视口高度，不包括工具栏和滚动条
  	var viewHeight = Math.max(document.documentElement.clientHeight, window.innerHeight);
  	//bottom top是相对于视口的左上角位置，bottom大于0或者top-视口高度小于0可见
  	//return !(rect.bottom < 0 || rect.top - viewHeight >= 0);//只露出一点即为可见
  	return !(rect.bottom < 0 || rect.bottom - viewHeight >= 0);//完全显示才当成可见
}