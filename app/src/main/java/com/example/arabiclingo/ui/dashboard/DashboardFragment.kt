package com.example.arabiclingo.ui.dashboard

import PhraseDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.arabiclingo.R
import com.example.arabiclingo.databinding.FragmentDashboardBinding




class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var listView: ListView

    private val phrasess = arrayOf(
        "مرحبًا", "كيف حالك؟", "شكرًا", "من فضلك",
        // Add more phrases here
    )

    private val phrases: Array<String> by lazy {
        resources.getStringArray(R.array.phrases_array)
    }





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listView = root.findViewById(R.id.listView)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, phrases)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedPhrase = phrases[position]
            val dialogFragment = PhraseDialogFragment(selectedPhrase)
            dialogFragment.show(parentFragmentManager, "PhraseDialog")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
