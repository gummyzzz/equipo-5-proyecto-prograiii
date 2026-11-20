package com.example.equipo5cuidados.model

data class Cuidador(
    val personaId: Long,
    val rol: RolCuidador,
    val pinHash: String,
    val activo: Boolean
) {
    fun verificarPin(pin: String): Boolean =
        pin.hashCode().toString() == pinHash

    fun puedeEditarPlan(): Boolean =
        activo && rol == RolCuidador.ADMINISTRADOR
}