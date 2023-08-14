import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.arabiclingo.R

class PhraseAdapter(private val phrases: Array<String>, private val answers: Array<String>) :
    RecyclerView.Adapter<PhraseAdapter.PhraseViewHolder>() {

    class PhraseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val phraseTextView: TextView = itemView.findViewById(R.id.textPhrase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phrase, parent, false)
        return PhraseViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val phrase = phrases[position]
        holder.phraseTextView.text = phrase

        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.defaultBackground))

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val correctAnswer = answers[position]
            val dialogFragment = PhraseDialogFragment(phrase, correctAnswer) { isCorrect ->
                if (isCorrect) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.correctBackground))
                } else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.incorrectBackground))
                }
            }
            dialogFragment.show((context as FragmentActivity).supportFragmentManager, "PhraseDialog")
        }
    }


    override fun getItemCount(): Int = phrases.size
}
