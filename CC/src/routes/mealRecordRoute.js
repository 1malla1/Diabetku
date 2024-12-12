const express = require('express');
const router = express.Router();
const { mealRecordController } = require('../controllers');

router.post('/', mealRecordController.create);
router.get('/:userId', mealRecordController.getByUserId);

module.exports = router;