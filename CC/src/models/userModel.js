const pool = require('../configs/database');

const getUserById = async (user_id) => {
    const result = await pool.query('SELECT * FROM users WHERE user_id = $1', [user_id]);
    return result.rows[0];
};

const getUserByEmail = async (email) => {
    const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
    return result.rows[0];
};

const createUser = async (
    name,
    email,
    password_hash,
    date_of_birth,
    gender,
) => {
    const result = await pool.query(
        'INSERT INTO users (name, email, password_hash, date_of_birth, gender) VALUES ($1, $2, $3, $4, $5) RETURNING *',
        [
            name,
            email,
            password_hash,
            date_of_birth,
            gender,
        ]
    );
    return result.rows[0];
};

const updateUser = async (user_id, updates) => {
    const setClause = Object.keys(updates)
        .map((key, index) => `${key} = $${index + 1}`)
        .join(', ');

    const values = Object.values(updates);
    values.push(user_id);

    const result = await pool.query(
        `UPDATE users SET ${setClause} WHERE user_id = $${values.length} RETURNING *`,
        values
    );
    return result.rows[0];
};

module.exports = {
    getUserById,
    getUserByEmail,
    createUser,
    updateUser,
};
