package com.example.simados_tu.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simados_tu.R
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.viewmodel.DetailUiState
import com.example.simados_tu.viewmodel.DetailViewModel
import com.example.simados_tu.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetail(
    onNavigateBack: () -> Unit,
    onUpdateClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Asisten Dosen", fontWeight = FontWeight.Bold) },
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
        Column(modifier = modifier.padding(innerPadding)) {
            when (uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorResource(id = R.color.green))
                    }
                }
                is DetailUiState.Error -> {
                    // REQ-DETAIL-08: Menampilkan pesan kesalahan jika data gagal dimuat
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = colorResource(R.color.red), textAlign = TextAlign.Center)
                    }
                }
                is DetailUiState.Success -> {
                    DetailContent(
                        data = uiState.data,
                        onUpdateClick = { onUpdateClick(uiState.data.id_master) },
                        onDeleteClick = { viewModel.showDeleteDialog = true }
                    )
                }
            }
        }
    }

    // REQ-DETAIL-07: Pop-up konfirmasi hapus data
    if (viewModel.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = {
                val nama = if (uiState is DetailUiState.Success) uiState.data.nama_lengkap else "data ini"
                Text("Apakah Anda yakin ingin menghapus data $nama?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.hapusData(onSuccess = onNavigateBack) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.red))
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun DetailContent(
    data: MasterResponse,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // REQ-DETAIL-03: Menampilkan seluruh informasi secara lengkap dan jelas
        InfoCard(title = "Data Asisten", items = listOf(
            "NIM" to data.nim,
            "Nama" to data.nama_lengkap,
            "Status" to if (data.status_aktif_asdos == 1) "Aktif" else "Tidak Aktif" // Perbaikan logcat jank
        ))

        InfoCard(title = "Informasi Mata Kuliah", items = listOf(
            "Kode MK" to data.kode_mk,
            "Nama MK" to data.nama_mk,
            "SKS" to "${data.sks}"
        ))

        InfoCard(title = "Dosen Pengampu", items = listOf(
            "NIP/NIK" to data.nip_nik,
            "Nama Dosen" to data.nama_dosen,
            "Jabatan" to (data.jabatan ?: "-")
        ))

        Spacer(modifier = Modifier.height(24.dp))

        // REQ-DETAIL-04 & REQ-DETAIL-05: Tombol Update dan Delete
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onUpdateClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.green))
            ) { Text("Update") }

            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.weight(1f),
                border = BorderStroke(1.dp, colorResource(R.color.red)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.red))
            ) { Text("Delete") }
        }
    }
}

@Composable
fun InfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.green))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            items.forEach { (label, value) ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = "$label:", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Medium)
                    Text(text = value, modifier = Modifier.weight(0.6f))
                }
            }
        }
    }
}