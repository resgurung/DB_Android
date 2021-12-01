package co.deshbidesh.db_android.db_note_feature.models

import co.deshbidesh.db_android.db_note_feature.infinite_list.DBNoteDataType

class DBNoteWrapper(
    var note: DBNote? = null,
    var dateRepresentable: DBNoteDateRepresentable? = null,
    var type: DBNoteDataType,
    var year:Int? = null
    )