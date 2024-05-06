package com.strainteam.gastoscompartidos.model

data class Eventos(
    val id: String,
    val evento : String,
    val fecha : String,
    val organizadorEmail : String,
    val organizadorName : String,
    val organizadorId : String,
    val bancoOrganizador : String,
    val cuentaOrganizador : String,
    val tipoCuota: String,
    val tipoEvento: String,
    val participantes: List<Participantes>,
){
    class Participantes(
        val id: String,
        val email: String,
        val name: String,
        val pedido: String,
        val totalDepositar : Int,
        val pagado: Boolean
    )
}
