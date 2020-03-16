/*
 * Html存储为doc文件相关
 */

//保存doc文档
function saveDocFile() {
	console.log('saveDocFile()');
	
	var statics = {
		mhtml: {
			top: "Mime-Version: 1.0\nContent-Base: " + location.href + "\nContent-Type: Multipart/related; boundary=\"NEXT.ITEM-BOUNDARY\";type=\"text/html\"\n\n--NEXT.ITEM-BOUNDARY\nContent-Type: text/html; charset=\"utf-8\"\nContent-Location: " + location.href + "\n\n<!DOCTYPE html>\n<html>\n_html_</html>",
			head: "<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n<style>\n_styles_\n</style>\n</head>\n",
			body: "<body>_body_</body>"
		}
	};

	//克隆需要保存的内容区域
	var markup = $("#content").clone();
 	
    //移除不显示的标签
    markup.each(function () {
        var self = $(this);
        if (self.is(':hidden'))
            self.remove();
    });

    var img = markup.find('img');
	var images = Array();
	for (var i = 0; i < img.length; i++) {
		var src = img[i].src;
		var imgType = "image/" + src.substring(src.lastIndexOf(".")+1, src.length);
		console.log("imgType = " + imgType + ", src = " + src);
		images[i] = {
			type: imgType,
			encoding: "base64",
			location: src,
			data: getImageBase64Data(src),
		};
	}
            
	var mhtmlBottom = "\n";
	for (var i = 0; i < images.length; i++) {
		mhtmlBottom += "--NEXT.ITEM-BOUNDARY\n";
		mhtmlBottom += "Content-Location: " + images[i].location + "\n";
		mhtmlBottom += "Content-Type: " + images[i].type + "\n";
		mhtmlBottom += "Content-Transfer-Encoding: " + images[i].encoding + "\n\n";
		mhtmlBottom += images[i].data + "\n\n";
	}
	mhtmlBottom += "--NEXT.ITEM-BOUNDARY--";

	var styles = "";
 
    //生成world文档内容
    var fileContent = statics.mhtml.top.replace("_html_", statics.mhtml.head.replace("_styles_", styles) + statics.mhtml.body.replace("_body_", markup.html())) + mhtmlBottom;
 	//通知安卓端保存文档
    window.jsInterface.saveDocFileContent(fileContent);
}

//通过客户端获取图片的base64数据（js需要创建canvas，且存在跨域问题）
function getImageBase64Data(src) {
	console.log("getImageBase64() src = " + src);
	return window.jsInterface.getImageBase64Data(src);
}


/*	
*  解决Android webview无法下载url为blob开头的文件问题
*
function downloadBlob(blobUrl) {
	console.log("downloadBlob() blobUrl = " + blobUrl);
	var xhr = new XMLHttpRequest();
	xhr.open('GET', blobUrl, true);
	xhr.setRequestHeader('Content-type','application/sb3');
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var blobPdf = this.response;
			var reader = new FileReader();
			reader.readAsDataURL(blobPdf);
			reader.onloadend = function() {
				var base64data = reader.result;
				console.log(base64data);//已经拿到了base64字节码，Base64解密字符串，保存为doc文件即可
			}
		}else{
			alert(this.status);
		}
	};

	xhr.onerror=function(data){
		alert(data);
	}; 

	xhr.send();
}*/