package com.example.simados_tu.view
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simados_tu.R
import com.example.simados_tu.modeldata.MasterResponse
import com.example.simados_tu.viewmodel.HomeUiState
import com.example.simados_tu.viewmodel.HomeViewModel
import com.example.simados_tu.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onTambahClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DialogAkun(
            nama = viewModel.namaStaff,
            email = viewModel.emailStaff,
            onDismiss = { showDialog = false },
            onLogout = {
                viewModel.logout {
                    showDialog = false
                    onLogoutSuccess()
                }
            }
        )
    }
    LaunchedEffect(Unit) {
        viewModel.getMasterList()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        topBar = {
            TopAppBar(
                title = { Text("SIMADOS-TU", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = colorResource(R.color.black)
                        )
                    }
                }
            )
        },
        //ini untuk tombol navigasi ke add data asdos
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTambahClick,
                containerColor = colorResource(R.color.green),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {Icon(Icons.Default.Add, contentDescription = "Tambah") }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            // Bagian Atas: Search Bar fungsional
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Menggunakan BasicTextField agar desain tetap minimalis sesuai gambar
                        BasicTextField(
                            value = viewModel.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (viewModel.searchQuery.isEmpty()) {
                                    Text(text = "Search asisten, matkul, atau dosen...", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }
            // Container Utama List (Warna Kuning)
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = colorResource(R.color.yellow)
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {

                    // 1. Bagian Header Tabel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nama Asisten Dosen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "Mata Kuliah",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Nama Dosen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    // 2. Tampilkan Data berdasarkan State
                    when (val state = viewModel.homeUiState) {
                        is HomeUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = colorResource(R.color.green))
                            }
                        }
                        is HomeUiState.Success -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                itemsIndexed(state.dataMaster) { index, data ->
                                    val itemColor =
                                        if (index % 2 == 0) colorResource(R.color.red) else colorResource(
                                            R.color.green
                                        )
                                    ItemMasterRow(
                                        data = data,
                                        backgroundColor = itemColor,
                                        onClick = { onDetailClick(data.id_master) } // Pastikan onDetailClick ada di parameter HalamanHome
                                    )
                                }
                            }
                        }
                        is HomeUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = state.message,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ItemMasterRow(data: MasterResponse, backgroundColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.nama_lengkap,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.2f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = data.nama_mk,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = data.nama_dosen,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}