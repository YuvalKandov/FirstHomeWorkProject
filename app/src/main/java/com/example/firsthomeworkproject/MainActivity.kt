package com.example.firsthomeworkproject

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.firsthomeworkproject.logic.GameManager
import com.example.firsthomeworkproject.utilities.Constants
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var main_BTN_left: Button
    private lateinit var main_BTN_right: Button
    private lateinit var main_IMG_backround: AppCompatImageView

    private lateinit var heartViews: Array<ImageView>
    private lateinit var carViews: Array<ImageView>
    private lateinit var gridViews: Array<Array<ImageView>>

    private lateinit var gameManager: GameManager

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameManager = GameManager()

        findViews()
        initViews()

        startTimer()
    }

    private fun findViews() {
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_IMG_backround = findViewById(R.id.main_IMG_backround)

        heartViews = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )

        carViews = arrayOf(
            findViewById(R.id.main_IMG_car_0),
            findViewById(R.id.main_IMG_car_1),
            findViewById(R.id.main_IMG_car_2)
        )

        gridViews = arrayOf(
            arrayOf(
                findViewById(R.id.main_IMG_cell_0_0),
                findViewById(R.id.main_IMG_cell_0_1),
                findViewById(R.id.main_IMG_cell_0_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_cell_1_0),
                findViewById(R.id.main_IMG_cell_1_1),
                findViewById(R.id.main_IMG_cell_1_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_cell_2_0),
                findViewById(R.id.main_IMG_cell_2_1),
                findViewById(R.id.main_IMG_cell_2_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_cell_3_0),
                findViewById(R.id.main_IMG_cell_3_1),
                findViewById(R.id.main_IMG_cell_3_2)
            ),
            arrayOf(
                findViewById(R.id.main_IMG_cell_4_0),
                findViewById(R.id.main_IMG_cell_4_1),
                findViewById(R.id.main_IMG_cell_4_2)
            )
        )
    }

    private fun initViews() {
        main_BTN_left.setOnClickListener {
            gameManager.moveCarLeft()
            refreshCarUI()
        }

        main_BTN_right.setOnClickListener {
            gameManager.moveCarRight()
            refreshCarUI()
        }

        val url = "https://images.pexels.com/photos/956981/milky-way-starry-sky-night-sky-star-956981.jpeg"

        Glide
            .with(this)
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .into(main_IMG_backround);

        refreshLivesUI()
        refreshCarUI()
        refreshGridUI()
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()

        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val crashed = gameManager.tick()
                    refreshGridUI()

                    if (crashed) {

                        if (gameManager.isDead()) {
                            showDeathToast()
                            gameManager.resetGame()
                        } else {
                            showCrashToast()
                            gameManager.resetRound()
                        }

                        refreshLivesUI()
                        refreshCarUI()
                        refreshGridUI()
                    }
                }
            }
        }, 0, Constants.Game.DELAY)
    }

    private fun refreshCarUI() {
        for (i in carViews.indices) {
            carViews[i].visibility =
                if (i == gameManager.carLane) ImageView.VISIBLE else ImageView.INVISIBLE
        }
    }

    private fun refreshGridUI() {
        for (row in 0 until Constants.Game.ROWS) {
            for (lane in 0 until Constants.Game.LANES) {
                gridViews[row][lane].visibility =
                    if (gameManager.getObstacle(row, lane)) ImageView.VISIBLE else ImageView.INVISIBLE
            }
        }
    }

    private fun refreshLivesUI() {
        for (i in heartViews.indices) {
            heartViews[i].visibility =
                if (i < gameManager.lives) ImageView.VISIBLE else ImageView.INVISIBLE
        }
    }

    private fun showCrashToast() {
        Toast.makeText(this, getString(R.string.crash_toast), Toast.LENGTH_SHORT).show()
        vibrateOnce()
    }

    private fun showDeathToast() {
        Toast.makeText(this, getString(R.string.death_toast), Toast.LENGTH_SHORT).show()
        vibrateOnce()
    }

    private fun vibrateOnce() {
        val vibrator = getSystemService(Vibrator::class.java)
        vibrator?.vibrate(
            VibrationEffect.createOneShot(
                200,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }
}