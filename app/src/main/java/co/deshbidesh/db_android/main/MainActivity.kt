package co.deshbidesh.db_android.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import co.deshbidesh.db_android.R
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
    }

    // navigation arrow from detail back to main
    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setBottomNavigationVisibility(visibility: Int) {

        bottomNavigationView.visibility = visibility
    }
}