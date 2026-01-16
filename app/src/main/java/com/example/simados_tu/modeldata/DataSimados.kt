package com.example.simados_tu.modeldata

import android.provider.ContactsContract
import kotlinx.serialization.Serializable

@Serializable
data class AllMasterResponse(
    val data: List<MasterResponse>
)

// Untuk menangkap respon Login
@Serializable
data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserData
)

@Serializable
data class UserData(
    val email: String,
    val nama_staff: String
)

// Untuk data Master (Asdos, MK, Dosen)
@Serializable
data class MasterResponse(

    val id_master: Int,
    val nim: String,
    val nama_lengkap: String,
    val nama_mk: String,
    val nama_dosen: String,
    val status_aktif_asdos: Int? = null,
    val status_aktif_dosen: Int? = null,
    val sks: Int? = null,
    val jabatan: String? = null,
    val kode_mk: String,
    val nip_nik: String,
)

//Untuk data Staff TU
@Serializable
data class StaffResponse(
    val nama_staff: String,
    val email: String,
)

@Serializable
data class DetailResponse(
    val data: MasterResponse
)

@Serializable
data class UpdateUiState(
    val id_master: Int = 0,
    val nim: String = "",
    val nama_lengkap: String = "",
    val kode_mk: String = "",
    val nama_mk: String = "",
    val sks: String = "",
    val nip_nik: String = "",
    val nama_dosen: String = "",
    val jabatan: String = "",
    val status_aktif_asdos: Int = 1,
    val isEntryValid: Boolean = false,
    val errorMessage: String? = null,
    // Data original untuk tracking perubahan
    val originalData: UpdateUiState? = null
) {
    // Fungsi untuk cek apakah ada perubahan data
    fun hasDataChanged(): Boolean {
        if (originalData == null) return false
        return nim != originalData.nim ||
                nama_lengkap != originalData.nama_lengkap ||
                kode_mk != originalData.kode_mk ||
                nama_mk != originalData.nama_mk ||
                sks != originalData.sks ||
                nip_nik != originalData.nip_nik ||
                nama_dosen != originalData.nama_dosen ||
                jabatan != originalData.jabatan ||
                status_aktif_asdos != originalData.status_aktif_asdos
    }
}
fun MasterResponse.toUpdateUiState(): UpdateUiState = UpdateUiState(
    id_master = id_master,
    nim = nim,
    nama_lengkap = nama_lengkap,
    kode_mk = kode_mk,
    nama_mk = nama_mk,
    sks = sks?.toString() ?: "",
    nip_nik = nip_nik,
    nama_dosen = nama_dosen,
    jabatan = jabatan ?: "",
    status_aktif_asdos = status_aktif_asdos ?: 1
)