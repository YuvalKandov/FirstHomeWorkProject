package com.example.firsthomeworkproject.logic

import com.example.firsthomeworkproject.utilities.Constants
import kotlin.random.Random

class GameManager {

    private val rows = Constants.Game.ROWS
    private val lanes = Constants.Game.LANES

    private val obstacles: Array<BooleanArray> =
        Array(rows) { BooleanArray(lanes) { false } }

    var lives: Int = Constants.Game.START_LIVES
        private set

    var carLane: Int = 1
        private set

    fun moveCarLeft() {
        if (carLane > 0) {
            carLane--
        }
    }

    fun moveCarRight() {
        if (carLane < lanes - 1) {
            carLane++
        }
    }


    fun tick(): Boolean {
        var crash = false

        // Check collision on bottom row before shifting
        if (obstacles[rows - 1][carLane]) {
            crash = true
            lives--
            clearBoard()
            return true
        }

        // Move obstacles down
        for (row in rows - 1 downTo 1) {
            for (lane in 0 until lanes) {
                obstacles[row][lane] = obstacles[row - 1][lane]
            }
        }

        // Clear top row
        for (lane in 0 until lanes) {
            obstacles[0][lane] = false
        }

        // Spawn new obstacle (max one)
        val spawnLane = Random.nextInt(lanes)
        obstacles[0][spawnLane] = true

        return crash
    }

    fun getObstacle(row: Int, lane: Int): Boolean {
        return obstacles[row][lane]
    }

    fun isDead(): Boolean {
        return lives <= 0
    }

    fun resetGame() {
        lives = Constants.Game.START_LIVES
        carLane = 1
        clearBoard()
    }

    fun resetRound() {
        carLane = 1
        clearBoard()
    }

    private fun clearBoard() {
        for (row in 0 until rows) {
            for (lane in 0 until lanes) {
                obstacles[row][lane] = false
            }
        }
    }
}