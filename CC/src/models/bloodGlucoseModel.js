const pool = require('../configs/database');

// Fungsi untuk membuat catatan kadar glukosa
const createBloodGlucose = async (userId, glucoseValue, testType, testDate, testTime) => {
    const result = await pool.query(
        `INSERT INTO blood_glucose (user_id, glucose_value, test_type, test_date, test_time) 
        VALUES ($1, $2, $3, $4, $5) RETURNING *`,
        [userId, glucoseValue, testType, testDate, testTime]
    );
    return result.rows[0];
};

// Fungsi untuk mendapatkan semua data kadar glukosa berdasarkan user_id
const getAllBloodGlucoseByUserId = async (userId) => {
    const result = await pool.query(
        `SELECT 
        id,
        glucose_value, 
        test_type, 
        TO_CHAR(test_date, 'DD Month YYYY') AS formatted_date, 
        TO_CHAR(test_time, 'HH24:MI') AS formatted_time
        FROM blood_glucose
        WHERE user_id = $1
        ORDER BY test_date ASC, test_time ASC`,
        [userId]
    );
    return result.rows;
};

// Fungsi untuk mengupdate catatan kadar glukosa
const updateBloodGlucose = async (id, userId, glucoseValue, testType, testDate, testTime) => {
    const result = await pool.query(
        `UPDATE blood_glucose 
        SET glucose_value = $1, test_type = $2, test_date = $3, test_time = $4 
        WHERE id = $5 AND user_id = $6 
        RETURNING *`,
        [glucoseValue, testType, testDate, testTime, id, userId]
    );
    return result.rows[0];
};

// Fungsi untuk menghapus catatan kadar glukosa
const deleteBloodGlucose = async (id) => {
    const result = await pool.query(
        `DELETE FROM blood_glucose 
        WHERE id = $1 
        RETURNING *`,
        [id]
    );
    return result.rows[0]; // Mengembalikan data yang dihapus
};

const getBloodGlucoseById = async (id) => {
    const result = await pool.query(
        `SELECT * FROM blood_glucose WHERE id = $1`,
        [id]
    );
    return result.rows[0];
};

module.exports = {
    createBloodGlucose,
    getAllBloodGlucoseByUserId,
    updateBloodGlucose,
    deleteBloodGlucose,
    getBloodGlucoseById,
};
