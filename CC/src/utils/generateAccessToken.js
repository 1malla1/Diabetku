const jwt = require('jsonwebtoken');

function generateAccessToken(id, username) {
  const payload = { id, username };
  return jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: '60m' });
}

module.exports = generateAccessToken;
