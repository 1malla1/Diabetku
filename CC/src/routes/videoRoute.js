const express = require('express');
const router = express.Router();
const { videoController } = require('../controllers');

router.post('/', videoController.create); // Rute untuk membuat video
router.get('/', videoController.getAll); // Rute untuk mendapatkan semua video

module.exports = router; 