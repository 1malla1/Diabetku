const { diabetesCheckService } = require('../services');

const diabetesCheck = async (req, res) => {
    try {
        const model = req.app.locals.model1; // Ambil model dari app instance
        const userId = req.params.userId; // Ambil userId dari parameter URL
        const inputData = req.body; // Ambil data input dari body request

        // Validasi input data
        const requiredFields = [
            'pregnancies',
            'glucose',
            'blood_pressure',
            'skin_thickness',
            'insulin',
            'bmi',
            'diabetes_pedigree_function',
            'age',
        ];

        const missingFields = requiredFields.filter((field) => inputData[field] === undefined);

        if (missingFields.length > 0) {
            return res.status(400).json({
                status: 'failed',
                message: `Incomplete input data. Missing fields: ${missingFields.join(', ')}`,
            });
        }

        // Proses prediksi dan simpan ke database
        const result = await diabetesCheckService.upsertDiabetesCheck(model, userId, inputData);

        // Berikan respons ke klien
        res.status(200).json({
            status: 'success',
            data: result,
        });
    } catch (error) {
        console.error('Error details:', error); // Logging error yang lebih spesifik
        res.status(500).json({
            status: 'error',
            message: 'Terjadi kesalahan dalam memproses permintaan',
            errorDetails: error.message, // Kirim detail error (opsional untuk debugging)
        });
    }
};



// Fungsi untuk mendapatkan data diabetes check berdasarkan user_id
const getByUserId = async (req, res) => {
    const { userId } = req.params;
    try {
        const data = await diabetesCheckService.getDiabetesCheckByUserId(userId);
        res.status(200).json({
            status: 'success',
            data,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to retrieve diabetes check data',
            error: error.message,
        });
    }
};

module.exports = {
    diabetesCheck,
    getByUserId,
}; 