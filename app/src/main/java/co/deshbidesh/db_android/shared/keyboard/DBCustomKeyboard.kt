package co.deshbidesh.db_android.shared.keyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.AUDIO_SERVICE
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.media.AudioManager
import android.media.AudioManager.*
import android.view.View
import android.widget.EditText
import co.deshbidesh.db_android.R


class DBCustomKeyboard(
    private val host: Activity,
    private val keyboardView: KeyboardView,
    private val layoutId: Int
): KeyboardView.OnKeyboardActionListener  {

    val CodeDelete = -5 // Keyboard.KEYCODE_DELETE

    val CodeCancel = -3 // Keyboard.KEYCODE_CANCEL

    val CodePrev = 55000
    val CodeAllLeft = 55001
    val CodeLeft = 55002
    val CodeRight = 55003
    val CodeAllRight = 55004
    val CodeNext = 55005
    val CodeClear = 55006

    private val EK_DUI_TIN = -7

    private val KA_KHA_GA = -8

    private val CHYA = -15

    private val TRA = -16

    private val ZYA = -17

    init {

        createKeyboard(layoutId)
    }

    fun switchKeyboard(keyboardId: Int) {

        when(keyboardId) {

            -7 -> {
                createKeyboard(R.xml.qwerty1)
            }
            -8 -> {
                createKeyboard(R.xml.qwerty0)
            }
            else -> {

                createKeyboard(R.xml.qwerty0)
            }
        }
    }

    private fun createKeyboard(layoutId: Int) {

        keyboardView.keyboard = Keyboard(host, layoutId)

        keyboardView.isPreviewEnabled = false // NOTE Do not show the preview balloons

        keyboardView.setOnKeyboardActionListener(this)
    }

    /**
     * Send a key press to the listener.
     * @param primaryCode this is the key that was pressed
     * @param keyCodes the codes for all the possible alternative keys
     * with the primary code being the first. If the primary key code is
     * a single character such as an alphabet or number or symbol, the alternatives
     * will include other characters that may be on the same key or adjacent keys.
     * These codes are useful to correct for accidental presses of a key adjacent to
     * the intended key.
     */
    @SuppressLint("WrongConstant")
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {

        // Get the EditText and its Editable
        val focusCurrent = host.window.currentFocus

        if (focusCurrent == null || focusCurrent !is EditText) return

        val editText = focusCurrent as EditText

        val editable = editText.text

        val start = editText.selectionStart

        // Handle key
        if (primaryCode === CodeCancel) {

            // delegate hide custom keyboard
            //hideCustomKeyboard()

        } else if (primaryCode === CodeDelete) {

            if (editable != null && start > 0) editable.delete(start - 1, start)

        } else if (primaryCode === CodeClear) {

            editable?.clear()

        } else if (primaryCode === CodeLeft) {

            if (start > 0) editText.setSelection(start - 1)

        } else if (primaryCode === CodeRight) {

            if (start < editText.length()) editText.setSelection(start + 1)

        } else if (primaryCode === CodeAllLeft) {

            editText.setSelection(0)

        } else if (primaryCode === CodeAllRight) {

            editText.setSelection(editText.length())

        } else if (primaryCode === CodePrev) {

            val focusNew = editText.focusSearch(View.FOCUS_BACKWARD)

            focusNew?.requestFocus()

        } else if (primaryCode === CodeNext) {

            val focusNew = editText.focusSearch(View.FOCUS_FORWARD)

            focusNew?.requestFocus()

        }  else if(primaryCode === EK_DUI_TIN) {

            switchKeyboard(EK_DUI_TIN)

        } else if (primaryCode === KA_KHA_GA) {

            switchKeyboard(KA_KHA_GA)

        } else if (primaryCode === CHYA) {

            editable!!.insert(start, "क्ष")

        } else if (primaryCode === TRA) {

            editable!!.insert(start, "त्र")

        } else if (primaryCode === ZYA) {

            editable!!.insert(start, "ज्ञ")
        }
        else { // Insert character

            editable!!.insert(start, primaryCode.toChar().toString())
        }
    }

    /**
     * Called when the user presses a key. This is sent before the [.onKey] is called.
     * For keys that repeat, this is only called once.
     * @param primaryCode the unicode of the key being pressed. If the touch is not on a valid
     * key, the value will be zero.
     */
    override fun onPress(primaryCode: Int) {}

    /**
     * Called when the user releases a key. This is sent after the [.onKey] is called.
     * For keys that repeat, this is only called once.
     * @param primaryCode the code of the key that was released
     */
    override fun onRelease(primaryCode: Int) {}

    /**
     * Sends a sequence of characters to the listener.
     * @param text the sequence of characters to be displayed.
     */
    override fun onText(text: CharSequence?) {}

    /**
     * Called when the user quickly moves the finger from right to left.
     */
    override fun swipeLeft() {}

    /**
     * Called when the user quickly moves the finger from left to right.
     */
    override fun swipeRight() {}

    /**
     * Called when the user quickly moves the finger from up to down.
     */
    override fun swipeDown() {}

    /**
     * Called when the user quickly moves the finger from down to up.
     */
    override fun swipeUp() {}

    /**
     * Plays a key sound when the user presses key in the keyboard.
     */
//    fun playClick(i: Int) {
//
//        val audioManager = host.getSystemService(AUDIO_SERVICE) as AudioManager
//
//        when (i) {
//
//            else -> { audioManager.playSoundEffect(FX_KEYPRESS_STANDARD)}
//        }
//
//    }


    private fun playClick(keyCode: Int) {
        val am = host.getSystemService(AUDIO_SERVICE) as AudioManager?
        when (keyCode) {
            32 -> am!!.playSoundEffect(FX_KEYPRESS_SPACEBAR)
            10 -> am!!.playSoundEffect(FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> am!!.playSoundEffect(FX_KEYPRESS_DELETE)
            else -> am!!.playSoundEffect(FX_KEYPRESS_STANDARD)
        }
    }
}