package com.example.simados_tu.view.route

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

object DestinasiLogin : DestinasiNavigasi {
    override val route = "login"
    override val titleRes = "Login Staff"
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Dashboard Master"
}