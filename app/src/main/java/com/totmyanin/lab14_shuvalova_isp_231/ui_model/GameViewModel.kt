package com.totmyanin.lab14_shuvalova_isp_231.ui_model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.totmyanin.lab14_shuvalova_isp_231.data.GameUiState
import com.totmyanin.lab14_shuvalova_isp_231.data.allWords
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.totmyanin.lab14_shuvalova_isp_231.data.MAX_NO_OF_WORDS
import com.totmyanin.lab14_shuvalova_isp_231.data.SCORE_INCREASE
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle()
        )
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }
    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        while (usedWords.contains(currentWord)) {
            currentWord = allWords.random()
        }
        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }
    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    private fun updateGameStae(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update {
                currentState -> currentState.copy (
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        }
        else
        {
            _uiState.update {
                currentState -> currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount + 1
                )
            }
        }
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score + SCORE_INCREASE
            updateGameStae(updatedScore)
        } else {
            _uiState.update { currentState -> currentState.copy(isGuessedWordWrong = true) }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameStae(_uiState.value.score)
        updateUserGuess("")
    }
}
