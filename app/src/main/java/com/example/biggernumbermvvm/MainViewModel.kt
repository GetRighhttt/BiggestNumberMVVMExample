package com.example.biggernumbermvvm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    /*
    MutableStateFlow that will be used to hold the state of our answer and update its value
    to state flow.
     */
    private val _answer = MutableStateFlow<AnswerState>(AnswerState.UnknownAnswer)
    val answer: StateFlow<AnswerState> get() = _answer

    companion object {
        const val TAG = "VIEW_MODEL"
    }

    init {
        /*
        Updates logcat with answer state and sets the initial state to unknown each time it is
        called.
         */
        Log.d(
            TAG,
            "View Model initialized on: ${Thread.currentThread().name.uppercase()}."
        )
        _answer.value = AnswerState.UnknownAnswer
    }

    /*
    Determines right answer. Will be called in btnRight setOnClickListener().
     */
    fun determineRightAnswer(leftNumber: Int, rightNumber: Int, isLeftSelected: Boolean) {
        // sets initial value
        _answer.value = AnswerState.LoadingAnswer

        try {
            viewModelScope.launch {
                // delay to allow for progress bar to show
                delay(500L)

                // destructing our AnswerChoices Data class into two variables -> left, right
                val (left, right) = AnswerChoices(leftNumber, rightNumber)

                when (isLeftSelected) {
                    true -> {
                        if (left > right) {
                            _answer.value = AnswerState.CorrectAnswer(message = "Correct Answer!")
                        } else {
                            _answer.value = AnswerState.WrongAnswer(message = "Wrong Answer!")
                        }
                    }

                    false -> {
                        if (right > left) {
                            _answer.value = AnswerState.CorrectAnswer(message = "Correct Answer!")
                        } else {
                            _answer.value = AnswerState.WrongAnswer(message = "Wrong Answer!")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(
                TAG,
                "Error in determineRightButtonAnswer viewModelScope: ${e.message}"
            )
        }
    }

    /*
    Resets answer state to unknown, which allows the buttons to call the random number
    method we created in our main activity.
     */
    fun resetAnswerState() {
        _answer.value = AnswerState.UnknownAnswer
        Log.d(TAG, "Empty State set...")
    }

    /*
    Sealed class for different states of answer
    */
    sealed class AnswerState {
        data class CorrectAnswer(val message: String) : AnswerState()
        data class WrongAnswer(val message: String) : AnswerState()
        object LoadingAnswer : AnswerState()
        object UnknownAnswer : AnswerState()
    }
}



