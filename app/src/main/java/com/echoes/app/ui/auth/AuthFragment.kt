package com.echoes.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.echoes.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var firebaseStatusText: TextView
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var displayNameLayout: TextInputLayout
    private lateinit var displayNameInput: TextInputEditText
    private lateinit var signInButton: MaterialButton
    private lateinit var registerButton: MaterialButton
    private lateinit var continueLocalButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        bindActions()
        collectViewModel()
    }

    private fun bindViews(view: View) {
        firebaseStatusText = view.findViewById(R.id.authFirebaseStatusText)
        emailLayout = view.findViewById(R.id.authEmailLayout)
        emailInput = view.findViewById(R.id.authEmailInput)
        passwordLayout = view.findViewById(R.id.authPasswordLayout)
        passwordInput = view.findViewById(R.id.authPasswordInput)
        displayNameLayout = view.findViewById(R.id.authDisplayNameLayout)
        displayNameInput = view.findViewById(R.id.authDisplayNameInput)
        signInButton = view.findViewById(R.id.authSignInButton)
        registerButton = view.findViewById(R.id.authRegisterButton)
        continueLocalButton = view.findViewById(R.id.authContinueLocalButton)
    }

    private fun bindActions() {
        signInButton.setOnClickListener {
            viewModel.signIn(
                email = emailInput.text?.toString().orEmpty(),
                password = passwordInput.text?.toString().orEmpty()
            )
        }

        registerButton.setOnClickListener {
            viewModel.register(
                email = emailInput.text?.toString().orEmpty(),
                password = passwordInput.text?.toString().orEmpty(),
                displayName = displayNameInput.text?.toString().orEmpty()
            )
        }

        continueLocalButton.setOnClickListener {
            viewModel.continueLocally()
        }
    }

    private fun collectViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        renderState(state)
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        handleEvent(event)
                    }
                }
            }
        }
    }

    private fun renderState(state: AuthUiState) {
        firebaseStatusText.text = getString(
            if (state.isFirebaseConfigured) {
                R.string.auth_firebase_ready
            } else {
                R.string.auth_firebase_missing_config
            }
        )
        emailLayout.error = state.emailErrorResId?.let(::getString)
        passwordLayout.error = state.passwordErrorResId?.let(::getString)
        displayNameLayout.error = state.displayNameErrorResId?.let(::getString)

        signInButton.isEnabled = !state.isLoading
        registerButton.isEnabled = !state.isLoading
        continueLocalButton.isEnabled = !state.isLoading
        signInButton.text = getString(
            if (state.isLoading) R.string.auth_working_button else R.string.auth_sign_in_button
        )
        registerButton.text = getString(
            if (state.isLoading) R.string.auth_working_button else R.string.auth_register_button
        )
    }

    private fun handleEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.NavigateToApp -> {
                findNavController().navigate(R.id.action_authFragment_to_welcomeFragment)
            }

            is AuthEvent.ShowMessage -> {
                Snackbar.make(requireView(), event.messageResId, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
