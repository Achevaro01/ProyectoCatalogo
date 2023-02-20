package com.alvaroherrera.proyectocatalogo.ui.notifications

data class EmpresaModel(var cif: String = "",
                        var nombre: String = "",
                        var calle: String = "",
                        var telefono: String = "",
                        var usuarioLogeado: UsuarioModel? = null)
