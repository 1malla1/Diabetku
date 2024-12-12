const { diabetesCheck } = require('../models');

module.exports = {
    userRoute: require('./userRoute'),
    bloodGlucoseRoute: require('./bloodGlucoseRoute'),
    mealRoute: require('./mealRecordRoute'),
    notification: require('./notificationRoute'),
    video: require('./videoRoute'),
    diabetesCheckRoute: require('./diabetesCheckRoute')
};