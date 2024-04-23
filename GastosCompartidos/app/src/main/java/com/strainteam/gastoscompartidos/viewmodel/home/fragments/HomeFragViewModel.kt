package com.strainteam.gastoscompartidos.viewmodel.home.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private lateinit var auth: FirebaseAuth
    private lateinit var dbMotivoRef: DatabaseReference
    private lateinit var dbCuotaRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    val motivoList = MutableLiveData<ArrayList<String>>()
    val cuotaList = MutableLiveData<ArrayList<String>>()
    val messageToast = MutableLiveData<String>()
    val showDialogEvent = MutableLiveData<Boolean>()

    init {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbMotivoRef = database.reference.child("MotivosEventos")
        dbCuotaRef = database.reference.child("TipoCuota")
    }

    fun getMotivosEventos(){
        dbMotivoRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                val motivosList = ArrayList<String>()
                for (motivo in it.result!!.children){
                    motivosList.add("${motivo.key} - ${motivo.value}")
                }
                motivoList.value = motivosList
                showDialogEvent.value = true
            }else{
                messageToast.value = "Error motivos de eventos: ${it.exception?.message}"
            }
        }
    }

    fun getTipoCuota(){
        dbCuotaRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                val cuotasList = ArrayList<String>()
                for (cuota in it.result!!.children){
                    cuotasList.add("${cuota.key} - ${cuota.value}")
                }
                cuotaList.value = cuotasList
                getMotivosEventos()
            }else{
                messageToast.value = "Error tipo de cuota: ${it.exception?.message}"
            }
        }
    }

}