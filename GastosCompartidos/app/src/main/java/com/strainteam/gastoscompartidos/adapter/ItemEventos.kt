package com.strainteam.gastoscompartidos.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.strainteam.gastoscompartidos.databinding.ItemEventosBinding
import com.strainteam.gastoscompartidos.model.Eventos
import com.strainteam.gastoscompartidos.model.User

class ItemEventos(private val context: Context, private val mList: MutableList<Eventos>): RecyclerView.Adapter<ItemEventos.ViewHolder>() {

    inner class ViewHolder(val binding: ItemEventosBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(evento: Eventos){
            binding.tvEvento.text = evento.evento
            binding.tvFecha.text = evento.fecha
            binding.tvOrganizador.text = "Organizador: "+evento.organizadorName
            binding.tvBanco.text = "Banco: "+evento.bancoOrganizador
            binding.tvTipoEventoVal.text = evento.tipoEvento
            binding.tvTipoCuotaVal.text = evento.tipoCuota
            binding.tvParticipantes.text = "Participantes: "+evento.participantes.size.toString()+" (Ver Participantes)"

            binding.tvCopy.setOnClickListener {
                if(evento.bancoOrganizador.isEmpty()){
                    Toast.makeText(context, "No hay datos para copiar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }else{
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Banco del Organizador", evento.bancoOrganizador)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "cuenta copiada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEventosBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    fun updateData(newUsers: List<Eventos>) {
        mList.clear()
        mList.addAll(newUsers)
        notifyDataSetChanged()
    }
}