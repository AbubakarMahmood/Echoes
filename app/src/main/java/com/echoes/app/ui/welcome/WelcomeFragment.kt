package com.echoes.app.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.echoes.app.data.auth.AuthRepository
import com.google.android.material.button.MaterialButton

class WelcomeFragment : Fragment() {

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_welcome, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository(requireContext())

        view.findViewById<MaterialButton>(R.id.createTextCapsuleButton).setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_createTextCapsuleFragment)
        }

        view.findViewById<MaterialButton>(R.id.viewArchiveButton).setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_archiveFragment)
        }

        view.findViewById<MaterialButton>(R.id.viewDiscoveryButton).setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_discoveryFragment)
        }

        view.findViewById<MaterialButton>(R.id.viewInsightsButton).setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_insightsFragment)
        }

        view.findViewById<MaterialButton>(R.id.signOutButton).setOnClickListener {
            authRepository.signOut()
            findNavController().navigate(R.id.action_welcomeFragment_to_authFragment)
        }
    }
}
