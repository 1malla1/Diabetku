const { bucket } = require('../configs/gcs');
const { userService } = require('../services');
const { generateAccessToken, generateRefreshToken } = require('../utils');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');

const register = async (req, res) => {
    const { name, email, password, date_of_birth } = req.body;
    try {
        const newUser = await userService.register(name, email, password, date_of_birth);
        res.status(201).json({
            status: 'success',
            message: 'register',
            data: newUser,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'register',
            error: error.message,
        });
    }
};

const login = async (req, res) => {
    const { email, password } = req.body;
    try {
        const user = await userService.login(email, password);

        // Generate access token and refresh token
        const accessToken = generateAccessToken(user.user_id, user.name);
        const refreshToken = generateRefreshToken(user.user_id, user.name);

        res.status(200).json({
            status: 'success',
            message: 'Login',
            data: { user, accessToken, refreshToken },
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Login',
            error: error.message,
        });
    }
};


const updateUser = async (req, res) => {
    const { user_id } = req.params;
    const updates = req.body; // Ambil data yang ingin diperbarui
    try {
        const updatedUser = await userService.updateUser(user_id, updates);
        res.status(200).json({
            status: 'success',
            message: 'User updated successfully',
            data: updatedUser,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to update user',
            error: error.message,
        });
    }
};

const getDataUserById = async (req, res) => {
    const { user_id } = req.params; // Ambil ID dari parameter request
    try {
        // Panggil service untuk mendapatkan data user
        const user = await userService.getDataUser(user_id);

        if (!user) {
            // Jika user tidak ditemukan
            return res.status(404).json({
                status: 'failed',
                message: 'User not found',
            });
        }

        // Jika user ditemukan, kembalikan data user
        res.status(200).json({
            status: 'success',
            message: 'User fetched successfully',
            data: user,
        });
    } catch (error) {
        // Tangani error yang mungkin terjadi
        const statusCode = error.statusCode || 500;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to fetch user',
            error: error.message,
        });
    }
};

const uploadProfilePicture = async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({
                status: 'failed',
                message: 'No file uploaded',
            });
        }

        if (!req.user || !req.user.id) {
            return res.status(401).json({
                status: 'failed',
                message: 'User not authenticated',
            });
        }

        // Ambil data user untuk mendapatkan URL profile image yang lama
        const currentUser = await userService.getUserById(req.user.id);
        
        // Hapus file lama jika ada
        if (currentUser && currentUser.profile_image_url) {
            try {
                // Ekstrak nama file dari URL
                const oldFileUrl = currentUser.profile_image_url;
                const fileName = oldFileUrl.split('/').pop();
                const oldFile = bucket.file(`profile/${fileName}`);
                
                // Hapus file lama
                await oldFile.delete().catch(err => {
                    console.log('Error deleting old file:', err);
                    // Lanjutkan proses meskipun gagal menghapus file lama
                });
            } catch (error) {
                console.log('Error processing old file:', error);
                // Lanjutkan proses meskipun gagal menghapus file lama
            }
        }

        // Upload file baru
        const file = req.file;
        const newFileName = `profile/${req.user.id}_profile_${Date.now()}_${file.originalname}`;
        const blob = bucket.file(newFileName);
        const blobStream = blob.createWriteStream({
            resumable: false,
            contentType: file.mimetype,
        });

        blobStream.on('error', (err) => {
            console.error('Upload error:', err);
            res.status(500).json({
                status: 'failed',
                message: 'Unable to upload the file',
                error: err.message,
            });
        });

        blobStream.on('finish', async () => {
            const publicUrl = `https://storage.googleapis.com/${bucket.name}/${blob.name}`;
            try {
                const userId = req.user.id;
                const updatedUser = await userService.updateUserProfilePicture(userId, publicUrl);
                res.status(200).json({
                    status: 'success',
                    message: 'File uploaded successfully',
                    data: {
                        url: publicUrl,
                        user: updatedUser
                    },
                });
            } catch (error) {
                console.error('Database update error:', error);
                res.status(500).json({
                    status: 'failed',
                    message: 'Failed to update user profile picture',
                    error: error.message,
                });
            }
        });

        blobStream.end(file.buffer);
    } catch (error) {
        console.error('General error:', error);
        res.status(500).json({
            status: 'failed',
            message: 'Internal server error',
            error: error.message,
        });
    }
};

const refreshAccessToken = async (req, res) => {
    const { token } = req.body;
    if (!token) {
        return res.status(401).json({
            status: 'failed',
            message: 'Token is required',
        });
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
        if (err) return res.sendStatus(403);
        const accessToken = generateAccessToken(user.id, user.username);
        res.json({ accessToken });
    });
};

module.exports = {
    register,
    login,
    updateUser,
    uploadProfilePicture,
    refreshAccessToken,
    getDataUserById
};