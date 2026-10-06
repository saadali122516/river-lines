package com.riverlines.game.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.riverlines.game.R
import com.riverlines.game.core.di.ServiceLocator
import com.riverlines.game.core.ui.EcoChallengeAdapter
import com.riverlines.game.databinding.DialogEcoChallengesBinding

class EcoChallengesDialog : DialogFragment() {

    private var _binding: DialogEcoChallengesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ThemeOverlay_RiverLines_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEcoChallengesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val challenges = ServiceLocator.progressRepository().challenges()
        if (challenges.isEmpty()) {
            binding.ecoList.visibility = View.GONE
            binding.ecoEmpty.visibility = View.VISIBLE
        } else {
            binding.ecoList.visibility = View.VISIBLE
            binding.ecoEmpty.visibility = View.GONE
            binding.ecoList.layoutManager = LinearLayoutManager(requireContext())
            binding.ecoList.adapter = EcoChallengeAdapter(challenges)
        }
        binding.ecoClose.setOnClickListener {
            try {
                dismissAllowingStateLoss()
            } catch (e: Exception) {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            val window = dialog?.window ?: return
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.decorView.setPadding(48, 0, 48, 0)
        } catch (e: Exception) {
        }
    }

    override fun onDestroyView() {
        _binding?.ecoList?.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "eco_challenges_dialog"
    }
}
