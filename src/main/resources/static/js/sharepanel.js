var share = Ya.share2('share', {
    content: {
        title: 'Challenge',
        description: $("#desc").text(),
        image: 'http://goalsinfinite.com/wp-content/uploads/2016/04/the-challenge.png'
    },
    contentByService: {
        facebook: {
            title: 'Challenge',
            description: $("#desc").text(),
            image: 'http://goalsinfinite.com/wp-content/uploads/2016/04/the-challenge.png',
            accessToken: 'fb-token'
        }
    },
    theme: {
        services: 'vkontakte,facebook,twitter',
        counter: true
    }
});