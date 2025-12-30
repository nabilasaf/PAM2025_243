package com.example.simados_tu.view.uicontroller

import HalamanTambah
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.simados_tu.view.HalamanDetail
import com.example.simados_tu.view.HalamanHome
import com.example.simados_tu.view.HalamanLogin
import com.example.simados_tu.view.HalamanUpdate
import com.example.simados_tu.view.route.DestinasiHome
import com.example.simados_tu.view.route.DestinasiLogin

@Composable
fun PetaNavigasi(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiLogin.route
    ) {
        composable(DestinasiLogin.route) {
            HalamanLogin(
                onLoginSuccess = {
                    // Pindah ke Home tanpa perlu bawa-bawa string token
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                }
            )
        }
        composable(DestinasiHome.route) {
            HalamanHome(
                onLogoutSuccess = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(0) // Bersihkan semua backstack agar tidak bisa "back" ke dashboard
                    }
                },
                onTambahClick = {
                    navController.navigate("tambah")
                },
                onDetailClick = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }
        //Rute Halaman Tambah Data
        composable(route = "tambah") {
            HalamanTambah(
                onNavigateBack = {
                    navController.popBackStack() // Kembali ke dashboard
                }
            )
        }

        composable(
            route = "detail/{idMaster}", // Sesuai dengan key di SavedStateHandle
            arguments = listOf(navArgument("idMaster") { type = NavType.IntType })
        ) {
            HalamanDetail(
                onNavigateBack = { navController.popBackStack() },
                onUpdateClick = { id ->
                    navController.navigate("update/$id") //Navigasi ke halaman Update
                }
            )
        }
        composable(
            route = "update/{idMaster}",
            arguments = listOf(navArgument("idMaster") { type = NavType.IntType })
        ) {
            HalamanUpdate(
                onNavigateBack = { navController.popBackStack() },
                onUpdateSuccess = {
                    navController.navigate("home") { // REQ-UPDATE-07
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }

}