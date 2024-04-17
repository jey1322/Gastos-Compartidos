package com.strainteam.gastoscompartidos.view.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.strainteam.gastoscompartidos.databinding.FragmentProfileBinding
import com.strainteam.gastoscompartidos.view.login.MainActivity
import com.strainteam.gastoscompartidos.viewmodel.home.fragments.ProfileFragViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileFragViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()

        //observadores
        viewModel.closeSession.observe(viewLifecycleOwner) {
            if (it) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        viewModel.nameUser.observe(viewLifecycleOwner) {
            binding.tvName.text = it
        }

        viewModel.emailUser.observe(viewLifecycleOwner) {
            binding.tvEmail.text = it
        }

        viewModel.availableUser.observe(viewLifecycleOwner) {
            binding.swAvailable.isChecked = it
        }

        viewModel.messageToast.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.progress.observe(viewLifecycleOwner) {
            binding.pbProfile.visibility = if (it) View.VISIBLE else View.GONE
        }

        //Eventos a click
        binding.btCloseSession.setOnClickListener {
            viewModel.closeSession()
        }

        binding.swAvailable.setOnCheckedChangeListener { _, isChecked ->
            binding.pbProfile.visibility = View.VISIBLE
            viewModel.changeAvailableUser(isChecked)
        }

    }

}