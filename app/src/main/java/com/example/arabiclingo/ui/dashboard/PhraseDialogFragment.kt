import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class PhraseDialogFragment(private val phrase: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputEditText = EditText(requireContext())
        inputEditText.requestFocus()
        inputEditText.showKeyboard()


        return AlertDialog.Builder(requireContext())
            .setTitle("Translate Phrase")
            .setMessage("Translate the phrase: $phrase")
            .setView(inputEditText)
            .setPositiveButton("Submit") { _, _ ->
                val userTranslation = inputEditText.text.toString()
                // Compare userTranslation with correct translation and display appropriate result
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    fun EditText.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

}
