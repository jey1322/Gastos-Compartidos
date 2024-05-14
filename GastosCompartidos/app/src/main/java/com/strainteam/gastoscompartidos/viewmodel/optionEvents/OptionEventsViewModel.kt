package com.strainteam.gastoscompartidos.viewmodel.optionEvents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class OptionEventsViewModel(application: Application): AndroidViewModel(application){
    private val context = getApplication<Application>().applicationContext
    private lateinit var  auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var dbEventoRef : DatabaseReference
    val isOrganizador = SingleLiveEvent<Boolean>()
    val showView = SingleLiveEvent<Boolean>()
    val messageToast = SingleLiveEvent<String>()

    init {
        auth = FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        dbEventoRef= database.reference.child("Eventos")
    }

    fun getIsOrganizadorEvent(idEvent: String){
        dbEventoRef.child(idEvent).child("OrganizadorId").get().addOnCompleteListener {
            if(it.isSuccessful){
                isOrganizador.value = it.result!!.value.toString() == auth.currentUser!!.uid
                showView.value = true
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
                showView.value = false
            }
        }
    }

}