package com.example.equipo5cuidados.model

import kotlin.math.max

data class Inventario(
    val cantidadDisponible: Double,
    val umbralDosis: Int
) {
    init {
        require(umbralDosis == 3 || umbralDosis == 5) {
            "El umbral de dosis solo puede ser 3 o 5"
        }
        require(cantidadDisponible >= 0.0) {
            "La cantidad disponible no puede ser negativa"
        }
    }

    fun dosisRestantes(cantidadPorToma: Double): Int {
        if (cantidadPorToma <= 0.0) return 0
        return (cantidadDisponible / cantidadPorToma).toInt()
    }

    fun estaBajo(cantidadPorToma: Double): Boolean {
        return dosisRestantes(cantidadPorToma) <= umbralDosis
    }

    fun descontar(cantidad: Double): Inventario {
        val nuevaCantidad = max(0.0, cantidadDisponible - cantidad)
        return copy(cantidadDisponible = nuevaCantidad)
    }

    fun reponer(cantidad: Double): Inventario {
        val nuevaCantidad = cantidadDisponible + max(0.0, cantidad)
        return copy(cantidadDisponible = nuevaCantidad)
    }
}