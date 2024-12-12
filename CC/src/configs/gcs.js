const { Storage } = require('@google-cloud/storage');
const path = require('path');

const keyFilename = process.env.GCP_KEYFILE_PATH;


const storage = new Storage({
    keyFilename,
    projectId: process.env.GCP_PROJECT_ID,
});

const bucketName = process.env.GCP_BUCKET_NAME;
const bucket = storage.bucket(bucketName);

module.exports =  { bucket } ;