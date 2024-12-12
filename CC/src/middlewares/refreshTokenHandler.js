const jwt = require('jsonwebtoken');

const verifyRefreshToken = (req, res, next) => {
    const { token } = req.body;
    if (!token || !refreshTokens.includes(token)) {
        return res.sendStatus(403); // Forbidden
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
        if (err) return res.sendStatus(403);
        req.user = user;
        next();
    });
};

module.exports = verifyRefreshToken; 