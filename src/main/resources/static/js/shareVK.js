function showVkShareButton() {
    document.write(VK.Share.button({
        url: document.URL,
        title: document.title,
        description: document.getElementById("desc").content,
        image: document.getElementById("img").content,
        noparse: false
    }, {type: 'custom',
        text: '<img src="http://vk.com/images/vk32.png" />'}));
}


