package co.deshbidesh.db_android.db_note_feature.models

class DBNoteInfiniteAdapterModel(
    var type: Int,
    var note: DBNote?
    ) {

    companion object{

        const val VIEW_TYPE_DATA        = 0

        const val VIEW_TYPE_PROGRESS    = 1
    }
}