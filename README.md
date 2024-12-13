# Diabetku API

Diabetku is an API for managing diabetes data, blood glucose levels, and meal records. This API is built using Node.js and Express.

### Key Features

- **User Registration and Authentication**: Users can create an account and log in using their username and password. Authentication is required to access the services.
- **Diabetes Check**: Users can submit their health data to predict the likelihood of diabetes based on various health metrics.
- **Blood Glucose Tracking**: Users can record and track their blood glucose levels over time, allowing for better management of their condition.
- **Meal Records**: Users can log their meals to keep track of their dietary habits and make necessary adjustments.
- **Video Resources**: Access educational videos related to diabetes management and healthy living.

## Installation Process

### Required
Make sure you have the following software installed on your system:

- Node.js (version 18 or later)
- npm

### Installation Steps
1. Clone this repository:

   ```bash
      git clone https://github.com/1malla1/Diabetku.git
   ```

   ```bash
      gCD Diabetku
   ```

   ```bash
      git checkout Cloud-computing
   ```

2. Go to the project directory:

   ```bash
   cd CC
   ```

3. Install dependencies:

   ```bash
   npm install
   ```

4. Insert the secret key in the `.env` file:

   ```plaintext
   SECRET_KEY=your_secret_key
   ```

5. Run the API:

   ```bash
   npm run dev
   ```

### API Access
Open your browser and visit [http://localhost:3000/api](http://localhost:3000/api) to access the API.

## Database Tables

Here are the explanations of the tables needed for this project:

### 1. Table `users`
This table stores user information.

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Table `diabetes_check`
This table stores diabetes check data for each user.

```sql
CREATE TABLE diabetes_check (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    pregnancies INT,
    glucose FLOAT,
    blood_pressure FLOAT,
    skin_thickness FLOAT,
    insulin FLOAT,
    bmi FLOAT,
    diabetes_pedigree_function FLOAT,
    age INT,
    result VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);
```

### 3. Table `blood_glucose`
This table stores blood glucose records for each user.

```sql
CREATE TABLE blood_glucose (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    glucose_value FLOAT NOT NULL,
    test_type VARCHAR(50),
    test_date DATE NOT NULL,
    test_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Table `meal_records`
This table stores meal records consumed by users.

```sql
CREATE TABLE meal_records (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    food_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5. Table `videos`
This table stores information about videos related to diabetes.

```sql
CREATE TABLE videos (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    link VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Database Relationships

- **users**: This table serves as the main table storing user information. 
- **diabetes_check**: This table has a one-to-one relationship with the `users` table, where each user can have one set of diabetes check data.
- **blood_glucose**: This table has a one-to-many relationship with the `users` table, where each user can have multiple blood glucose records.
- **meal_records**: This table also has a one-to-many relationship with the `users` table, where each user can have multiple meal records.
- **videos**: This table does not have a direct relationship with other tables but can be used to store video information relevant to all users.

## Available Endpoints

- **User**
  - `POST /users/register` - Register a new user
  - `POST /users/login` - User login
  - `GET /users/data/:user_id` - Get user data by ID

- **Diabetes Check**
  - `POST /diabetes-check/:userId` - Create or update diabetes check data
  - `GET /diabetes-check/:userId` - Get diabetes check data by user_id

- **Blood Glucose**
  - `POST /blood-glucose` - Create blood glucose record
  - `GET /blood-glucose/all/:userId` - Get all blood glucose data by user_id

- **Meal Records**
  - `POST /meal-records` - Create meal record
  - `GET /meal-records/:userId` - Get meal records by user_id

- **Videos**
  - `GET /videos` - Get all videos

## Authentication

This service uses tokens for authentication. You need to have an account to access this service. If you do not have an account, please create a new one. After that, create a token for authentication. The process is similar to logging in, where you need to authenticate yourself with a username and password. If the authentication is valid, you will receive a token.

The tokens provided consist of `accessToken` and `refreshToken`. 
- **accessToken**: This token is valid for 30 minutes. You need to use it to access the service.
- **refreshToken**: This token is used to refresh the `accessToken`. If you want to refresh the token, you need to send the `refreshToken` to the service. If the `refreshToken` is valid, you will receive a new `accessToken`. If it is not valid, you will receive an error message.

Make sure to keep your tokens secure and do not share them with others.

## Postman Collection
You can view and test the API using Postman. [Click here to access the Postman Collection](https://capston-8616.postman.co/workspace/Capston-API~2cd580d2-3078-4646-9977-bcfc4457b35e/collection/39631592-b301058f-6f36-4682-81a4-068faf41f68e?action=share&creator=39631592).
