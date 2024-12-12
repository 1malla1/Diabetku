const express = require('express');
const router = express.Router();
const { notificationController } = require('../controllers');
const { authenticateJwtToken } = require('../middlewares');

router.post('/', notificationController.create); // Rute untuk membuat notifikasi
router.get('/user/:userId', notificationController.getByUserId); // Rute untuk mendapatkan notifikasi berdasarkan user_id
router.put('/:id', authenticateJwtToken, notificationController.update); // Rute untuk mengupdate notifikasi
router.delete('/:id', authenticateJwtToken, notificationController.remove); // Rute untuk menghapus notifikasi

module.exports = router; 