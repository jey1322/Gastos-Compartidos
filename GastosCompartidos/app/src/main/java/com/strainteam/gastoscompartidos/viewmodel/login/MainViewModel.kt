package com.strainteam.gastoscompartidos.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.preferens.SessionManager
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var sessionManager: SessionManager
    val showDialogEvent = SingleLiveEvent<Boolean>()
    val messageToast = SingleLiveEvent<String>()
    val errorSigIn = SingleLiveEvent<String>()
    val openEmail = SingleLiveEvent<Boolean>()
    val startActivityHome = SingleLiveEvent<Boolean>()

    fun onCreate(){
        database = FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        dbReference = database.reference.child("User")
        sessionManager = SessionManager(context)
        try {
            if(sessionManager.fetchUid()!!.isNotEmpty()){startActivityHome.value = true}
        } catch (_: Exception) {}
    }

    fun createAccount(){
        showDialogEvent.call(true)
    }

    fun createAccountFirebase(email: String, password: String, userName: String){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                val user = auth.currentUser
                user!!.sendEmailVerification().addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val userBD = dbReference.child(user.uid.toString())
                        userBD.child("Email").setValue(email)
                        userBD.child("Name").setValue(userName)
                        userBD.child("Disponible").setValue(true)
                        messageToast.value = "Cuenta creada, verifica tu correo electrónico."
                        openEmail.value = true
                    }else{
                        messageToast.value = "Error al verificar la cuenta: ${task.exception?.message}"
                    }
                }
            }else{
                messageToast.value = "Error al crear la cuenta: ${it.exception?.message}"
            }
        }
    }

    fun sigInFirebase(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                when(auth.currentUser?.isEmailVerified){
                    true -> {
                        errorSigIn.value = "Bienvenido"
                        sessionManager.saveEmail(auth.currentUser?.email.toString())
                        sessionManager.saveUid(auth.currentUser?.uid.toString())
                        startActivityHome.value = true
                    } false -> {
                        val user = auth.currentUser
                        user!!.sendEmailVerification().addOnCompleteListener {
                            if(it.isSuccessful){
                                errorSigIn.value = "Es necesario verificar tu correo electrónico."
                                openEmail.value = true
                            }else{
                                errorSigIn.value = "Error al verificar la cuenta: ${it.exception?.message}"
                            }
                        }
                    } null -> TODO()
                }
            }else{
                errorSigIn.value = "Error al iniciar sesión: ${it.exception?.message}"
            }
        }
    }

    fun forgotPasswordFirebase(email: String){
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                messageToast.value = "Se ha enviado un correo a tu cuenta para restablecer tu contraseña."
            }else{
                messageToast.value = "Error al enviar el correo: ${it.exception?.message}"
            }
        }
    }
}
