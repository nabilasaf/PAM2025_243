package com.example.simados_tu.view

import CustomInputField
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simados_tu.R
import com.example.simados_tu.viewmodel.UpdateViewModel
import com.example.simados_tu.viewmodel.provider.PenyediaViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanUpdate(
    onNavigateBack: () -> Unit,
    onUpdateSuccess: () -> Unit,
    viewModel: UpdateViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val state = viewModel.uiState
    var showConfirm by remember { mutableStateOf(false) }
    var statusText by remember(state.status_aktif_asdos) {
        mutableStateOf(state.status_aktif_asdos.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Data Asdos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.green),
                    titleContentColor = colorResource(R.color.white),
                    navigationIconContentColor = colorResource(R.color.white)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- SECTION 1: DATA ASISTEN ---
            item { Text("Informasi Asisten", fontWeight = FontWeight.Bold, color = Color.DarkGray) }
            item {
                CustomInputField(
                    label = "NIM",
                    value = state.nim,
                    onValueChange = { input ->
                        // Samakan: Hanya angka & max 15 digit
                        if (input.all { it.isDigit() } && input.length <= 15) {
                            viewModel.updateUiState(state.copy(nim = input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            item {
                CustomInputField(
                    label = "Nama Lengkap Asdos",
                    value = state.nama_lengkap,
                    onValueChange = { input ->
                        // Samakan: Hanya huruf & spasi & max 100
                        if (input.all { it.isLetter() || it.isWhitespace() } && input.length <= 100) {
                            viewModel.updateUiState(state.copy(nama_lengkap = input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            // --- SECTION 2: DATA MATA KULIAH ---
            item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
            item { Text("Informasi Mata Kuliah", fontWeight = FontWeight.Bold, color = Color.DarkGray) }
            item {
                CustomInputField(
                    label = "Kode MK",
                    value = state.kode_mk,
                    onValueChange = { input ->
                        if (input.length <= 10) {
                            viewModel.updateUiState(state.copy(kode_mk = input.uppercase()))
                        }
                    }
                )
            }
            item {
                CustomInputField(
                    label = "Nama Mata Kuliah",
                    value = state.nama_mk,
                    onValueChange = { input ->
                        if (input.all { it.isLetter() }&& input.length <= 100) {
                            viewModel.updateUiState(state.copy(nama_mk = input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
            item {
                CustomInputField(
                    label = "SKS",
                    value = state.sks,
                    onValueChange = { input ->
                        // Hanya angka
                        if (input.all { it.isDigit() } && input.length <= 2) {
                            viewModel.updateUiState(state.copy(sks = input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // --- SECTION 3: DATA DOSEN ---
            item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
            item { Text("Informasi Dosen", fontWeight = FontWeight.Bold, color = Color.DarkGray) }
            item {
                CustomInputField(
                    label = "NIP/NIK",
                    value = state.nip_nik,
                    onValueChange = { input ->
                        // Samakan: Hanya angka & max 20 digit
                        if (input.all { it.isDigit() } && input.length <= 20) {
                            viewModel.updateUiState(state.copy(nip_nik = input))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            item {
                CustomInputField(
                    label = "Nama Dosen Pengampu",
                    value = state.nama_dosen,
                    onValueChange = { input ->
                        if (input.all { it.isLetter() || it.isWhitespace() } && input.length <= 100) {
                            viewModel.updateUiState(state.copy(nama_dosen = input))
                        }
                    }
                )
            }
            item {
                CustomInputField(
                    label = "Jabatan",
                    value = state.jabatan,
                    onValueChange = { input ->
                        if (input.all { it.isLetter() || it.isWhitespace() } && input.length <= 50) {
                            viewModel.updateUiState(state.copy(jabatan = input))
                        }
                    }
                )
            }

            // --- SECTION 4: STATUS (Khusus Update) ---
            item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
            item {
                CustomInputField(
                    label = "Status (1: Aktif, 0: Tidak)",
                    value = statusText, // Gunakan state String lokal agar bisa dihapus
                    onValueChange = { input ->
                        if (input == "" || input == "0" || input == "1") {
                            statusText = input
                            val targetValue = input.toIntOrNull() ?: 0
                            viewModel.updateUiState(state.copy(status_aktif_asdos = targetValue))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Tombol Update (Samakan Style dengan Submit Data)
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Jika valid, Tampilkan pop-up konfirmasi
                        showConfirm = true
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = state.isEntryValid, // Hanya bisa diklik jika semua field valid
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
                ) {
                    Text("Update Data", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // Dialog Konfirmasi (REQ-UPDATE-04)
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Konfirmasi Perubahan", fontWeight = FontWeight.Bold, color = Color.DarkGray) },
            text = { Text("Apakah Anda yakin data yang dimasukkan sudah benar?") },
            confirmButton = {
                Button(onClick = {
                    showConfirm = false
                    viewModel.simpanUpdate(onUpdateSuccess) // Jalankan request PUT ke MySQL
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.green),
                        contentColor = Color.White
                    )
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false },
                    colors = ButtonDefaults.textButtonColors(
                    contentColor = colorResource(id = R.color.red)
                )) { Text("Batal") }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}