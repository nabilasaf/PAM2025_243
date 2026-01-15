const jwt = require('jsonwebtoken');
const JWT_SECRET = 'YOUR_SUPER_SECRET_KEY_SIMADOS'; 

//Tambahkan (req, res, next)
exports.verifyToken = (req, res, next) => { 
    // Ambil token dari header (Bearer Token)
    const token = req.headers.authorization ? req.headers.authorization.split(' ')[1] : null;

    if (!token) {
        return res.status(403).json({ message: 'Akses ditolak. Token tidak disediakan.' });
    }

    try {
        // Verifikasi token
        const decoded = jwt.verify(token, JWT_SECRET);
        req.user = decoded;
        next(); // Lanjutkan ke controller
    } catch (error) {
        return res.status(401).json({ message: 'Token tidak valid.' });
    }
};