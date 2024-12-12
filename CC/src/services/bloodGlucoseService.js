const { bloodGlucoseModel } = require('../models');

const createBloodGlucose = async (userId, glucoseValue, testType, testDate, testTime) => {
    return await bloodGlucoseModel.createBloodGlucose(userId, glucoseValue, testType, testDate, testTime);
};

// Fungsi untuk mendapatkan semua data kadar glukosa berdasarkan user_id
const getAllBloodGlucoseByUserId = async (userId) => {
    return await bloodGlucoseModel.getAllBloodGlucoseByUserId(userId);
};

// Fungsi untuk mengupdate catatan kadar glukosa
const updateBloodGlucose = async (id, userId, glucoseValue, testType, testDate, testTime) => {
    return await bloodGlucoseModel.updateBloodGlucose(id, userId, glucoseValue, testType, testDate, testTime);
};

// Fungsi untuk menghapus catatan kadar glukosa
const deleteBloodGlucose = async (id) => {
    return await bloodGlucoseModel.deleteBloodGlucose(id);
};

const getBloodGlucoseById = async (id) => {
    return await bloodGlucoseModel.getBloodGlucoseById(id);
};

module.exports = {
    createBloodGlucose,
    getAllBloodGlucoseByUserId,
    updateBloodGlucose,
    deleteBloodGlucose,
    getBloodGlucoseById,
};