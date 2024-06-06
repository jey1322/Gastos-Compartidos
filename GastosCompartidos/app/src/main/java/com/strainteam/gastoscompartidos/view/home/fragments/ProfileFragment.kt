package com.strainteam.gastoscompartidos.view.home.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.FragmentProfileBinding
import com.strainteam.gastoscompartidos.view.login.MainActivity
import com.strainteam.gastoscompartidos.viewmodel.home.fragments.ProfileFragViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileFragViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onCreate()

        //observadores
        viewModel.closeSession.observe(viewLifecycleOwner, Observer {
            if (it) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        })

        viewModel.nameUser.observe(viewLifecycleOwner, Observer {
            binding.tvName.text = it
        })

        viewModel.emailUser.observe(viewLifecycleOwner, Observer {
            binding.tvEmail.text = it
        })

        viewModel.availableUser.observe(viewLifecycleOwner, Observer {
            binding.swAvailable.isChecked = it
        })

        viewModel.messageToast.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer {
            binding.pbProfile.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.openEmail.observe(viewLifecycleOwner, Observer {
            val dialog = MaterialAlertDialogBuilder(requireContext())
            dialog.setTitle("Verifica tu correo electrónico")
            dialog.setBackground(resources.getDrawable(R.drawable.button_cancel))
            dialog.setMessage("Para usar la aplicación es necesario verificar tu correo electrónico.\n¿Deseas abrir tu aplicación de correo electrónico?")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Abrir Correo"){_,_ ->
                try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }catch (e: Exception){
                    Toast.makeText(requireContext(),"No se encontró una aplicación de correo electrónico",Toast.LENGTH_LONG).show()
                }
            }
            dialog.setNegativeButton("Ahora no"){_,_ ->}
            dialog.show()
        })

        viewModel.enableClick.observe(viewLifecycleOwner, Observer {
            binding.swAvailable.isEnabled = it
            binding.tvEmailContact.isEnabled = it
            binding.tvForgotPassword.isEnabled = it
            binding.tvDeleteUser.isEnabled = it
        })

        //Eventos a click
        binding.btCloseSession.setOnClickListener {
            viewModel.closeSession()
        }

        binding.swAvailable.setOnCheckedChangeListener { _, isChecked ->
            binding.pbProfile.visibility = View.VISIBLE
            disableClick()
            viewModel.changeAvailableUser(isChecked)
        }

        binding.tvForgotPassword.setOnClickListener {
            binding.pbProfile.visibility = View.VISIBLE
            disableClick()
            viewModel.RestablecerContrasena()
        }
        
        binding.tvEmailContact.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
        }
        
        binding.tvDeleteUser.setOnClickListener { 
            val dialog = MaterialAlertDialogBuilder(requireContext())
            dialog.setTitle("Eliminar cuenta")
            dialog.setBackground(resources.getDrawable(R.drawable.button_cancel))
            dialog.setMessage("¿Estás seguro de que deseas eliminar tu cuenta?")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Eliminar") { _, _ ->
                binding.pbProfile.visibility = View.VISIBLE
                disableClick()
                viewModel.deleteUserFirebase()
            }
            dialog.setNegativeButton("Cancelar") { _, _ ->}
            dialog.show()
        }

    }

    private fun disableClick(){
        binding.swAvailable.isEnabled = false
        binding.tvEmailContact.isEnabled = false
        binding.tvForgotPassword.isEnabled = false
        binding.tvDeleteUser.isEnabled = false
    }

}