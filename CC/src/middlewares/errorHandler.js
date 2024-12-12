const errorHandler = (err, req, res, next) => {
    res.status(500).json({
        status: 'error',
        message: 'Internal Server Error',
        errorDetails: process.env.NODE_ENV === 'development' ? err.message : undefined,
    });
};

module.exports = errorHandler;