package com.example.equipo5cuidados.navigation

import com.example.equipo5cuidados.R

/** Pestañas de la app: nombre e ícono de cada una. */
enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOY("Hoy", R.drawable.ic_hoy),
    PLAN("Plan", R.drawable.ic_plan),
    MEDICAMENTOS("Medicamentos", R.drawable.ic_medicamentos),
    PERSONAS("Personas", R.drawable.ic_personas),
}