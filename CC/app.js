require('dotenv').config();
const express = require('express');
const errorHandler = require('./src/middlewares/errorHandler');
const undefinedEndpointHandler = require('./src/middlewares/undefine');
const userRoute = require('./src/routes/userRoute');
const bloodGlucoseRoute = require('./src/routes/bloodGlucoseRoute');
const mealRecordRoute = require('./src/routes/mealRecordRoute');
const videoRoute = require('./src/routes/videoRoute');
const diabetesCheckRoute = require('./src/routes/diabetesCheckRoute');
const tf = require('@tensorflow/tfjs-node');

const PORT = process.env.PORT || 3000;
const app = express();

async function loadModel1() {
    try {
        const model1 = await tf.loadGraphModel(process.env.MODEL_URL_1, { strict: false });
        app.locals.model1 = model1;
        console.log('Model 1 berhasil dimuat!');
    } catch (error) {
        console.error('Gagal memuat Model 1:', error.message);
    }
}

loadModel1();

app.get('/', async (req, res) => {
    try {
        res.send('<h1>Welcome to the Diabetku!</h1>');
    } catch (err) {
        console.error(err);
        res.status(500).send('Internal Server Error');
    }
});
app.use((req, res, next) => {
    req.app.model1 = app.locals.model1;
    req.app.model2 = app.locals.model2;
    next();
});

app.use(express.json());
app.use('/users', userRoute);
app.use('/blood-glucose', bloodGlucoseRoute);
app.use('/meal-records', mealRecordRoute);
app.use('/videos', videoRoute);
app.use('/diabetes-check', diabetesCheckRoute);
app.use(undefinedEndpointHandler);
app.use(errorHandler);
app.listen(PORT, () => {
    console.log(`Server is listening on port ${PORT}`);
});
