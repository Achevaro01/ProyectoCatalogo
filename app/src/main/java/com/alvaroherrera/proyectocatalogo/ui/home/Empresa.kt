package com.alvaroherrera.proyectocatalogo.ui.home

@Empresa.Parcelize
data class Empresa(
    val nombre: String = "",
    val telefono: String = ""
) {
    annotation class Parcelize

}


