package com.strainteam.gastoscompartidos.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strainteam.gastoscompartidos.databinding.ActivityMainBinding
import com.strainteam.gastoscompartidos.databinding.DialogCreateAccountBinding
import com.strainteam.gastoscompartidos.viewmodel.login.MainViewModel
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.view.home.Home

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.onCreate()

        //observadores
        viewModel.showDialogEvent.observe(this, Observer {
            val dialog = MaterialAlertDialogBuilder(this)
            val bindingDialog = DialogCreateAccountBinding.inflate(layoutInflater)
            dialog.setView(bindingDialog.root)
            dialog.setBackground(resources.getDrawable(R.drawable.button_cancel))
            dialog.setCancelable(false)
            val builder = dialog.create()
            builder.show()
            bindingDialog.btnCancel.setOnClickListener { builder.dismiss() }
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
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        })

        viewModel.errorSigIn.observe(this, Observer { error ->
            Toast.makeText(this,error,Toast.LENGTH_LONG).show()
            binding.btnEntrar.isEnabled = true
            binding.btnEntrar.text = "Entrar"
            binding.btnEntrar.setBackgroundResource(R.drawable.button)
            binding.progressBar.visibility = View.GONE
        })

        viewModel.openEmail.observe(this, Observer {
            val dialog = MaterialAlertDialogBuilder(this)
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
                    Toast.makeText(this,"No se encontró una aplicación de correo electrónico",Toast.LENGTH_LONG).show()
                }
            }
            dialog.setNegativeButton("Ahora no"){_,_ ->}
            dialog.show()
        })

        viewModel.startActivityHome.observe(this, Observer {
            startActivity(Intent(this, Home::class.java))
            finish()
        })

        //Eventos a click
        binding.tvCreateAccount.setOnClickListener {
            viewModel.createAccount()
        }

        binding.btnEntrar.setOnClickListener {
            if(binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                binding.btnEntrar.isEnabled = false
                binding.btnEntrar.text = "Cargando..."
                binding.progressBar.visibility = View.VISIBLE
                viewModel.sigInFirebase(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }else{
                Toast.makeText(this,"Llena todos los campos",Toast.LENGTH_LONG).show()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            if(binding.etEmail.text.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                viewModel.forgotPasswordFirebase(binding.etEmail.text.toString())
            }else{
                binding.etEmail.error = "Ingresa tu correo"
                binding.etEmail.requestFocus()
            }
        }

    }
}