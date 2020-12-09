package co.deshbidesh.db_android.shared

class DBHelper {

    fun generateDescriptionFromContent(content: String): String {

        var stringArray: List<String> = content.split(" ")

        return if (stringArray.count() > 15) {

            stringArray.take(15).joinToString(separator = " ") { it ->
                "$it"
            }
        } else {

            stringArray.joinToString(separator = " ") { it ->
                "$it"
            }
        }
    }
}