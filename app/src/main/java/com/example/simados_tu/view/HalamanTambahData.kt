import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simados_tu.viewmodel.TambahViewModel
import com.example.simados_tu.viewmodel.provider.PenyediaViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanTambah(
    onNavigateBack: () -> Unit,
    viewModel: TambahViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val focusManager = LocalFocusManager.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Dialog Konfirmasi
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Simpan", fontWeight = FontWeight.Bold) },
            text = { Text("Pastikan data NIM, SKS, dan NIP sudah benar. Simpan sekarang?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.simpan(onSuccess = onNavigateBack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1F27))
                ) { Text("Ya, Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Data Asdos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B3B32))
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tampilan Peringatan jika validasi gagal (REQ-TAMBAH-09)
                viewModel.errorMessage?.let {
                    item {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF7B1F27)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Text(it, color = Color(0xFF7B1F27), modifier = Modifier.padding(12.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // --- SECTION 1: DATA ASISTEN ---
                item { Text("Informasi Asisten", fontWeight = FontWeight.Bold, color = Color.DarkGray) }
                item {
                    CustomInputField(
                        label = "NIM (Maks 15 Angka)",
                        value = viewModel.nim,
                        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 15) viewModel.nim = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                item {
                    CustomInputField(
                        label = "Nama Lengkap Asdos (Hanya Huruf)",
                        value = viewModel.namaAsdos,
                        onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() } && it.length <= 100) viewModel.namaAsdos = it }
                    )
                }

                // --- SECTION 2: DATA MATA KULIAH ---
                item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
                item { Text("Informasi Mata Kuliah", fontWeight = FontWeight.Bold, color = Color.DarkGray) }
                item {
                    CustomInputField("Kode MK", viewModel.kodeMk, { viewModel.kodeMk = it })
                }
                item {
                    CustomInputField("Nama Mata Kuliah", viewModel.namaMk, { viewModel.namaMk = it })
                }
                item {
                    CustomInputField(
                        label = "SKS (Maks 10)",
                        value = viewModel.sks,
                        onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.sks = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // --- SECTION 3: DATA DOSEN ---
                item { HorizontalDivider(Modifier.padding(vertical = 8.dp)) }
                item { Text("Informasi Dosen", fontWeight = FontWeight.Bold, color = Color.DarkGray) }
                item {
                    CustomInputField(
                        label = "NIP/NIK (Maks 20 Angka)",
                        value = viewModel.nipNik,
                        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 20) viewModel.nipNik = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                item {
                    CustomInputField(
                        label = "Nama Dosen Pengampu (Hanya Huruf)",
                        value = viewModel.namaDosen,
                        onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() } && it.length <= 100) viewModel.namaDosen = it }
                    )
                }
                item {
                    CustomInputField(
                        label = "Jabatan (Hanya Huruf)",
                        value = viewModel.jabatan,
                        onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() } && it.length <= 50) viewModel.jabatan = it }
                    )
                }

                // Tombol Submit
                item {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val error = viewModel.validate()
                            if (error == null) showConfirmDialog = true else viewModel.errorMessage = error
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1F27))
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Submit Data", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CustomInputField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ) {
        Column {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = keyboardOptions,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}