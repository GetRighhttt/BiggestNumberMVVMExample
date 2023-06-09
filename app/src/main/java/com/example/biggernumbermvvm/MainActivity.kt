package com.example.biggernumbermvvm

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.biggernumbermvvm.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

    /*
    Companion object used for creating static instances.
     */
    companion object {
        const val TAG = "MAIN"
    }

    /*
    Initializer block. Logs when main activity starts.
     */
    init {
        Log.d(TAG, "${Thread.currentThread().name.uppercase()} initialized.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Binding block to bind our views.
         */
        binding.apply {
            pbMain.visibility = View.GONE

            /*
            On Click listeners for our buttons. We use our view model methods to determine
            the right answer and reset the state of the UI to unknown after.
             */
            btnLeft.setOnClickListener {
                viewModel.determineRightAnswer(
                    btnLeft.text.toString().toInt(),
                    btnRight.text.toString().toInt(),
                    true
                )
                Log.d(TAG, "$btnLeft clicked!")
                viewModel.resetAnswerState()
            }

            btnRight.setOnClickListener {
                viewModel.determineRightAnswer(
                    btnLeft.text.toString().toInt(),
                    btnRight.text.toString().toInt(),
                    false
                )
                Log.d(TAG, "$btnRight clicked!")
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
            lifecycleScope.launch {
                binding.apply {

                    viewModel.answer.flowWithLifecycle(lifecycle).collect {
                        when (it) {
                            is MainViewModel.AnswerState.CorrectAnswer -> {
                                // delay just for demo purposes
                                delay(300L)
                                tvDisplayAnswer.text = it.message
                                pbMain.visibility = View.GONE
                                backgroundSelector.setBackgroundColor(Color.GREEN)
                                materialDialog(
                                    this@MainActivity,
                                    it.message,
                                    "Great Job! You got the answer right!"
                                )
                                Log.d(TAG, "Correct Answer Chosen.")
                            }

                            is MainViewModel.AnswerState.WrongAnswer -> {
                                // delay just for demo purposes
                                delay(300L)
                                tvDisplayAnswer.text = it.message
                                pbMain.visibility = View.GONE
                                backgroundSelector.setBackgroundColor(Color.RED)
                                materialDialog(
                                    this@MainActivity,
                                    it.message,
                                    "Oops! Looks like you got the answer wrong!"
                                )
                                Log.d(TAG, "Wrong Answer Chosen.")
                            }

                            is MainViewModel.AnswerState.UnknownAnswer -> {
                                delay(600L)
                                Log.d(TAG, "Unknown Answer...")
                                assignRandomNumbers()
                            }

                            is MainViewModel.AnswerState.LoadingAnswer -> {
                                pbMain.visibility = View.VISIBLE
                                animateButton(btnLeft)
                                animateButton(btnRight)
                                Log.d(TAG, "Loading Answer...")
                            }
                        }
                    }

                }
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException: ${e.message}")
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

    private fun materialDialog(
        mainActivity: MainActivity,
        titleText: String,
        answerText: String
    ) = object : MaterialAlertDialogBuilder(this) {
        val dialog = MaterialAlertDialogBuilder(mainActivity)
            .setTitle(titleText)
            .setMessage(answerText)
            .setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun animateButton(button: Button) = binding.apply {
        button.animate().apply {
            duration = 500L
            rotationXBy(180F)
        }.withEndAction {
            button.animate().apply {
                duration = 500L
                rotationXBy(-180F)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}