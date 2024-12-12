const { mealRecordModel } = require('../models');

const createMealRecord = async (userId, foodName) => {
    return await mealRecordModel.createMealRecord(userId, foodName);
};

const getMealRecordsByUserId = async (userId) => {
    return await mealRecordModel.getMealRecordsByUserId(userId);
};

module.exports = {
    createMealRecord,
    getMealRecordsByUserId,
}; 