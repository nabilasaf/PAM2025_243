const pool = require('../config/db.config');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt'); // PENTING: Untuk Hashing Password
const JWT_SECRET = 'YOUR_SUPER_SECRET_KEY_SIMADOS'; // Pindah ke config/.env di production



exports.login = async (req, res) => {
    const { username, password } = req.body;

    try {
        // 1. Cari User di database
        const [rows] = await pool.execute('SELECT username, password_hash FROM staff_tu WHERE username = ?', [username]);

        if (rows.length === 0) {
            return res.status(401).json({ message: 'Username atau password salah.' });
        }

        const user = rows[0];
        
        // 2. Bandingkan Password (Gunakan bcrypt.compare)
        // const isMatch = await bcrypt.compare(password, user.password_hash);
        
        // --- Contoh Simple Tanpa Hashing (HANYA UNTUK DEMO!) ---
        const isMatch = (password === user.password_hash); // Ganti dengan bcrypt.compare() di production
        // --------------------------------------------------------

        if (!isMatch) {
            return res.status(401).json({ message: 'Username atau password salah.' });
        }

        // 3. Buat Token JWT
        const token = jwt.sign({ username: user.username }, JWT_SECRET, { expiresIn: '1h' });

        res.status(200).json({ 
            message: 'Login berhasil',
            token: token,
            user: user.username
        });
        
    } catch (error) {
        res.status(500).json({ message: 'Server Error saat Login.', error: error.message });
    }
};

// Fungsi untuk mengambil data profil Staff TU (REQ-AKUN-02)
exports.getProfile = async (req, res) => {
    // req.user.username didapat dari middleware auth (hasil decode JWT)
    const username = req.user.username; 

    try {
        const [rows] = await pool.execute(
            'SELECT username, nama_staff FROM staff_tu WHERE username = ?', 
            [username]
        );

        if (rows.length === 0) {
            return res.status(404).json({ message: 'User tidak ditemukan.' });
        }

        // Kirim data ke Android sesuai kebutuhan DialogAkun
        res.status(200).json({
            username: rows[0].username,
            nama_staff: rows[0].nama_staff
        });
    } catch (error) {
        res.status(500).json({ message: 'Gagal mengambil profil.', error: error.message });
    }
};