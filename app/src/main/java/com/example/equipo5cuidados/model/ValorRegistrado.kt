package com.example.equipo5cuidados.model

/**
 * Dato anotado en una medición o un seguimiento.
 * Solo se usa el campo que corresponde al tipo de hábito (una escala se guarda como número).
 *
 * Nota para el equipo: en el diagrama pertenece al área 3, pero lo sube el área 2
 * junto con Habito, porque Habito.aceptaValor() lo necesita para compilar.
 */
data class ValorRegistrado(
    val numero: Double? = null,
    val siNo: Boolean? = null,
    val texto: String? = null
)
