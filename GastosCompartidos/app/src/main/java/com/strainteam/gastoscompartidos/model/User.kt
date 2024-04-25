package com.strainteam.gastoscompartidos.model

data class User(
    val id: String,
    val disponible: Boolean,
    val nombre: String,
    val email: String,
    var select : Boolean = false
)
