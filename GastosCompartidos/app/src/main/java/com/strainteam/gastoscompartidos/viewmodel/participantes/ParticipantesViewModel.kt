package com.strainteam.gastoscompartidos.viewmodel.participantes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.strainteam.gastoscompartidos.adapter.ItemParticipantes
import com.strainteam.gastoscompartidos.adapter.ItemUserAdapter
import com.strainteam.gastoscompartidos.model.Eventos
import com.strainteam.gastoscompartidos.model.User
import com.strainteam.gastoscompartidos.model.UserSelect
import com.strainteam.gastoscompartidos.viewmodel.utils.SingleLiveEvent

class ParticipantesViewModel(application: Application, isOrginizador: Boolean, tipoCuota: String, idEvento: String): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private lateinit var auth: FirebaseAuth
    private lateinit var dbEventoRef : DatabaseReference
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    val participantesAdapter = ItemParticipantes(context, mutableListOf(), isOrginizador, tipoCuota, idEvento, this)
    val hideProgress = SingleLiveEvent<Boolean>()
    val showNoParticipantes = SingleLiveEvent<Boolean>()
    val messageToast = SingleLiveEvent<String>()
    val participantesList = MutableLiveData<MutableList<Eventos.Participantes>>()
    val showDialogDelete = SingleLiveEvent<String>()
    val showDialogCuota = SingleLiveEvent<String>()
    val showDialogPedido = SingleLiveEvent<String>()
    val userList = MutableLiveData<MutableList<User>>()
    val userAdapter = ItemUserAdapter(context, mutableListOf())

    init {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbEventoRef = database.reference.child("Eventos")
        dbUserRef = database.reference.child("User")
    }

    fun getParticipantesEvento(idEvento: String){
        dbEventoRef.child(idEvento).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                val participantesList = mutableListOf<Eventos.Participantes>()
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() != auth.currentUser?.uid){
                        participantesList.add(Eventos.Participantes(
                            participante.child("id").value.toString(),
                            participante.child("email").value.toString(),
                            participante.child("name").value.toString(),
                            participante.child("pedido").value.toString(),
                            participante.child("totalDepositar").value.toString().toInt(),
                            participante.child("pagado").value as Boolean
                        ))
                    }
                }
                if(participantesList.isNotEmpty()) {
                    this.participantesList.value = participantesList
                    participantesAdapter.updateData(participantesList)
                    showNoParticipantes.value = false
                }else{
                    showNoParticipantes.value = true
                }
                hideProgress.value = true
            }else{
                hideProgress.value = true
                messageToast.value = "Error al obtener los participantes: ${it.exception?.message}"
            }
        }
    }

    fun showDialogDeleteParticipante(idParticipante: String){
        showDialogDelete.value = idParticipante
    }

    fun deleteParticipante(idEvento: String, idParticipante: String){
        dbEventoRef.child(idEvento).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == idParticipante){
                        participante.ref.removeValue().addOnCompleteListener {
                            if(it.isSuccessful){
                                messageToast.value = "Participante eliminado"
                                getParticipantesEvento(idEvento)
                            }else{
                                messageToast.value = "Error eliminar: ${it.exception?.message}"
                            }
                        }
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun showDialogAddCuota(idParticipante: String){
        showDialogCuota.value = idParticipante
    }

    fun addtotalDepositarParticipante(idEvento: String, idParticipante: String, totalDepositar: Int){
        dbEventoRef.child(idEvento).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == idParticipante){
                        participante.ref.child("totalDepositar").setValue(totalDepositar).addOnCompleteListener {
                            if(it.isSuccessful){
                                messageToast.value = "Cuota añadida"
                                getParticipantesEvento(idEvento)
                            }else{
                                messageToast.value = "Error al añadir cuota: ${it.exception?.message}"
                            }
                        }
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun showDialogPed(pedido: String){
        showDialogPedido.value = pedido
    }

    fun getPedidoParticipante(idEvento: String, idParticipante: String){
        dbEventoRef.child(idEvento).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for (participante in it.result!!.children){
                    if(participante.child("id").value.toString() == idParticipante){
                        showDialogPed(participante.child("pedido").value.toString())
                    }
                }
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

    fun getUsers(idEvento: String){
        dbEventoRef.child(idEvento).child("Participantes").get().addOnCompleteListener { event ->
            if(event.isSuccessful){
                val userList = mutableSetOf<User>()
                val currentParticipants = event.result!!.children.map { it.child("id").value.toString() }
                dbUserRef.get().addOnCompleteListener { users ->
                    if(users.isSuccessful){
                        for (user in users.result!!.children){
                            if(!currentParticipants.contains(user.key)){
                                val newUser = User(
                                    user.key.toString(),
                                    user.child("Disponible").value as Boolean,
                                    user.child("Name").value.toString(),
                                    user.child("Email").value.toString(),
                                    false
                                )
                                userList.add(newUser)
                            }
                        }
                        this.userList.value = userList.toMutableList()
                        userAdapter.updateData(userList.toList())
                    }else{
                        messageToast.value = "Error participantes: ${users.exception?.message}"
                    }
                }
            }else{
                messageToast.value = "Error usuarios: ${event.exception?.message}"
            }
        }
    }

    fun addParticipantes(idEvent: String, list: List<User>){
        val userSelectL = ArrayList<UserSelect>()
        for(user in list){
            val userSelect = UserSelect(user.id, user.nombre, user.email, 0, "", false)
            if(!userSelectL.contains(userSelect)){
                userSelectL.add(userSelect)
            }
        }
        dbEventoRef.child(idEvent).child("Participantes").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(user in userSelectL){
                    dbEventoRef.child(idEvent).child("Participantes").child(user.id).setValue(user)
                }
                messageToast.value = "Participantes añadidos"
            }else{
                messageToast.value = "Error: ${it.exception?.message}"
            }
        }
    }

}