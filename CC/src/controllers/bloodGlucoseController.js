const { bloodGlucoseService } = require('../services');

const create = async (req, res) => {
    const { userId, glucoseValue, testType, testDate, testTime } = req.body;
    try {
        const newBloodGlucose = await bloodGlucoseService.createBloodGlucose(userId, glucoseValue, testType, testDate, testTime);
        res.status(201).json({
            status: 'success',
            message: 'Blood glucose data created',
            data: newBloodGlucose,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to create blood glucose data',
            error: error.message,
        });
    }
};

// Fungsi untuk mendapatkan semua data kadar glukosa berdasarkan user_id
const getAllByUserId = async (req, res) => {
    const { userId } = req.params;
    try {
        const data = await bloodGlucoseService.getAllBloodGlucoseByUserId(userId);
        res.status(200).json({
            status: 'success',
            data,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to retrieve blood glucose data',
            error: error.message,
        });
    }
};

// Fungsi untuk mengupdate catatan kadar glukosa
const update = async (req, res) => {
    const { id } = req.params;
    const userId = req.user.id;
    const { glucoseValue, testType, testDate, testTime } = req.body;
    try {
        const updatedBloodGlucose = await bloodGlucoseService.updateBloodGlucose(id, userId, glucoseValue, testType, testDate, testTime);
        res.status(200).json({
            status: 'success',
            message: 'Blood glucose data updated',
            data: updatedBloodGlucose,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to update blood glucose data',
            error: error.message,
        });
    }
};

// Fungsi untuk menghapus catatan kadar glukosa
const remove = async (req, res) => {
    const { id } = req.params;
    try {
        const deletedBloodGlucose = await bloodGlucoseService.deleteBloodGlucose(id);
        res.status(200).json({
            status: 'success',
            message: 'Blood glucose data deleted',
            data: deletedBloodGlucose,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to delete blood glucose data',
            error: error.message,
        });
    }
};

const getById = async (req, res) => {
    const { id } = req.params;
    try {
        const bloodGlucoseData = await bloodGlucoseService.getBloodGlucoseById(id);
        if (!bloodGlucoseData) {
            return res.status(404).json({
                status: 'failed',
                message: 'Data kadar glukosa tidak ditemukan',
            });
        }
        res.status(200).json({
            status: 'success',
            data: bloodGlucoseData,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Gagal mengambil data kadar glukosa',
            error: error.message,
        });
    }
};

module.exports = {
    create,
    getAllByUserId,
    update,
    remove,
    getById,
};