package co.deshbidesh.db_android.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_database.database.DBDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        navController = Navigation.findNavController(this@MainActivity, R.id.main_fragment_container)

        bottomNavigationView.setupWithNavController(navController)

        DBDatabase.getDatabase(application)
    }

    fun setBottomNavigationVisibility(visibility: Int) {

        bottomNavigationView.visibility = visibility
    }
}