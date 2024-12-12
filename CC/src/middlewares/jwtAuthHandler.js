const jwt = require('jsonwebtoken');

const authenticateJwtToken = (req, res, next) => {
    if (req.isPublic) {
        return next();
    }

    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (token == null) {
        return res.status(401).json({
            status: 'failed',
            message: 'Unauthorized',
        });
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
        if (err) {
            return res.status(403).json({
                status: 'failed',
                message: 'Forbidden',
            });
        }

        req.user = user; // Simpan informasi pengguna ke dalam request
        next();
    });
};

module.exports = authenticateJwtToken;