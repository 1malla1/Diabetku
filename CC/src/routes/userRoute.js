const express = require('express');
const router = express.Router();
const { userController } = require('../controllers');
const multer = require('multer');
const authenticateJwtToken = require('../middlewares/jwtAuthHandler');

const upload = multer({ storage: multer.memoryStorage() });

router.post('/register', userController.register);
router.post('/login', userController.login);
router.post('/refresh-token', userController.refreshAccessToken); // Tambahkan ini
router.put('/:user_id', authenticateJwtToken, userController.updateUser);
router.post('/upload-profile', authenticateJwtToken, upload.single('profilePicture'), userController.uploadProfilePicture);
router.get('/data/:user_id', authenticateJwtToken, userController.getDataUserById);


module.exports = router;