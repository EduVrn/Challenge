Share = {
    // vkontakte: function () {
    //      url = 'http://vkontakte.ru/share.php?';
    //    url += 'url=' + encodeURIComponent(document.URL);
    //    url += '&title=' + encodeURIComponent(document.title);
    //    url += '&description=' + encodeURIComponent(document.getElementById("desc").content);
    //     url += '&image=' + encodeURIComponent(document.getElementById("img").content);
    //    url += '&noparse=true';
    //   Share.popup(url);
    //},
    facebook: function () {
        url = 'http://www.facebook.com/sharer.php?s=100';
        url += '&p[title]=' + encodeURIComponent(document.title);
        url += '&p[summary]=' + encodeURIComponent(document.getElementById("desc").content);
        url += '&p[url]=' + encodeURIComponent(document.URL);
        url += '&p[images][0]=' + encodeURIComponent(document.getElementById("img").content);
        Share.popup(url);
    },
    twitter: function () {
        url = 'http://twitter.com/share?';
        url += 'text=' + encodeURIComponent(document.title);
        url += '&url=' + encodeURIComponent(document.URL);
        Share.popup(url);
    },
    me: function (el) {
        console.log(el.href);
        Share.popup(el.href);
        return false;
    },
    popup: function (url) {
        window.open(url, '', 'toolbar=0,status=0,width=626,height=436');
    }
};

     