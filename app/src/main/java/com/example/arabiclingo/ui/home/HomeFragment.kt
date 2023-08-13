package com.example.arabiclingo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.arabiclingo.R
import com.example.arabiclingo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.welcome.text = getString(R.string.welcome_message)

        binding.textHome.text = getString(R.string.learn_tab_instructions)
        binding.instructionsContent.text = getString(R.string.camera_tab_instructions)

        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.welcome.startAnimation(fadeInAnimation)
        binding.textHome.startAnimation(fadeInAnimation)
        binding.instructionsContent.startAnimation(fadeInAnimation)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}