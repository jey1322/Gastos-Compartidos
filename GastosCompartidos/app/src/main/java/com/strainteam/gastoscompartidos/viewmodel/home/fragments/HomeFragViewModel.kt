package com.strainteam.gastoscompartidos.viewmodel.home.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.adapter.ItemUserAdapter
import com.strainteam.gastoscompartidos.model.User
import com.strainteam.gastoscompartidos.model.UserSelect
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class HomeFragViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private lateinit var auth: FirebaseAuth
    private lateinit var dbMotivoRef: DatabaseReference
    private lateinit var dbCuotaRef: DatabaseReference
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    val motivoList = SingleLiveEvent<ArrayList<String>>()
    val cuotaList = SingleLiveEvent<ArrayList<String>>()
    val messageToast = SingleLiveEvent<String>()
    val showDialogEvent = SingleLiveEvent<Boolean>()
    val hideProgress = SingleLiveEvent<Boolean>()
    val userList = MutableLiveData<MutableList<User>>()
    val userSelectList = MutableLiveData<MutableList<UserSelect>>()
    val userAdapter = ItemUserAdapter(context, mutableListOf())

    init {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbMotivoRef = database.reference.child("MotivosEventos")
        dbCuotaRef = database.reference.child("TipoCuota")
        dbUserRef = database.reference.child("User")
    }

    fun getMotivosEventos(){
        dbMotivoRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                val motivosList = ArrayList<String>()
                for (motivo in it.result!!.children){
                    motivosList.add("${motivo.key} - ${motivo.value}")
                }
                motivoList.value = motivosList
                hideProgress.value = true
                showDialogEvent.value = true
            }else{
                hideProgress.value = true
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
                hideProgress.value = true
                messageToast.value = "Error tipo de cuota: ${it.exception?.message}"
            }
        }
    }

    fun getUserExceptMe(){
        dbUserRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                val userList = ArrayList<User>()
                for (user in it.result!!.children){
                    if(user.key != auth.currentUser?.uid){
                        userList.add(User(user.key.toString(), user.child("Disponible").value as Boolean, user.child("Name").value.toString(), user.child("Email").value.toString()))
                    }
                }
                this.userList.value = userList
                userAdapter.updateData(userList)
            }else{
                messageToast.value = "Error obtener usuarios: ${it.exception?.message}"
            }
        }
    }

    fun migrateUserAUserSelect(list: List<User>){
        val userSelectL = ArrayList<UserSelect>()
        for (user in list){
            val userSelect = UserSelect(user.id, user.nombre, user.email)
            if(!userSelectL.contains(userSelect)){
                userSelectL.add(userSelect)
            }
        }
        userSelectList.value = userSelectL
    }

}