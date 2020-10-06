package DBNotes

import java.util.*

class DBNote(

    var title:          String,  // Title of the note

    var content:        String,  // Main content of the note

    var createdDate:    Date, // When note was created

    var updatedDate:    Date,  // when note was updated

    var languageType:   LanguageType // is the text english
) {
}