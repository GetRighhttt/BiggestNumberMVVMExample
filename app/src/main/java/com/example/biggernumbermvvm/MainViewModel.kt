package com.example.biggernumbermvvm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        /*
        Updates logcat with answer state and sets the initial state to unknown each time it is
        called.
         */
        Log.d("VIEW_MODEL", "View Model initialized on: ${Thread.currentThread().name.uppercase()}.")
        _answer.value = AnswerState.UnknownAnswer
    }

    /*
    Determines right answer. Will be called in btnRight setOnClickListener().
     */
    fun determineRightButtonAnswer(leftNumber: Int, rightNumber: Int) {
        try {
            viewModelScope.launch {
                // sets initial value
                _answer.value = AnswerState.UnknownAnswer

                // destructing our AnswerChoices Data class into two variables -> left, right
                val (left, right) = AnswerChoices(leftNumber, rightNumber)

                if (right > left) {
                    _answer.value = AnswerState.CorrectAnswer(message = "Correct Answer!")
                } else {
                    _answer.value = AnswerState.WrongAnswer(message = "Wrong Answer!")
                }
            }
        } catch (e: Exception) {
            Log.d("VIEW_MODEL", "Error in determineRightButtonAnswer: ${e.message}")
        }
    }

    /*
    Determines right answer. Will be called in btnLeft setOnClickListener().
     */
    fun determineLeftButtonAnswer(leftNumber: Int, rightNumber: Int) {
        try {
            viewModelScope.launch {
                // sets initial value
                _answer.value = AnswerState.UnknownAnswer

                // destructing our AnswerChoices Data class into two variables -> left, right
                val (left, right) = AnswerChoices(leftNumber, rightNumber)

                if (left > right) {
                    _answer.value = AnswerState.CorrectAnswer(message = "Correct Answer!")
                } else {
                    _answer.value = AnswerState.WrongAnswer(message = "Wrong Answer!")
                }
            }
        } catch (e: Exception) {
            Log.d("VIEW_MODEL", "Error in determineLeftButtonAnswer: ${e.message}")
        }
    }

    /*
    Resets answer state to unknown, which allows the buttons to call the random number
    method we created in our main activity.
     */
    fun resetAnswerState() {
        _answer.value = AnswerState.UnknownAnswer
    }

    /*
    Sealed class for different states of answer
    */
    sealed class AnswerState {
        data class CorrectAnswer(val message: String) : AnswerState()
        data class WrongAnswer(val message: String) : AnswerState()
        object UnknownAnswer : AnswerState()
    }
}


