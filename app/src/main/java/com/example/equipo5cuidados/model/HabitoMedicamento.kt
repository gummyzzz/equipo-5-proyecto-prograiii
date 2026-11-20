package com.example.equipo5cuidados.model

import java.time.LocalDate

data class HabitoMedicamento(
    override val id: Long,
    override val titulo: String,
    override val descripcion: String = "",
    override val fechaInicio: LocalDate,
    override val fechaFin: LocalDate? = null,
    override val activo: Boolean = true,
    override val programacion: Programacion,
    val dosisTexto: String,
    val cantidadPorToma: Double,
    val inventario: Inventario,
    val condiciones: List<CondicionToma> = emptyList(),
    val colorHex: Long? = null
) : Habito() {

    override fun tipo(): TipoHabito = TipoHabito.MEDICAMENTO

    fun dosisRestantes(): Int = inventario.dosisRestantes(cantidadPorToma)

    fun stockBajo(): Boolean = inventario.estaBajo(cantidadPorToma)

    override fun validar(): List<String> {
        val errores = super.validar().toMutableList()
        if (dosisTexto.isBlank()) errores.add("La dosis no puede estar vacía")
        if (cantidadPorToma <= 0.0) errores.add("La cantidad por toma debe ser mayor que 0")
        return errores
    }
}