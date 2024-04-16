package com.strainteam.gastoscompartidos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.preferens.SessionManager
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

    init {
        database = FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        dbReference = database.reference.child("User")
        sessionManager = SessionManager(context)
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
                        errorSigIn.value = "Bienvenido ${auth.currentUser?.email}"
                        sessionManager.saveEmail(auth.currentUser?.email.toString())
                        sessionManager.saveUid(auth.currentUser?.uid.toString())
                    }
                    false -> {
                        errorSigIn.value = "Es necesario verificar tu correo electrónico."
                        openEmail.value = true
                    }
                    null -> TODO()
                }
            }else{
                errorSigIn.value = "Error al iniciar sesión: ${it.exception?.message}"
            }
        }
    }

    fun forgotPasswordFirebase(email: String){
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                messageToast.value = "Se ha enviado un correo para restablecer tu contraseña."
            }else{
                messageToast.value = "Error al enviar el correo: ${it.exception?.message}"
            }
        }
    }
}

class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer<T> { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    fun call(t: T) {
        value = t
    }
}