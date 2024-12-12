const { diabetesCheck } = require('../models');

module.exports = {
    userController: require('./userController'),
    bloodGlucoseController: require('./bloodGlucoseController'),
    mealRecordController: require('./mealRecordController'),
    videoController: require('./videoController'),
    diabetesCheckController: require('./diabetesCheckController')
};