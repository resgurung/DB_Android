package co.deshbidesh.db_android.DBCalendarFeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.Shared.DBAppBarConfiguration
import co.deshbidesh.db_android.Shared.DBBaseFragment


class CalendarFragment : DBBaseFragment() {

    private lateinit var toolBar: Toolbar

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = view.findViewById(R.id.calendar_fragment_toolbar)

        toolBar.inflateMenu(R.menu.calendar_top_menu)

        toolBar.setOnMenuItemClickListener { item ->

            when(item.itemId) {

                R.id.calendar_menu_today -> {

                    menuTodayPressed()

                    true
                }
                else -> false
            }

        }
        navController = NavHostFragment.findNavController(this);

        NavigationUI.setupWithNavController(toolBar, navController, DBAppBarConfiguration.configuration())
    }

    private fun menuTodayPressed() {

        Toast.makeText(context, "Today Menu button Clicked", Toast.LENGTH_LONG).show()
    }
}