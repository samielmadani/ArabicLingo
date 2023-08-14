import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.arabiclingo.R

class PhraseDialogFragment(private val phrase: String, private val correctAnswer: String, private val phrases: Array<String>, private val callback: (Boolean) -> Unit) : DialogFragment() {

    constructor() : this("", "", emptyArray(), { _ -> }) {
        // Empty constructor required by Android's FragmentManager
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_phrase, null)
        val inputEditText = view.findViewById<EditText>(R.id.inputEditText)
        inputEditText.requestFocus()
        inputEditText.showKeyboard()

        val textPhrase = view.findViewById<TextView>(R.id.textPhrase)
        val checkBoxShowEnglish = view.findViewById<CheckBox>(R.id.checkBoxShowEnglish)

        textPhrase.text = phrase

        checkBoxShowEnglish.setOnCheckedChangeListener { _, isChecked ->
            val transliterations = resources.getStringArray(R.array.transliterations_array)
            if (isChecked) {
                val phraseIndex = phrases.indexOf(phrase)
                if (phraseIndex != -1 && phraseIndex < transliterations.size) {
                    textPhrase.text = transliterations[phraseIndex]
                }
            } else {
                textPhrase.text = phrase
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Translate the Phrase:")
            .setView(view)
            .setPositiveButton("Submit") { _, _ ->
                val userTranslation = inputEditText.text.toString()
                val isCorrect = userTranslation.equals(correctAnswer, ignoreCase = true)
                if (isCorrect) {
                    callback(true)
                    showToast("$userTranslation is Correct!")
                } else {
                    callback(false)
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
