from flask import Flask, jsonify, request
from keras.models import load_model
import pandas as pd
import numpy as np
from tabulate import tabulate
from flask_caching import Cache

app = Flask(__name__)

# Load machine learning model
model = load_model("model_glucose.h5", compile=False)

# Load nutrition data
nutrition_data = pd.read_csv("nutrition.csv")

# Log kolom yang ada dalam nutrition_data
print("Kolom yang ada dalam nutrition_data:", nutrition_data.columns.tolist())
print("Isi nutrition_data:", nutrition_data.head())  # Menampilkan beberapa baris pertama dari DataFrame

@cache.cached(timeout=60, query_string=True)
def recommend_food(glucose_input):
    """
    Memberikan rekomendasi makanan berdasarkan kadar glukosa input,
    menampilkan nama makanan, jumlah karbohidrat, kalori, dan estimasi glukosa.

    Args:
        glucose_input (float): Kadar glukosa dalam darah.

    Returns:
        str: Rekomendasi makanan dalam bentuk tabel atau pesan kesalahan.
    """
    # Log input glucose level
    print("Processing glucose input:", glucose_input)

    if glucose_input is None:
        raise ValueError("Glucose input cannot be None")

    suitable_foods = nutrition_data[(nutrition_data['glucose'] >= glucose_input - 10) &
                                    (nutrition_data['glucose'] <= glucose_input + 10)]

    # Log nilai suitable_foods
    print("Suitable foods found:", suitable_foods)

    if suitable_foods.empty:
        return f"Tidak ada makanan dengan estimasi glukosa sekitar {glucose_input} mg/dL."

    # Siapkan data untuk ditampilkan dalam tabel
    table_data = []
    for _, row in suitable_foods.iterrows():
        table_data.append([row['name'], row['carbohydrate'], row['calories'], row['glucose']])

    # Header untuk tabel
    headers = ["Nama Makanan", "Karbohidrat (g)", "Kalori (kcal)", "Estimasi Glukosa (mg/dL)"]

    # Buat tabel
    return tabulate(table_data, headers=headers, tablefmt="grid")

@app.route("/")
def index():
    return jsonify({
        "status": {
            "code": 200,
            "message": "Success fetching API!"
        },
        "data": None
    }), 200

@app.route("/recommend", methods=['POST'])
def recommend():
    try:
        data = request.get_json()
        glucose_level = data.get('glucose_level')

        # Log input glucose level
        print("Input glucose level:", glucose_level)

        # Panggil fungsi recommend_food
        recommendations = recommend_food(glucose_level)

        # Log recommendations
        print("Recommendations:", recommendations)

        return jsonify({
            "status": {
                "code": 200,
                "message": "Recommendation successful!",
            },
            "data": {
                "recommendations": recommendations
            }
        }), 200

    except Exception as e:
        return jsonify({
            "status": {
                "code": 500,
                "message": f"Internal Server Error: {str(e)}"
            },
            "data": None
        }), 500

if __name__ == "__main__":
    app.run(debug=True)