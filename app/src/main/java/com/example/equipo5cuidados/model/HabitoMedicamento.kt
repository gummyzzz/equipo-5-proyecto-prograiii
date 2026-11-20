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
    val nombre: String,
    val dosisIndicada: String,
    val cantidadPorToma: Double,
    val unidad: String,
    val colorIdentificacion: String,
    val inventario: Inventario? = null,
    val condiciones: List<CondicionToma> = emptyList()
) : Habito() {

    override fun tipo(): TipoHabito = TipoHabito.MEDICAMENTO

    fun avisosPrevios(): List<CondicionToma> = condiciones

    override fun validar(): List<String> {
        val errores = super.validar().toMutableList()
        if (nombre.isBlank()) errores.add("El nombre del medicamento no puede estar vacío")
        if (dosisIndicada.isBlank()) errores.add("La dosis indicada no puede estar vacía")
        if (cantidadPorToma <= 0.0) errores.add("La cantidad por toma debe ser mayor que 0")
        if (unidad.isBlank()) errores.add("La unidad no puede estar vacía")
        return errores
    }
}