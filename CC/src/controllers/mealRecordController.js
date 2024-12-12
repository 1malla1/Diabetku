const { mealRecordService } = require('../services');

const create = async (req, res) => {
    const { userId, foodName} = req.body;
    try {
        const newMealRecord = await mealRecordService.createMealRecord(userId, foodName);
        res.status(201).json({
            status: 'success',
            message: 'Meal record created',
            data: newMealRecord,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to create meal record',
            error: error.message,
        });
    }
};

const getByUserId = async (req, res) => {
    const { userId } = req.params;
    try {
        const mealRecords = await mealRecordService.getMealRecordsByUserId(userId);
        res.status(200).json({
            status: 'success',
            data: mealRecords,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to retrieve meal records',
            error: error.message,
        });
    }
};

module.exports = {
    create,
    getByUserId,
};