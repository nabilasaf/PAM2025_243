package com.example.simados_tu.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simados_tu.repositori.RepositoriSimados
import com.example.simados_tu.repositori.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TambahViewModel(
    private val repositoriSimados: RepositoriSimados,
    private val tokenManager: TokenManager
) : ViewModel() {

    // State Form
    var nim by mutableStateOf("")
    var namaAsdos by mutableStateOf("")
    var kodeMk by mutableStateOf("")
    var namaMk by mutableStateOf("")
    var sks by mutableStateOf("")
    var nipNik by mutableStateOf("")
    var namaDosen by mutableStateOf("")
    var jabatan by mutableStateOf("")

    var isEntryValid by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Helper Validasi: Cek apakah hanya huruf dan spasi
    private fun isAlphabetic(v: String) = v.all { it.isLetter() || it.isWhitespace() }

    // Fungsi Validasi
    fun updateValidasi() {
        isEntryValid = nim.isNotBlank() &&
                namaAsdos.isNotBlank() &&
                kodeMk.isNotBlank() &&
                namaMk.isNotBlank() &&
                sks.isNotBlank() &&
                nipNik.isNotBlank() &&
                namaDosen.isNotBlank()
    }
    // Fungsi Validasi Lengkap
    fun validate(): String? {
        // 1. Cek Kolom Kosong (Wajib diisi karena NOT NULL di DB)
        if (nim.isBlank() || namaAsdos.isBlank() || kodeMk.isBlank() ||
            namaMk.isBlank() || sks.isBlank() || nipNik.isBlank() ||
            namaDosen.isBlank() || jabatan.isBlank()) {
            return "Semua kolom wajib diisi!"
        }

        // 2. Validasi NIM: Maks 15 digit (Hanya angka sudah difilter di UI)
        if (nim.length > 15) return "NIM maksimal 15 angka!"

        // 3. Validasi Nama Asdos: Hanya huruf & Maks 100
        if (!isAlphabetic(namaAsdos)) return "Nama Asdos hanya boleh huruf!"
        if (namaAsdos.length > 100) return "Nama Asdos maksimal 100 huruf!"

        // 4. Validasi SKS: Maks 10 dan harus angka
        val s = sks.toIntOrNull()
        if (s == null || s > 10) return "SKS harus angka dan maksimal 10!"

        // 5. Validasi NIP/NIK: Maks 20 digit
        if (nipNik.length > 20) return "NIP/NIK maksimal 20 angka!"

        // 6. Validasi Nama Dosen: Hanya huruf & Maks 100
        if (!isAlphabetic(namaDosen)) return "Nama Dosen hanya boleh huruf!"
        if (namaDosen.length > 100) return "Nama Dosen maksimal 100 huruf!"

        // 7. Validasi Jabatan: Hanya huruf & Maks 50
        if (!isAlphabetic(jabatan)) return "Jabatan hanya boleh huruf!"
        if (jabatan.length > 50) return "Jabatan maksimal 50 huruf!"

        return null
    }

    fun simpan(onSuccess: () -> Unit) {
        val error = validate()
        if (error != null) {
            errorMessage = error
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null // Reset error sebelum mencoba simpan
            try {
                // Mengambil token terbaru dari DataStore
                val token = tokenManager.getToken.first()
                if (token != null) {
                    repositoriSimados.insertMaster(
                        token = token,
                        nim = nim,
                        namaAsdos = namaAsdos,
                        kodeMk = kodeMk,
                        namaMk = namaMk,
                        sks = sks,
                        nipNik = nipNik,
                        namaDosen = namaDosen,
                        jabatan = jabatan
                    )
                    onSuccess() // REQ-TAMBAH-07: Kembali ke dashboard jika berhasil
                } else {
                    errorMessage = "Sesi habis, silakan login kembali."
                }
            } catch (e: Exception) {
                // REQ-TAMBAH-09: Tampilkan pesan error jika server menolak (misal: NIM Duplikat)
                errorMessage = "Gagal simpan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}