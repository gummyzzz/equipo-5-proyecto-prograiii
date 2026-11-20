package com.example.equipo5cuidados.data

import com.example.equipo5cuidados.model.ContactoEmergencia
import com.example.equipo5cuidados.model.Cuidador
import com.example.equipo5cuidados.model.Persona
import com.example.equipo5cuidados.model.PersonaCuidada
import com.example.equipo5cuidados.model.RolCuidador

object DatosPersonas {

    private val contactoLucia = ContactoEmergencia(
        id = 1L,
        nombre = "Lucía Mamani",
        telefono = "72345678",
        parentesco = "Hija",
        esPrincipal = true
    )

    private val contactoCentroSalud = ContactoEmergencia(
        id = 2L,
        nombre = "Centro de salud del barrio",
        telefono = "22412345",
        parentesco = "Centro de salud",
        esPrincipal = false
    )

    private val contactoRosa = ContactoEmergencia(
        id = 3L,
        nombre = "Rosa Mamani",
        telefono = "71234567",
        parentesco = "Esposa",
        esPrincipal = true
    )

    val personas: List<Persona> = listOf(
        Persona(
            id = 1L,
            nombre = "Rosa Mamani",
            telefono = "71234567",
            fotoUri = null,
            cuidador = Cuidador(
                personaId = 1L,
                rol = RolCuidador.COLABORADOR,
                pinHash = "1111".hashCode().toString(),
                activo = true
            ),
            personaCuidada = PersonaCuidada(
                personaId = 1L,
                colorPerfil = "#7E57C2",
                notas = "Hipertensa. Camina con bastón.",
                consentimientoRegistrado = true,
                contactos = listOf(contactoLucia, contactoCentroSalud),
                cuidadoPor = listOf(3L)
            )
        ),
        Persona(
            id = 2L,
            nombre = "Juan Quispe",
            telefono = "73456789",
            fotoUri = null,
            cuidador = null,
            personaCuidada = PersonaCuidada(
                personaId = 2L,
                colorPerfil = "#26A69A",
                notas = "Se recupera de una quemadura en el brazo derecho.",
                consentimientoRegistrado = true,
                contactos = listOf(contactoRosa),
                cuidadoPor = listOf(1L, 3L)
            )
        ),
        Persona(
            id = 3L,
            nombre = "Lucía Mamani",
            telefono = "72345678",
            fotoUri = null,
            cuidador = Cuidador(
                personaId = 3L,
                rol = RolCuidador.ADMINISTRADOR,
                pinHash = "2222".hashCode().toString(),
                activo = true
            ),
            personaCuidada = null
        )
    )

    fun buscar(id: Long): Persona? =
        personas.find { it.id == id }

    fun personasCuidadas(): List<Persona> =
        personas.filter { it.esPersonaCuidada() }

    fun cuidadores(): List<Persona> =
        personas.filter { it.esCuidador() }
}