package com.alvaroherrera.proyectocatalogo.ui.notifications

import java.io.Serializable

data class UsuarioModel(var direccion: String = "",
                        var edad: String = "",
                        var nombre: String = "",
                        var provider: String = "",
                        var email: String = "",
                        var telefono: String  = "",
                        var empresas:  ArrayList<EmpresaModel> = arrayListOf()): Serializable
