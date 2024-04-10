package com.strainteam.gastoscompartidos.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel: ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    val showDialogEvent = SingleLiveEvent<Boolean>()
    val messageToast = MutableLiveData<String>()
    val errorSigIn = MutableLiveData<String>()

    init {
        database = FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        dbReference = database.reference.child("User")
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
                    }else{
                        messageToast.value = "Erroral verificar la cuenta: ${task.exception?.message}"
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
                errorSigIn.value = "Bienvenido ${auth.currentUser?.email}"
            }else{
                errorSigIn.value = "Error al iniciar sesión: ${it.exception?.message}"
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