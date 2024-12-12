const { videoService } = require('../services');

// Fungsi untuk membuat video
const create = async (req, res) => {
    const { title, description, link } = req.body;
    try {
        const newVideo = await videoService.createVideo(title, description, link);
        res.status(201).json({
            status: 'success',
            message: 'Video created',
            data: newVideo,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to create video',
            error: error.message,
        });
    }
};

// Fungsi untuk mendapatkan semua video
const getAll = async (req, res) => {
    try {
        const videos = await videoService.getVideos(); // Panggil tanpa parameter
        res.status(200).json({
            status: 'success',
            data: videos,
        });
    } catch (error) {
        const statusCode = error.statusCode || 400;
        res.status(statusCode).json({
            status: 'failed',
            message: 'Failed to retrieve videos',
            error: error.message,
        });
    }
};

module.exports = {
    create,
    getAll,
}; 