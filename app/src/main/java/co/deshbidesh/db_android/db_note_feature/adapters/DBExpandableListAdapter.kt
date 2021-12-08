package co.deshbidesh.db_android.db_note_feature.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.models.DBNoteDateRepresentable
import co.deshbidesh.db_android.shared.utility.DBCalenderMonth

class DBExpandableListAdapter internal constructor(
    private val context: Context,
    var yearList: MutableList<DBNoteDateRepresentable>,
    var monthHashMap: HashMap<Int, MutableList<DBNoteDateRepresentable>>
): BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.monthHashMap[this.yearList[groupPosition].dateInt]!![childPosition]
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val yearCount = getGroup(groupPosition) as DBNoteDateRepresentable
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.layout_expandable_item, null)
        }
        val listTitleTextView = convertView!!.findViewById<TextView>(R.id.note_list_date_expandable_item_title)
        //listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = "${yearCount.dateInt} - No of Notes: ${yearCount.count}"//listTitle"
        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView

        val monthCount = getChild(groupPosition, childPosition) as DBNoteDateRepresentable

        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.layout_expandable_item, null)
        }
        val expandedListTextView = convertView!!.findViewById<TextView>(R.id.note_list_date_expandable_item_title)
        val rightImageView = convertView!!.findViewById<ImageView>(R.id.note_list_date_item_right_image)
        rightImageView.isVisible = true
        expandedListTextView.text = "  - ${DBCalenderMonth.months[monthCount.dateInt - 1]} - No of Notes: ${monthCount.count}"//expandedListText
        return convertView
    }

    override fun getGroupCount(): Int = this.yearList.size

    override fun getChildrenCount(groupPosition: Int): Int {
        return return this.monthHashMap[this.yearList[groupPosition].dateInt]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.yearList[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun notifyDataChanged() {
        this.notifyDataSetChanged()
    }
}