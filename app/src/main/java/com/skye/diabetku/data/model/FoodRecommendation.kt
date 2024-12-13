package com.skye.diabetku.data.model

object FoodRecommendationParser {
    fun parseRecommendations(recommendations: String): List<FoodItem> {
        val foodList = mutableListOf<FoodItem>()

        val lines = recommendations.split("\n").filter {
            !it.startsWith("+") && !it.contains("Nama Makanan") && !it.contains("===")
        }

        for (line in lines) {
            if (line.isBlank()) continue

            val columns = line.split("|").map { it.trim() }.filter { it.isNotEmpty() }
            if (columns.size >= 4) {
                try {
                    foodList.add(
                        FoodItem(
                            name = columns[0],
                            carbohydrate = columns[1].toDouble(),
                            calories = columns[2].toDouble(),
                            estimatedGlucose = columns[3].toDouble()
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return foodList
    }
}