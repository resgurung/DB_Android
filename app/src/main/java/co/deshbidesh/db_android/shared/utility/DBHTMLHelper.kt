package co.deshbidesh.db_android.shared.utility

import android.graphics.Color

class DBHTMLHelper {

    companion object {

        fun htmlHelper(text: String): String {

            return "<!DOCTYPE html> " +
                        "<html> " +
                        "<head> </head>" +
                        "<meta name= viewport content= width=device-width  initial-scale=1.0 > " +
                        "<style>body{color: #0099CC;} img{display: inline;height: auto;max-width: 100%;} video{display: inline;width: 100%;poster=} p{height: auto;width: 100%; } iframe{width: 100%} </style> " +
                        "<body>   ${text.replace("\"","")} </body></html>"

        }
    }
}