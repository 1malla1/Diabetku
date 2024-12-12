const isPublic = () => (req,next) => {
    req.isPublic = true;
    next();
};

module.exports = isPublic;
