package com.A306.runnershi.Helper

class WebViewConstant {
    val HTML_TEXT = "<html>\n" +
            "\n" +
            "<head>\n" +
            "\t<title>openvidu-insecure-js</title>\n" +
            "\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" charset=\"utf-8\">\n" +
            "\n" +
            "\t<!-- Bootstrap -->\n" +
            "\t<script src=\"https://code.jquery.com/jquery-3.3.1.min.js\" integrity=\"sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=\" crossorigin=\"anonymous\"></script>\n" +
            "\t<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\n" +
            "\t<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>\n" +
            "\t<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
            "\t<!-- Bootstrap -->\n" +
            "\n" +
            "\t<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" media=\"screen\">\n" +
            "\t<script src=\"file:///android_asset/openvidu-browser-2.17.0.js\"></script>\n" +
            "\t<script src=\"file:///android_asset/app.js\"></script>\n" +
            "\t<script src=\"file:///android_asset/jquery.js\"></script>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "\n" +
            "\t<div id=\"main-container\" class=\"container\">\n" +
            "\t\t<div id=\"join\">\n" +
            "\t\t\t<div id=\"join-dialog\" class=\"jumbotron vertical-center\">\n" +
            "\t\t\t\t<form class=\"form-group\" onsubmit=\"joinSession(); return false\">\n" +
            "\t\t\t\t\t<p>\n" +
            "\t\t\t\t\t\t<input class=\"form-control\" type=\"text\" id=\"userName\" required>\n" +
            "\t\t\t\t\t</p>\n" +
            "\t\t\t\t\t<p>\n" +
            "\t\t\t\t\t\t<input class=\"form-control\" type=\"text\" id=\"sessionId\" required>\n" +
            "\t\t\t\t\t</p>\n" +
            "\t\t\t\t\t<p class=\"text-center\">\n" +
            "\t\t\t\t\t\t<input class=\"btn btn-lg btn-success\" type=\"submit\" name=\"commit\" value=\"Join!\">\n" +
            "\t\t\t\t\t</p>\n" +
            "\t\t\t\t</form>\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\n" +
            "\t\t<div id=\"session\" style=\"display: none;\">\n" +
            "\t\t\t<div id=\"session-header\">\n" +
            "\t\t\t\t<h1 id=\"session-title\"></h1>\n" +
            "\t\t\t\t<input class=\"btn btn-large btn-danger\" type=\"button\" id=\"buttonLeaveSession\" onmouseup=\"leaveSession()\" value=\"방 나가기\">\n" +
            "\t\t\t</div>\n" +
            "\t\t\t<div id=\"main-video\" class=\"col-md-6\"><p></p><video autoplay playsinline=\"true\"></video></div>\n" +
            "\t\t\t<div id=\"video-container\" class=\"col-md-6\"></div>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "</body>\n" +
            "\n" +
            "</html>"
}