import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class PhraseDialogFragment(private val phrase: String, private val correctAnswer: String) : DialogFragment() {

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
                if (userTranslation.equals(correctAnswer, ignoreCase = true)) {
                    // Set the item color to green in the list view
                    showToast("$userTranslation is Correct!")
                } else {
                    showToast("$userTranslation is Incorrect. The correct answer is: $correctAnswer")

                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    fun EditText.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
