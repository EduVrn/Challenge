function uLoginAnswer(token) {
    $.getJSON("//ulogin.ru/token.php?host=" +
            encodeURIComponent(location.toString()) + "&token=" + token + "&callback=?",
            function (data) {
                data = $.parseJSON(data.toString());
                if (!data.error) {
                    //если пользователь залогинился через вк
                    //document.location.href = "";//"WEB-INF/jsp/index.jsp?username=" + data.first_name;
                    alert("Привет, " + data.first_name + " " + data.last_name + "; id: " + data.uid + "!");
                } else
                    alert("Ошибка");
            });
}
;

