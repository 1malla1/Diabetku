const jwt = require('jsonwebtoken');

function generateAccessToken(id, username) {
    const payload = { id, username };
    return jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: '60m' });
}

function generateRefreshToken(id) {
  return jwt.sign({ id }, process.env.JWT_SECRET, { expiresIn: '7d' }); // Refresh token berlaku selama 7 hari
}

module.exports = {
    generateAccessToken,
    generateRefreshToken,
};
