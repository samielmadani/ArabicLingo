package com.example.arabiclingo.ui.dashboard

import PhraseAdapter
import PhraseDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arabiclingo.R
import com.example.arabiclingo.databinding.FragmentDashboardBinding




class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var listView: ListView


    private val phrases: Array<String> by lazy {
        resources.getStringArray(R.array.phrases_array)
    }

    private val answers: Array<String> by lazy {
        resources.getStringArray(R.array.answers_array)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PhraseAdapter(phrases, answers)
        recyclerView.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.recyclerView.startAnimation(fadeInAnimation)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
