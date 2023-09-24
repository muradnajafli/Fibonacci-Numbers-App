package com.example.fibonaccinumbers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.fibonaccinumbers.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isCancelled = AtomicBoolean(false)
    private var calculationThread: Thread? = null
    private var handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }



    private fun setupUI() {
        binding.button.setOnClickListener {
            if (binding.editTextNumber.text.isEmpty()) {
                Toast.makeText(this, "Please, enter an integer.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isCancelled.get()) {
                startCalculation()
            } else {
                cancelCalculation()
            }
            isCancelled.set(!(isCancelled.get()))
        }
    }
    private fun startCalculation() {
        isCancelled.set(false)
        binding.button.text = "CANCEL"
        binding.editTextNumber.isEnabled = false
        val num = binding.editTextNumber.text.toString().toInt()

        calculationThread = Thread {
            calculateFibonacciSum(num)
        }
        calculationThread?.start()
    }

    private fun cancelCalculation() {
        isCancelled.set(true)
        calculationThread?.interrupt()
        binding.editTextNumber.isEnabled = true
        handler.removeCallbacksAndMessages(null)
        binding.button.text = "START"
        binding.counterText.text = ""
    }


    private fun calculateFibonacciSum(num: Int) {
        var sum = 0

        if (num <= 0) {
            return
        }

        for (i in 1..num) {
            if (Thread.currentThread().isInterrupted) {
                return
            }
            handler.postDelayed({
                binding.counterText.text = "Current is $i"
            }, i * 1000L)
            val currentValue = calculateFibonacci(i)
            sum += currentValue
        }

        handler.postDelayed({
            binding.counterText.text = "Result: $sum"
            binding.button.text = "START"
            binding.editTextNumber.isEnabled = true
            isCancelled.set(false)
        }, (num + 1) * 1000L)
    }

    private fun calculateFibonacci(n: Int): Int {
        return if (n <= 1) {
            n
        } else {
            calculateFibonacci(n - 1) + calculateFibonacci(n - 2)
        }
    }
}