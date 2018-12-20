function test() {
    // var xmlhttp;
    // if (window.XMLHttpRequest)
    // {// code for IE7+, Firefox, Chrome, Opera, Safari
    //     xmlhttp=new XMLHttpRequest();
    // }
    // else
    // {// code for IE6, IE5
    //     xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    // }

    var keyword = document.getElementById("input").value;

    // xmlhttp.open("POST", "/test", true);
    // xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    // xmlhttp.send("data=" + keyword);
    // xmlhttp.onreadystatechange=function()
    // {
    //     var result = xmlhttp.responseText
    //     if (xmlhttp.readyState==4 && xmlhttp.status==200)
    //     {
    //         // alert(result)
    //         document.getElementById("test").innerHTML = result;
    //     }
    // }

    // xmlhttp.open("GET", "/search?keyword="+keyword, true);
    // xmlhttp.send();

    var form = document.getElementById("form");
    if (keyword != null && keyword.length > 0) {
        form.submit();
    }
}