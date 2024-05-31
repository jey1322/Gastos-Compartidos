package com.strainteam.gastoscompartidos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.strainteam.gastoscompartidos.databinding.ItemParticipantesBinding
import com.strainteam.gastoscompartidos.model.Eventos

class ItemParticipantes(private val context: Context, private var mParticipantes: MutableList<Eventos.Participantes>, private val isOrganizador: Boolean, private val tipoCuota: String): RecyclerView.Adapter<ItemParticipantes.ViewHolder>() {

    inner class ViewHolder(val binding : ItemParticipantesBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(participantes: Eventos.Participantes) {
            binding.tvEmail.text = participantes.email
            binding.tvName.text = participantes.name
            binding.tvCuotaVal.text = participantes.totalDepositar.toString()
            binding.tvEstadoVal.text = if (participantes.pagado) "Pagada" else "Sin pagar"
            binding.tvAddCuota.visibility = if (isOrganizador && tipoCuota == "Cuota Variable") View.VISIBLE else View.GONE
            binding.tvDelete.visibility = if (isOrganizador) View.VISIBLE else View.GONE
            binding.tvDelete.setOnClickListener {
                Toast.makeText(context, "Eliminar", Toast.LENGTH_SHORT).show()
            }
            binding.tvAddCuota.setOnClickListener {
                Toast.makeText(context, "Agregar cuota", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return mParticipantes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemParticipantesBinding.inflate(LayoutInflater.from(context), parent, false))
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