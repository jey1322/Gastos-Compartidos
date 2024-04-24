package com.strainteam.gastoscompartidos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.strainteam.gastoscompartidos.R
import com.strainteam.gastoscompartidos.databinding.ItemUsersBinding
import com.strainteam.gastoscompartidos.model.User

class ItemUserAdapter(private val context: Context, private val mUser : MutableList<User>): RecyclerView.Adapter<ItemUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUsersBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            binding.tvName.text = user.nombre
            binding.tvEmail.text = user.email
            binding.tvAvailability.text = if(user.disponible){"Disponible"}else{"No Disponible"}

            binding.root.setOnClickListener {
                if(user.select){
                    binding.root.setBackgroundResource(R.drawable.edittext)
                    user.select = false
                }else{
                    binding.root.setBackgroundResource(R.drawable.button_cancel)
                    user.select = true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemUsersBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(mUser[position])
    }

    fun updateData(newUsers: List<User>) {
        mUser.clear()
        mUser.addAll(newUsers)
        notifyDataSetChanged()
    }
}