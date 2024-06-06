package com.strainteam.gastoscompartidos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strainteam.gastoscompartidos.databinding.ItemParticipantesBinding
import com.strainteam.gastoscompartidos.model.Eventos
import com.strainteam.gastoscompartidos.viewmodel.participantes.ParticipantesViewModel

class ItemParticipantes(private val context: Context, private var mParticipantes: MutableList<Eventos.Participantes>, private val isOrganizador: Boolean, private val tipoCuota: String, private val idEvento: String, private val viewModel: ParticipantesViewModel): RecyclerView.Adapter<ItemParticipantes.ViewHolder>() {

    inner class ViewHolder(val binding : ItemParticipantesBinding, private val viewModel: ParticipantesViewModel): RecyclerView.ViewHolder(binding.root){
        fun bind(participantes: Eventos.Participantes) {
            binding.tvEmail.text = participantes.email
            binding.tvName.text = participantes.name
            binding.tvCuotaVal.text = participantes.totalDepositar.toString()
            binding.tvEstadoVal.text = if (participantes.pagado) "Pagada" else "Sin pagar"
            binding.tvAddCuota.visibility = if (isOrganizador && tipoCuota == "Cuota Variable") View.VISIBLE else View.GONE
            binding.tvDelete.visibility = if (isOrganizador) View.VISIBLE else View.GONE
            binding.tvDelete.setOnClickListener {
                viewModel.showDialogDeleteParticipante(participantes.id)
            }
            binding.tvAddCuota.setOnClickListener {
                viewModel.showDialogAddCuota(participantes.id)
            }
            binding.tvPedido.setOnClickListener {
                viewModel.getPedidoParticipante(idEvento, participantes.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return mParticipantes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemParticipantesBinding.inflate(LayoutInflater.from(context), parent, false),viewModel )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mParticipantes[position])
    }

    fun updateData(newParticipantes: List<Eventos.Participantes>){
        mParticipantes.clear()
        mParticipantes.addAll(newParticipantes)
        notifyDataSetChanged()
    }
}