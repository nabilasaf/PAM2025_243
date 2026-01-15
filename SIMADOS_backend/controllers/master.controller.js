const pool = require('../config/db.config');

// Helper function untuk menjalankan transaksi SQL
const executeTransaction = async (queries) => {
    const connection = await pool.getConnection();
    try {
        await connection.beginTransaction();
        const results = [];
        for (const { sql, values } of queries) {
            const [rows] = await connection.execute(sql, values);
            results.push(rows);
        }
        await connection.commit();
        return results;
    } catch (error) {
        await connection.rollback();
        throw error;
    } finally {
        connection.release();
    }
};

// --- A. CREATE (Tambah Data Simultan) ---
exports.createSimultaneousData = async (req, res) => {
    // Ambil semua field sesuai struktur tabel baru
    const { 
        nim, nama_lengkap, 
        kode_mk, nama_mk, sks, 
        nip_nik, nama_dosen, jabatan 
    } = req.body;
    
    const input_by_username = req.user.username; 

    try {
        // 1. Insert ke transaksi_master untuk dapat id_master
        const [masterResult] = await pool.execute(
            'INSERT INTO transaksi_master (input_by_username) VALUES (?)', 
            [input_by_username]
        );
        const id_master = masterResult.insertId;

        // 2. Insert ke 3 tabel detail secara paralel (REQ-TAMBAH-06)
        await Promise.all([
            pool.execute(
                'INSERT INTO asisten_dosen (id_master, nim, nama_lengkap, status_aktif_asdos) VALUES (?, ?, ?, ?)',
                [id_master, nim, nama_lengkap, true]
            ),
            pool.execute(
                'INSERT INTO mata_kuliah (id_master, kode_mk, nama_mk, sks) VALUES (?, ?, ?, ?)',
                [id_master, kode_mk, nama_mk, sks]
            ),
            pool.execute(
                'INSERT INTO dosen_koordinator (id_master, nip_nik, nama_dosen, jabatan) VALUES (?, ?, ?, ?)',
                [id_master, nip_nik, nama_dosen, jabatan]
            )
        ]);

        res.status(201).json({ message: "Data berhasil disimpan ke semua tabel." });
    } catch (error) {
        res.status(500).json({ message: "Gagal simpan", error: error.message }); // REQ-TAMBAH-09
    }
};
// --- B. READ (Tampil Dashboard Gabungan) ---
exports.findAllSimultaneousData = async (req, res) => {
    // Query JOIN untuk menggabungkan 4 tabel
    const sql = `
        SELECT
            TM.id_master,
            AD.nim, AD.nama_lengkap,
            MK.kode_mk, MK.nama_mk,
            DK.nip_nik, DK.nama_dosen,
            TM.tanggal_transaksi
        FROM
            transaksi_master TM
        JOIN asisten_dosen AD ON TM.id_master = AD.id_master
        JOIN mata_kuliah MK ON TM.id_master = MK.id_master
        JOIN dosen_koordinator DK ON TM.id_master = DK.id_master
        ORDER BY TM.tanggal_transaksi DESC
    `;

    try {
        const [rows] = await pool.execute(sql);
        res.status(200).json({ data: rows });
    } catch (error) {
        res.status(500).json({ message: "Gagal mengambil data dashboard.", error: error.sqlMessage });
    }
};

// --- C. DELETE (Hapus Simultan) ---
exports.deleteSimultaneousData = async (req, res) => {
    const { id_master } = req.params;
    // Cukup hapus dari transaksi_master, ON DELETE CASCADE akan menangani sisanya
    const sql = 'DELETE FROM transaksi_master WHERE id_master = ?';

    try {
        const [result] = await pool.execute(sql, [id_master]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Transaksi Master tidak ditemukan." });
        }

        res.status(204).json({ message: "Data master simultan berhasil dihapus." });
    } catch (error) {
        res.status(500).json({ message: "Gagal menghapus data.", error: error.sqlMessage });
    }
};

// --- D. READ (Tampil Detail per ID) ---
exports.findSimultaneousDataById = async (req, res) => {
    const { id_master } = req.params;

    const sql = `
        SELECT
            AD.*, MK.*, DK.*, TM.tanggal_transaksi
        FROM
            transaksi_master TM
        JOIN asisten_dosen AD ON TM.id_master = AD.id_master
        JOIN mata_kuliah MK ON TM.id_master = MK.id_master
        JOIN dosen_koordinator DK ON TM.id_master = DK.id_master
        WHERE TM.id_master = ?
    `;

    try {
        const [rows] = await pool.execute(sql, [id_master]);
        
        if (rows.length === 0) {
            return res.status(404).json({ message: "Data detail tidak ditemukan." });
        }
        
        res.status(200).json({ data: rows[0] });
    } catch (error) {
        res.status(500).json({ message: "Gagal mengambil data detail.", error: error.sqlMessage });
    }
};

//--- E. UPDATE (Update Data Simultan) ---
exports.updateSimultaneousData = async (req, res) => {
    const { id_master } = req.params;
    const { nim, nama_lengkap, status_aktif_asdos, kode_mk, nama_mk, sks, nip_nik, nama_dosen, jabatan } = req.body;

    try {
        const queries = [
            // 1. Update ASISTEN_DOSEN
            { 
                sql: 'UPDATE asisten_dosen SET nim=?, nama_lengkap=?, status_aktif_asdos=? WHERE id_master=?', 
                values: [nim, nama_lengkap, status_aktif_asdos, id_master] 
            },
            // 2. Update MATA_KULIAH
            { 
                sql: 'UPDATE mata_kuliah SET kode_mk=?, nama_mk=?, sks=? WHERE id_master=?', 
                values: [kode_mk, nama_mk, sks, id_master] 
            },
            // 3. Update DOSEN_KOORDINATOR
            { 
                sql: 'UPDATE dosen_koordinator SET nip_nik=?, nama_dosen=?, jabatan=? WHERE id_master=?', 
                values: [nip_nik, nama_dosen, jabatan, id_master] 
            }
        ];

        // Eksekusi semua query dalam satu transaksi database
        await executeTransaction(queries);

        res.status(200).json({ message: "Data master simultan berhasil diperbarui." });

    } catch (error) {
        console.error("Error updating data:", error);
        res.status(500).json({ message: "Gagal memperbarui data master. Transaksi dibatalkan.", error: error.sqlMessage || error.message });
    }
};


exports.getDetailById = async (req, res) => {
    const { id } = req.params; // Mengambil ID dari URL
    try {
        const [rows] = await pool.execute(`
            SELECT tm.id_master, ad.nim, ad.nama_lengkap, ad.status_aktif_asdos,
                   mk.kode_mk, mk.nama_mk, mk.sks,
                   dk.nip_nik, dk.nama_dosen, dk.jabatan
            FROM transaksi_master tm
            JOIN asisten_dosen ad ON tm.id_master = ad.id_master
            JOIN mata_kuliah mk ON tm.id_master = mk.id_master
            JOIN dosen_koordinator dk ON tm.id_master = dk.id_master
            WHERE tm.id_master = ?`, [id]);

        if (rows.length > 0) {
            res.status(200).json(rows[0]); // Kirim data lengkap
        } else {
            res.status(404).json({ message: "Data tidak ditemukan" });
        }
    } catch (error) {
        res.status(500).json({ message: "Gagal mengambil detail", error: error.message });
    }
};