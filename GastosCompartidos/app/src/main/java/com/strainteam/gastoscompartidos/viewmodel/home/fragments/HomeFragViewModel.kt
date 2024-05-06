package com.strainteam.gastoscompartidos.viewmodel.home.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.adapter.ItemEventos
import com.strainteam.gastoscompartidos.adapter.ItemUserAdapter
import com.strainteam.gastoscompartidos.model.Eventos
import com.strainteam.gastoscompartidos.model.User
import com.strainteam.gastoscompartidos.model.UserSelect
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class HomeFragViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private lateinit var auth: FirebaseAuth
    private lateinit var dbMotivoRef: DatabaseReference
    private lateinit var dbCuotaRef: DatabaseReference
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var dbEventoRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    val motivoList = SingleLiveEvent<ArrayList<String>>()
    val cuotaList = SingleLiveEvent<ArrayList<String>>()
    val messageToast = SingleLiveEvent<String>()
    val showDialogEvent = SingleLiveEvent<Boolean>()
    val hideProgress = SingleLiveEvent<Boolean>()
    val userList = MutableLiveData<MutableList<User>>()
    val userSelectList = MutableLiveData<MutableList<UserSelect>>()
    val userAdapter = ItemUserAdapter(context, mutableListOf())
    val eventosAdapter = ItemEventos(context, mutableListOf())
    val nameUser = MutableLiveData<String>()
    val eventos = MutableLiveData<MutableList<Eventos>>()
    val showNoEvents = SingleLiveEvent<Boolean>()

    init {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbMotivoRef = database.reference.child("MotivosEventos")
        dbCuotaRef = database.reference.child("TipoCuota")
        dbUserRef = database.reference.child("User")
        dbEventoRef = database.reference.child("Eventos")
        getNameUser()
    }

    fun getMotivosEventos(){
        dbMotivoRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                val motivosList = ArrayList<String>()
                for (motivo in it.result!!.children){
                    motivosList.add(motivo.value.toString())
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
                    cuotasList.add(cuota.value.toString())
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

    fun getNameUser(){
        val user = auth.currentUser
        dbUserRef.child(user!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                nameUser.value = it.result!!.child("Name").value.toString()
            }else{
                messageToast.value = "Error obtener usuario: ${it.exception?.message}"
            }
        }
    }

    fun migrateUserAUserSelect(list: List<User>){
        val userSelectL = ArrayList<UserSelect>()
        for (user in list){
            val userSelect = UserSelect(user.id, user.nombre, user.email, 0,"", false)
            if(!userSelectL.contains(userSelect)){
                userSelectL.add(userSelect)
            }
        }
        userSelectL.add(UserSelect(auth.currentUser?.uid.toString(), nameUser.value.toString(), auth.currentUser?.email.toString(), 0, "", false))
        userSelectList.value = userSelectL
    }

    fun createEventos(Evento: String, Fecha: String, TipoEvento: String, TipoCuota: String){
        val evento = dbEventoRef.push()
        evento.child("Evento").setValue(Evento)
        evento.child("Fecha").setValue(Fecha)
        evento.child("TipoEvento").setValue(TipoEvento)
        evento.child("TipoCuota").setValue(TipoCuota)
        evento.child("OrganizadorId").setValue(auth.currentUser?.uid)
        evento.child("OrganizadorName").setValue(nameUser.value)
        evento.child("OrganizadorEmail").setValue(auth.currentUser?.email)
        evento.child("BancoOrganizador").setValue("")
        evento.child("CuentaOrganizador").setValue("")
        evento.child("Participantes").setValue(userSelectList.value)
        messageToast.value = "Evento creado"
        getEventos()
    }

    fun getEventos(){
        dbEventoRef.get().addOnCompleteListener {
            if(it.isSuccessful){
                val eventosList = ArrayList<Eventos>()
                for (evento in it.result!!.children){
                    val participantesList = ArrayList<Eventos.Participantes>()
                    for (participante in evento.child("Participantes").children){
                        participantesList.add(Eventos.Participantes(participante.child("id").value.toString(), participante.child("email").value.toString(), participante.child("name").value.toString(), participante.child("pedido").value.toString(), participante.child("totalDepositar").value.toString().toInt(), participante.child("pagado").value.toString().toBoolean()))
                    }
                    eventosList.add(Eventos(evento.key.toString(), evento.child("Evento").value.toString(), evento.child("Fecha").value.toString(), evento.child("OrganizadorEmail").value.toString(), evento.child("OrganizadorName").value.toString(), evento.child("OrganizadorId").value.toString(), evento.child("BancoOrganizador").value.toString(), evento.child("CuentaOrganizador").value.toString(), evento.child("TipoCuota").value.toString(), evento.child("TipoEvento").value.toString(), participantesList))
                }
                eventos.value = eventosList
                if(eventosList.isNotEmpty()){
                    eventosAdapter.updateData(eventosList)
                    showNoEvents.value = false
                }else{
                    showNoEvents.value = true
                }
                hideProgress.value = true
            }else{
                hideProgress.value = true
                messageToast.value = "Error obtener eventos: ${it.exception?.message}"
            }
        }
    }

}