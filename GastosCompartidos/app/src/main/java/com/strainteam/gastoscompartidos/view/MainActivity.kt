package com.strainteam.gastoscompartidos.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.strainteam.gastoscompartidos.databinding.ActivityMainBinding
import com.strainteam.gastoscompartidos.databinding.DialogCreateAccountBinding
import com.strainteam.gastoscompartidos.viewmodel.MainViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //observadores
        viewModel.showDialogEvent.observe(this, Observer {
            val dialog = MaterialAlertDialogBuilder(this)
            val bindingDialog = DialogCreateAccountBinding.inflate(layoutInflater)
            dialog.setView(bindingDialog.root)
            val builder = dialog.create()
            builder.show()
            bindingDialog.btnCancel.setOnClickListener {
                builder.dismiss()
            }
            bindingDialog.btnCreateAccount.setOnClickListener {
                if(bindingDialog.etUserName.text.isNotEmpty() && bindingDialog.etPassword.text.isNotEmpty() && bindingDialog.etEmail.text.isNotEmpty()){
                    viewModel.createAccountFirebase(bindingDialog.etEmail.text.toString(),bindingDialog.etPassword.text.toString(),bindingDialog.etUserName.text.toString())
                    builder.dismiss()
                }else{
                    Toast.makeText(this,"Llena todos los campos",Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.messageToast.observe(this, Observer{message ->
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        })

        viewModel.errorSigIn.observe(this, Observer { error ->
            Toast.makeText(this,error,Toast.LENGTH_LONG).show()
            binding.btnEntrar.isEnabled = true
            binding.btnEntrar.text = "Entrar"
            binding.btnEntrar.setBackgroundResource(R.drawable.button)
        })

        //Eventos a click
        binding.tvCreateAccount.setOnClickListener {
            viewModel.createAccount()
        }

        binding.btnEntrar.setOnClickListener {
            if(binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                binding.btnEntrar.isEnabled = false
                binding.btnEntrar.text = "Cargando..."
                viewModel.sigInFirebase(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }else{
                Toast.makeText(this,"Llena todos los campos",Toast.LENGTH_LONG).show()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            if(binding.etEmail.text.isNotEmpty()) {
                viewModel.forgotPasswordFirebase(binding.etEmail.text.toString())
            }else{
                binding.etEmail.error = "Ingresa tu correo"
                binding.etEmail.requestFocus()
            }
        }

    }
}