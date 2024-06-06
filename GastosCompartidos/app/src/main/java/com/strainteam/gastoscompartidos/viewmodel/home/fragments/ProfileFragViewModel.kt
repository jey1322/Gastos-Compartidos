package com.strainteam.gastoscompartidos.viewmodel.home.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.preferens.SessionManager
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class ProfileFragViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    val closeSession = SingleLiveEvent<Boolean>()
    val nameUser = SingleLiveEvent<String>()
    val emailUser = SingleLiveEvent<String>()
    val availableUser = SingleLiveEvent<Boolean>()
    val messageToast = SingleLiveEvent<String>()
    val progress = SingleLiveEvent<Boolean>()
    val openEmail = SingleLiveEvent<Boolean>()
    val enableClick = SingleLiveEvent<Boolean>()
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var sessionManager: SessionManager

    fun onCreate(){
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbReference = database.reference.child("User")
        sessionManager = SessionManager(context)

        getNameUserFirebase()
    }

    fun closeSession(){
        sessionManager.deleteSession()
        closeSession.value = true
    }

    fun getNameUserFirebase(){
        val user = auth.currentUser
        dbReference.child(user!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                progress.value = false
                nameUser.value = it.result!!.child("Name").value.toString()
                emailUser.value = it.result!!.child("Email").value.toString()
                availableUser.value = it.result!!.child("Disponible").value as Boolean
            }else{
                progress.value = false
                messageToast.value = "Error al obtener los datos del usuario: ${it.exception?.message}"
            }
        }
    }

    fun changeAvailableUser(valor : Boolean){
        val user = auth.currentUser
        dbReference.child(user!!.uid).child("Disponible").setValue(valor).addOnCompleteListener {
            if(it.isSuccessful){
                progress.value = false
                availableUser.value = valor
                enableClick.value = true
            }else{
                enableClick.value = true
                progress.value = false
                messageToast.value = "Error al cambiar la disponibilidad: ${it.exception?.message}"
            }
        }
    }

    fun RestablecerContrasena(){
        val user = auth.currentUser
        auth.sendPasswordResetEmail(user!!.email.toString()).addOnCompleteListener {
            if(it.isSuccessful){
                openEmail.value = true
                progress.value = false
                enableClick.value = true
            }else{
                enableClick.value = true
                progress.value = false
                messageToast.value = "Error al enviar el correo: ${it.exception?.message}"
            }
        }
    }

    fun deleteUserFirebase(){
        val user = auth.currentUser
        user!!.delete().addOnCompleteListener {
            if(it.isSuccessful){
                dbReference.child(user.uid).removeValue()
                sessionManager.deleteSession()
                closeSession.value = true
            }else{
                enableClick.value = true
                progress.value = false
                messageToast.value = "Error al eliminar la cuenta: ${it.exception?.message}"
            }
        }
    }

}