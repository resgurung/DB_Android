package co.deshbidesh.db_android.shared.utility

class DBHTMLHelper {

    companion object {

        fun htmlHelper(text: String): String {

            return "<!DOCTYPE html> " +
                        "<html> " +
                        "<head> </head>" +
                        "<meta name= viewport content= width=device-width  initial-scale=1.0 > " +
                        "<style>" +
                    "body{ background-color: transparent;} " +
                    "img{display: inline;height: auto;max-width: 100%;} " +
                    "video{display: inline;width: 100%;poster=} " +
                    "p{height: auto;width: 100%; } " +
                    "iframe{width: 100%} " +
                    "div { margin-bottom: 70px; }" +
                    "</style> " +
                        "<body>   ${text.replace("\"","")} </body></html>"
        }
    }
}
//color: #fff;