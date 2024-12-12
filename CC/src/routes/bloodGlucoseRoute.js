const express = require('express');
const router = express.Router();
const { bloodGlucoseController } = require('../controllers');
const { authenticateJwtToken } = require('../middlewares');

router.post('/', authenticateJwtToken, bloodGlucoseController.create);
router.get('/all/:userId', authenticateJwtToken, bloodGlucoseController.getAllByUserId);
router.put('/:id', authenticateJwtToken, bloodGlucoseController.update);
router.delete('/:id', authenticateJwtToken, bloodGlucoseController.remove);
router.get('/:id', authenticateJwtToken, bloodGlucoseController.getById);

module.exports = router;