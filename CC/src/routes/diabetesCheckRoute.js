const express = require('express');
const router = express.Router();
const { diabetesCheckController } = require('../controllers');
const { authenticateJwtToken } = require('../middlewares');

router.post('/:userId', authenticateJwtToken, diabetesCheckController.diabetesCheck); // Rute untuk membuat atau mengupdate data diabetes check
router.get('/:userId', authenticateJwtToken, diabetesCheckController.getByUserId); // Rute untuk mendapatkan data diabetes check berdasarkan user_id

module.exports = router; 