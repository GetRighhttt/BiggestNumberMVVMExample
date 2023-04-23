package com.example.biggernumbermvvm

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.biggernumbermvvm.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import java.util.Random

class MainActivity : AppCompatActivity() {

    /*
    Variables needed to bind views and get a reference to our view model.
     */
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    /*
    Using delegation to delegate our view model responsibilities to a variable
     */
    private val viewModel: MainViewModel by viewModels()

    companion object {
        private fun materialDialog(mainActivity: MainActivity, answerText: String) {
            MaterialAlertDialogBuilder(mainActivity)
                .setTitle(answerText)
                .setMessage("Press OK to continue.")
                .setPositiveButton("OK") { _, _ -> }
                .show()
        }
    }

    /*
    Initializer block. Logs when main activity starts.
     */
    init {
        Log.d("MAIN", "${Thread.currentThread().name.uppercase()} initialized.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Binding block to bind our views.
         */
        binding.apply {

            /*
            On Click listeners for our buttons. We use our view model methods to determine
            the right answer and reset the state of the UI to unknown after.
             */
            btnLeft.setOnClickListener {
                viewModel.determineLeftButtonAnswer(
                    btnLeft.text.toString().toInt(),
                    btnRight.text.toString().toInt()
                )
                Log.d("MAIN", "$btnLeft clicked!")
                viewModel.resetAnswerState()
            }

            btnRight.setOnClickListener {
                viewModel.determineRightButtonAnswer(
                    btnLeft.text.toString().toInt(),
                    btnRight.text.toString().toInt()
                )
                Log.d("MAIN", "$btnRight clicked!")
                viewModel.resetAnswerState()
            }
        }
        /*
        Updates views based on the state of the answer from our view model.
         */
        determineAnswerState()
    }

    /*
    Sets the state of the answer using our sealed class Answer State we created in our view model.
    Also sets the message to the display textview, and shows a Snack bar with the message also.
     */
    private fun determineAnswerState() {
        try {
            lifecycleScope.launchWhenStarted {
                binding.apply {

                    viewModel.answer.collect {
                        when (it) {
                            is MainViewModel.AnswerState.CorrectAnswer -> {
                                // delay just for demo purposes
                                delay(300L)
                                tvDisplayAnswer.text = it.message
                                backgroundSelector.setBackgroundColor(Color.GREEN)
                                materialDialog(this@MainActivity, it.message)
                                Log.d("MAIN", "Correct Answer Chosen.")
                            }

                            is MainViewModel.AnswerState.WrongAnswer -> {
                                // delay just for demo purposes
                                delay(300L)
                                tvDisplayAnswer.text = it.message
                                backgroundSelector.setBackgroundColor(Color.RED)
                                materialDialog(this@MainActivity, it.message)
                                Log.d("MAIN", "Wrong Answer Chosen.")
                            }

                            is MainViewModel.AnswerState.UnknownAnswer -> {
                                Log.d("MAIN", "Unknown Answer...")
                                // when in unknown, assign random numbers
                                assignRandomNumbers()
                            }
                        }
                    }

                }
            }
        } catch (e: Exception) {
            Log.e("MAIN", "Error in determineAnswerState: ${e.message}")
        }
    }

    private fun assignRandomNumbers() {
        val r = Random()
        val leftNumber = r.nextInt(30)

        // while they are equal, set right num to random number.
        var rightNumber = leftNumber
        while (rightNumber == leftNumber) {
            rightNumber = r.nextInt(30)
        }

        binding.apply {
            btnLeft.text = leftNumber.toString()
            btnRight.text = rightNumber.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}