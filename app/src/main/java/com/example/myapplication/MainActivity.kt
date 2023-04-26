package com.example.myapplication

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //addProgressButton()
        addPressProgressButton()
    }

    fun addProgressButton() {
        val widthDp = 100 * (160 / resources.displayMetrics.densityDpi)
        val layoutParams =
            ConstraintLayout.LayoutParams(widthDp, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }

        val pressButton = ProgressButton(context = this)

        pressButton.setCompleteListener {
            Toast.makeText(this, "타이머가 종료되었습니다.", Toast.LENGTH_SHORT).show()
        }
        pressButton.setOnClickListener {
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                repeat(33) {
                    pressButton.setProgress(it * 3f)
                    delay(1000L)
                }
                pressButton.setProgress(100f)

                pressButton.complete()
            }

            Toast.makeText(this, "타이머가 시작됩니다.", Toast.LENGTH_SHORT).show()
        }

        binding.root.setOnClickListener {
            pressButton.clearProgress()
        }

        binding.layoutRoot.addView(
            pressButton, layoutParams
        )
    }

    fun addPressProgressButton() {
        val widthDp = 100 * (160 / resources.displayMetrics.densityDpi)
        val layoutParams =
            ConstraintLayout.LayoutParams(widthDp, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }

        val pressButton = PressProgressButton(context = this)
        pressButton.setOnTouchListener { v, event ->
            v.performClick()
            return@setOnTouchListener v.onTouchEvent(event)
        }
        pressButton.setCompleteListener {
            Toast.makeText(this, "타이머가 종료되었습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.layoutRoot.addView(
            pressButton, layoutParams
        )
    }
}