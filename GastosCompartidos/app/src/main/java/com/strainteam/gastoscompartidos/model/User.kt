package com.strainteam.gastoscompartidos.model

data class User(
    val data : List<Usuarios>
)
{
    class Usuarios{
        val disponible : Boolean = true
        val nombre : String = ""
        val email : String = ""
    }
}
