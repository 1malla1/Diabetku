const pool = require('../configs/database');

// Fungsi untuk membuat video
const createVideo = async (title, description, link) => {
    const result = await pool.query(
        `INSERT INTO videos (title, description, link) 
        VALUES ($1, $2, $3) RETURNING *`,
        [title, description, link]
    );
    return result.rows[0];
};

// Fungsi untuk mendapatkan semua video tanpa pagination
const getVideos = async () => {
    const result = await pool.query(
        `SELECT * FROM videos ORDER BY id DESC`
    );
    return result.rows;
};

module.exports = {
    createVideo,
    getVideos,
}; 