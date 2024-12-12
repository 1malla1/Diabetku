const bcrypt = require('bcrypt');
const { userModel } = require('../models');
const getUserById = async (userId) => {
    return await userModel.getUserById(userId);
};

const register = async (
    name, 
    email, 
    password, 
    date_of_birth
) => {
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = await userModel.createUser(
        name, 
        email, 
        hashedPassword, 
        date_of_birth, 
        null
    );
    return newUser;
};

const login = async (email, password) => {
    const user = await userModel.getUserByEmail(email);
    if (!user) {
        const error = new Error('Invalid credentials');
        error.statusCode = 401;
        throw error;
    }
    const validPassword = await bcrypt.compare(password, user.password_hash);
    if (!validPassword) {
        const error = new Error('Invalid credentials');
        error.statusCode = 401;
        throw error;
    }
    return { user_id: user.user_id, name: user.name };
};

const updateUser = async (user_id, updates) => {
    const user = await userModel.getUserById(user_id);
    if (!user) {
        const error = new Error('User not found');
        error.statusCode = 404;
        throw error;
    }

    const updatedUser = await userModel.updateUser(user_id, updates);
    return updatedUser;
};

const getDataUser = async (user_id) => {
    try {
        // Mencari user berdasarkan ID di database
        const user = await userModel.getUserById(user_id);
        return user; // Mengembalikan data user (null jika tidak ditemukan)
    } catch (err) {
        // Menangani error, misalnya jika ID tidak valid
        console.error('Error fetching user by ID:', err);
        throw new Error('Invalid user ID or database query failed');
    }
};

const updateUserProfilePicture = async (userId, profilePictureUrl) => {
    const updatedUser = await userModel.updateUser(userId, { profile_image_url: profilePictureUrl });
    return updatedUser;
};

module.exports = {
    register,
    login,
    updateUser,
    updateUserProfilePicture,
    getUserById,
    getDataUser,
};
