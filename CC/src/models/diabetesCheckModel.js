const pool = require('../configs/database');

const upsertDiabetesCheck = async (userId, data) => {
    const result = await pool.query(
        `INSERT INTO diabetes_check (user_id, pregnancies, glucose, blood_pressure, skin_thickness, insulin, bmi, diabetes_pedigree_function, age, result)
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
        ON CONFLICT (user_id) DO UPDATE SET
            pregnancies = EXCLUDED.pregnancies,
            glucose = EXCLUDED.glucose,
            blood_pressure = EXCLUDED.blood_pressure,
            skin_thickness = EXCLUDED.skin_thickness,
            insulin = EXCLUDED.insulin,
            bmi = EXCLUDED.bmi,
            diabetes_pedigree_function = EXCLUDED.diabetes_pedigree_function,
            age = EXCLUDED.age,
            result = EXCLUDED.result
        RETURNING *`,
        [
            userId,
            data.pregnancies,
            data.glucose,
            data.blood_pressure,
            data.skin_thickness,
            data.insulin,
            data.bmi,
            data.diabetes_pedigree_function,
            data.age,
            data.result // Hasil prediksi
        ]
    );
    return result.rows[0];
};

// Fungsi untuk mendapatkan data diabetes check berdasarkan user_id
const getDiabetesCheckByUserId = async (userId) => {
    const result = await pool.query(
        `SELECT * FROM diabetes_check WHERE user_id = $1`,
        [userId]
    );
    return result.rows[0];
};

module.exports = {
    upsertDiabetesCheck,
    getDiabetesCheckByUserId,
}; 