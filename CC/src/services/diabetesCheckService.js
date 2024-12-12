const tf = require('@tensorflow/tfjs-node');
const { diabetesCheckModel } = require('../models');

// Fungsi untuk membuat atau mengupdate data diabetes check
const upsertDiabetesCheck = async (model, userId, inputData) => {
    if (!model) {
        throw new Error('Model tidak ditemukan! Pastikan model sudah dimuat di app.js.');
    }

    const tensorData = tf.tensor([[ 
        inputData.pregnancies / 10, 
        inputData.glucose / 200, 
        inputData.blood_pressure / 120,
        inputData.skin_thickness / 100,
        inputData.insulin / 600,
        inputData.bmi / 50,
        inputData.diabetes_pedigree_function, // Sudah dalam rentang [0, 1]
        inputData.age / 100
    ]]);

    try {
        console.log('Input Shape:', tensorData.shape);
        console.log('Model Inputs:', model.inputs);
        const prediction = model.predict(tensorData);
        const predictionData = prediction.dataSync(); // Ambil hasil prediksi
        console.log('Model-1 Output:', predictionData[0]);

        const predictionResult = predictionData[0] >= 0.71 ? 'Diabetes' : 'No Diabetes';

        const result = await diabetesCheckModel.upsertDiabetesCheck(userId, {
            ...inputData,
            result: predictionResult, // Tambahkan hasil prediksi
        });
        return result;

    } catch (error) {
        throw new Error(`Error dalam prediksi: ${error.message}`);
    }
};


// Fungsi untuk mendapatkan data diabetes check berdasarkan user_id
const getDiabetesCheckByUserId = async (userId) => {
    return await diabetesCheckModel.getDiabetesCheckByUserId(userId);
};

module.exports = {
    upsertDiabetesCheck,
    getDiabetesCheckByUserId,
}; 