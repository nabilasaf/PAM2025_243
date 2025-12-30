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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
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
            item {
                // 1. NIM: HANYA BOLEH ANGKA & MAKSIMAL 15 DIGIT
                CustomInputField(
                    label = "NIM (Wajib)",
                    value = state.nim,
                    onValueChange = { input ->
                        // Filter: Hanya terima jika input baru adalah angka
                        if (input.all { it.isDigit() } && input.length <= 15) {
                            viewModel.updateUiState(state.copy(nim = input))
                        }
                    }
                )

                // 2. NAMA LENGKAP: HANYA HURUF DAN SPASI
                CustomInputField(
                    label = "Nama Lengkap (Wajib)",
                    value = state.nama_lengkap,
                    onValueChange = { input ->
                        // Filter: Mencegah angka atau simbol masuk ke kolom nama
                        if (input.all { it.isLetter() || it.isWhitespace() } && input.length <= 100) {
                            viewModel.updateUiState(state.copy(nama_lengkap = input))
                        }
                    }
                )

                // 3. KODE MK: OTOMATIS HURUF BESAR (UPPERCASE)
                CustomInputField(
                    label = "Kode MK (Wajib)",
                    value = state.kode_mk,
                    onValueChange = { input ->
                        if (input.length <= 10) {
                            viewModel.updateUiState(state.copy(kode_mk = input.uppercase()))
                        }
                    }
                )
                // 4. NAMA MK: HANYA HURUF DAN SPASI
                CustomInputField(
                    label = "Nama MK (Wajib)",
                    value = state.nama_mk,
                    onValueChange = { input ->
                        if (input.length <= 100) {
                            viewModel.updateUiState(state.copy(nama_mk = input))
                        }
                    }
                )

                // 5. SKS: HANYA ANGKA (MAX 2 DIGIT)
                CustomInputField(
                    label = "SKS (Wajib)",
                    value = state.sks,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 2) {
                            viewModel.updateUiState(state.copy(sks = input))
                        }
                    }
                )

                // 6. NIP/NIK: HANYA ANGKA
                CustomInputField(
                    label = "NIP/NIK (Wajib)",
                    value = state.nip_nik,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 20) {
                            viewModel.updateUiState(state.copy(nip_nik = input))
                        }
                    }
                )
                // 7. NAMA DOSEN: HANYA HURUF DAN SPASI
                CustomInputField(
                    label = "Nama Dosen (Wajib)",
                    value = state.nama_dosen,
                    onValueChange = { input ->
                        if (input.length <= 100) {
                            viewModel.updateUiState(state.copy(nama_dosen = input))
                        }
                    }
                )
                // 8. JABATAN: HANYA HURUF DAN SPASI
                CustomInputField(
                    label = "Jabatan",
                    value = state.jabatan,
                    onValueChange = { input ->
                        if (input.length <= 50) {
                            viewModel.updateUiState(state.copy(jabatan = input))
                        }
                    }
                )

                // 9. STATUS: DIPAKSA HANYA BISA ISI 0 ATAU 1
                CustomInputField(
                    label = "Status (1: Aktif, 0: Tidak)",
                    value = statusText,
                    onValueChange = { input ->
                        if (input == "" || input == "0" || input == "1") {
                            statusText = input
                            // Update ke ViewModel hanya jika ada angka, jika kosong default ke 0
                            val targetValue = input.toIntOrNull() ?: 0
                            viewModel.updateUiState(state.copy(status_aktif_asdos = targetValue))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                //Tombol tetap Disabled (mati) jika syarat isEntryValid belum terpenuhi
                Button(
                    onClick = { showConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.isEntryValid,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
                ) {
                    Text("Update Data")
                }

                //Menampilkan pesan error dari server jika gagal
                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = colorResource(R.color.red),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    // Dialog Konfirmasi (REQ-UPDATE-04)
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Konfirmasi Perubahan") },
            text = { Text("Apakah Anda yakin data yang dimasukkan sudah benar?") },
            confirmButton = {
                Button(onClick = {
                    showConfirm = false
                    viewModel.simpanUpdate(onUpdateSuccess) // Jalankan request PUT ke MySQL
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Batal") }
            }
        )
    }
}