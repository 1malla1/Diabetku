const { videoModel } = require('../models');

// Fungsi untuk membuat video
const createVideo = async (title, description, link) => {
    return await videoModel.createVideo(title, description, link);
};

// Fungsi untuk mendapatkan video
const getVideos = async (offset, limit) => {
    return await videoModel.getVideos(offset, limit);
};

module.exports = {
    createVideo,
    getVideos,
}; 