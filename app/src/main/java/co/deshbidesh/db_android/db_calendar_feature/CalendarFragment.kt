package co.deshbidesh.db_android.db_calendar_feature

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentCalendarBinding
import co.deshbidesh.db_android.shared.DBAppBarConfiguration
import co.deshbidesh.db_android.shared.DBBaseFragment
import com.puskal.merocalendar.DateClickListener
import com.puskal.merocalendar.MonthChangeListener
import com.puskal.merocalendar.enum.CalendarType
import com.puskal.merocalendar.enum.LocalizationType
import com.puskal.merocalendar.model.DateModel


class CalendarFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private lateinit var toolBar: Toolbar

    //private lateinit var navController: NavController

    private var _binding: FragmentCalendarBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCalendarBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = binding.calendarFragmentToolbar

        toolBar.inflateMenu(R.menu.calendar_top_menu)

        toolBar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        toolBar.setOnMenuItemClickListener { item ->

            when(item.itemId) {

                R.id.calendar_menu_today -> {

                    menuTodayPressed()

                    true
                }
                else -> false
            }

        }
        //navController = NavHostFragment.findNavController(this);

        //NavigationUI.setupWithNavController(toolBar, navController, DBAppBarConfiguration.configuration())

        val dateClickListener = object : DateClickListener {
            override fun onDateClick(dateModel: DateModel) {
                Log.d("d", "datemodel ${dateModel.formattedAdDate}")

                val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_calendar_day, null)
                val mBuilder = AlertDialog.Builder(requireContext())
                    .setView(mDialogView)

                val yearMonthTV = mDialogView.findViewById<TextView>(R.id.calendar_year_month_tv)

                val dayTV = mDialogView.findViewById<TextView>(R.id.calendar_day_tv)

                val addNoteButton = mDialogView.findViewById<Button>(R.id.calendar_add_note)

                val cancelPopup = mDialogView.findViewById<Button>(R.id.calendar_close_popup)

                yearMonthTV.text = dateModel.localizedBSMonthYear

                dayTV.text = dateModel.localizedSDay

                val mAlertDialog = mBuilder.show()

                addNoteButton.setOnClickListener {

                }

                cancelPopup.setOnClickListener {

                    mAlertDialog.dismiss()
                }
            }
        }

        val monthChangeListener = object : MonthChangeListener {
            override fun onMonthChange(
                startDateOfThisMonth: DateModel,
                endDateOfThisMonth: DateModel,
                adYear: Int,
                adMonth: Int
            ) {
                /**if your api get event monthwise, or want to show event monthwise, get event using this method
                 *eg: you already got starDate and End Date of current visible month from this override method,pass this data to server or filter from local stored event to get ovrerall event of this month
                 *
                 * you can also pass whole event without using this listener by passing event on calendarview object directly Such as-> mCalendarView.setEvent(fakeEventList), library will auto filter
                 *but for high performance its better to send event belong to this month or in range of start date - to end date
                 */
                //for example consider this fakeEventList is the list of event belongs to this month that is receive from api after query date range from here
                //after getting this list setevent to calendar as given below

                //binding.mCalendarView.setEvent(/*Any Events*/)

            }
        }
        // setup Calendar
        binding.mCalendarView.setCalendarType(CalendarType.BS)
            .setLanguage(LocalizationType.NEPALI_NP)
            .setOnDateClickListener(dateClickListener)
            .setOnMonthChangeListener(monthChangeListener)
            //.setEvent(fakeEventList)  //you can also add event separately like inside api response function such as binding.mCalendarView.setEvent(eventResponse)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun menuTodayPressed() {

        Toast.makeText(context, "Today Menu button Clicked", Toast.LENGTH_LONG).show()
    }
}