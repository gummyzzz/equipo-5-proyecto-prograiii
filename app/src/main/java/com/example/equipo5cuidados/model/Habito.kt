package com.example.equipo5cuidados.model

import java.time.LocalDate

/*
 * Área 2 · Plan de hábitos
 * En el diagrama, Habito es una clase ABSTRACTA. En Kotlin se implementa como
 * sealed class: es abstracta y además cerrada (solo existen los tipos declarados
 * en este mismo paquete). Así, un `when (habito)` sabe qué casos existen.
 *
 * HabitoMedicamento también hereda de Habito, pero lo implementa el área 3
 * en su propio archivo (model/HabitoMedicamento.kt).
 */

/** Tipo de cada hábito. */
enum class TipoHabito { MEDICAMENTO, RUTINA, MEDICION, SEGUIMIENTO }

/** Cómo se responde un seguimiento. */
enum class FormatoEntrada { ESCALA, SI_NO, NUMERO, TEXTO }

sealed class Habito {
    abstract val id: Long
    abstract val titulo: String
    abstract val descripcion: String
    abstract val fechaInicio: LocalDate
    abstract val fechaFin: LocalDate?
    abstract val activo: Boolean
    abstract val programacion: Programacion

    /** Cada subclase dice de qué tipo es (método abstracto). */
    abstract fun tipo(): TipoHabito

    /** Un hábito está vigente si está activo y la fecha cae entre su inicio y su fin. */
    fun estaVigente(fecha: LocalDate): Boolean {
        val fin = fechaFin
        return activo && !fecha.isBefore(fechaInicio) && (fin == null || !fecha.isAfter(fin))
    }

    /** Devuelve TODOS los errores juntos, para mostrarlos en un formulario. */
    open fun validar(): List<String> {
        val errores = mutableListOf<String>()
        if (titulo.isBlank()) errores.add("El título no puede estar vacío")
        val fin = fechaFin
        if (fin != null && fin.isBefore(fechaInicio)) errores.add("La fecha de fin no puede ser anterior al inicio")
        if (programacion.horas.isEmpty()) errores.add("Debe tener al menos una hora")
        return errores
    }

    /**
     * Polimorfismo: cada tipo de hábito responde a su manera si acepta un valor.
     * Por defecto (medicamento, rutina) no se espera ningún valor.
     */
    open fun aceptaValor(v: ValorRegistrado?): Boolean = v == null
}

/** Tarea de cuidado que no es un medicamento: curación, cambio de posición, caminata… */
data class HabitoRutina(
    override val id: Long,
    override val titulo: String,
    override val descripcion: String,
    override val fechaInicio: LocalDate,
    override val fechaFin: LocalDate? = null,
    override val activo: Boolean = true,
    override val programacion: Programacion,
    val categoria: String,
    val pasos: List<PasoRutina> = emptyList()
) : Habito() {
    override fun tipo() = TipoHabito.RUTINA

    fun pasosOrdenados(): List<PasoRutina> = pasos.sortedBy { it.orden }
}

/**
 * Medición con un equipo de la familia (presión, temperatura, glucosa…).
 * minimoValido y maximoValido son el rango de valores POSIBLES (para atrapar errores
 * de tipeo, como 375 en vez de 37,5). No son valores clínicos.
 */
data class HabitoMedicion(
    override val id: Long,
    override val titulo: String,
    override val descripcion: String,
    override val fechaInicio: LocalDate,
    override val fechaFin: LocalDate? = null,
    override val activo: Boolean = true,
    override val programacion: Programacion,
    val tipoMedicion: String,
    val unidad: String,
    val minimoValido: Double,
    val maximoValido: Double
) : Habito() {
    override fun tipo() = TipoHabito.MEDICION

    override fun aceptaValor(v: ValorRegistrado?): Boolean {
        val numero = v?.numero ?: return false
        return numero in minimoValido..maximoValido
    }
}

/** Algo que el cuidador decide seguir: dolor, ánimo, apetito… */
data class HabitoSeguimiento(
    override val id: Long,
    override val titulo: String,
    override val descripcion: String,
    override val fechaInicio: LocalDate,
    override val fechaFin: LocalDate? = null,
    override val activo: Boolean = true,
    override val programacion: Programacion,
    val pregunta: String,
    val formato: FormatoEntrada,
    val escalaMin: Int? = null,
    val escalaMax: Int? = null
) : Habito() {
    override fun tipo() = TipoHabito.SEGUIMIENTO

    override fun aceptaValor(v: ValorRegistrado?): Boolean {
        if (v == null) return false
        return when (formato) {
            FormatoEntrada.ESCALA -> {
                val numero = v.numero ?: return false
                numero >= (escalaMin ?: 0) && numero <= (escalaMax ?: 10)
            }
            FormatoEntrada.SI_NO -> v.siNo != null
            FormatoEntrada.NUMERO -> v.numero != null
            FormatoEntrada.TEXTO -> !v.texto.isNullOrBlank()
        }
    }
}
