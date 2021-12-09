package co.deshbidesh.db_android.main

import android.annotation.SuppressLint
import android.content.Context
import android.inputmethodservice.KeyboardView
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.shared.keyboard.DBCustomKeyboard
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // nepali keyboard
    var showNepaliKeyboard: Boolean = false

    var systemKeyboard: Boolean = false

    lateinit var adView: AdView

    private lateinit var keyboardView: KeyboardView

    private var customKeyboard: DBCustomKeyboard? = null

    // navigation
    private lateinit var navController: NavController

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        navController = Navigation.findNavController(this@MainActivity, R.id.main_fragment_container)

        bottomNavigationView.setupWithNavController(navController)

        keyboardView = findViewById(R.id.keyboardview)

        customKeyboard = DBCustomKeyboard(this, keyboardView, R.xml.qwerty0)

        adView = findViewById(R.id.main_adView)

        val adMobRequest = AdRequest
            .Builder()
            .build()

        adView.loadAd(adMobRequest)
    }

    fun setBottomNavigationVisibility(visibility: Int) {

        bottomNavigationView.visibility = visibility
    }

    override fun onBackPressed() {
        if (isCustomKeyboardVisible()) {

            hideCustomKeyboard()

        } else {

            super.onBackPressed()
        }
    }

    /** hide system keyboard */
    fun hideSystemKeyboard() {

        // Check if no view has focus:
        val view = this.currentFocus

        view?.let {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

            imm?.hideSoftInputFromWindow(it.windowToken, 0)

            systemKeyboard = false
        }
    }
    /**  show system keyboard*/
    fun showSystemKeyboard() {

        if (!systemKeyboard) {

            val view = this.currentFocus

            view?.let {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

                imm?.showSoftInput(it, InputMethodManager.SHOW_FORCED)

                systemKeyboard = true
            }
        }
    }

    /** check if custom is shown in the screen */
    fun isCustomKeyboardVisible(): Boolean {

        return keyboardView.visibility == View.VISIBLE
    }

    /** Make the CustomKeyboard invisible. */
    fun hideCustomKeyboard() {

        keyboardView.visibility = View.GONE

        keyboardView.isEnabled = false
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view. */
    private fun showCustomKeyboard() {

        if (!isCustomKeyboardVisible()) {

            keyboardView.visibility = View.VISIBLE

            keyboardView.isEnabled = true
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun registerEditText(editText: EditText) {

        // Make the custom keyboard appear

        editText.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {

                handleKeyboard()
            }
        }

        editText.setOnClickListener {

            handleKeyboard()
        }

        editText.setOnLongClickListener(View.OnLongClickListener {

            handleKeyboard()

            false
        })


        editText.setOnTouchListener { v, event ->

            if (showNepaliKeyboard) {

                val et = v as EditText

                val layout = et.layout

                val inputType = et.inputType // Backup the input type

                et.inputType = InputType.TYPE_NULL

                et.onTouchEvent(event)

                et.inputType = inputType

                if (layout != null) {

                    val line = layout.getLineForVertical(event.y.toInt())

                    val offset = layout.getOffsetForHorizontal(line, event.x)

                    if (offset > 0) {
                        // now set the touch position
                        et.setSelection(offset)
                    }
                }
                // return as normal
                true

            } else {

                false
            }

        }

        // Disable spell check
        //editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    fun handleKeyboard(){

        if (showNepaliKeyboard) {

            showCustomKeyboard()

            hideSystemKeyboard()

        } else {

            showSystemKeyboard()

            hideCustomKeyboard()

        }
    }

    fun resetKeyboard() {

    }
}