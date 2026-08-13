package com.rukinpavel.wordlyapp.feature.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.Keyboard
import com.rukinpavel.wordlyapp.core.ui.LetterTile
import com.rukinpavel.wordlyapp.core.ui.WordlyTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    GameContent(
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameUiState,
    sideEffect: SharedFlow<GameSideEffect>,
    onEvent: (GameUiEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        sideEffect.collectLatest { effect ->
            when (effect) {
                is GameSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                GameSideEffect.GameFinished -> {
                    // Logic for game finished
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("WORDLY", style = MaterialTheme.typography.headlineLarge) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WordGrid(board = uiState.board)
                }

                Keyboard(
                    letterStates = uiState.keyboardLetterStates,
                    onKeyClick = { onEvent(GameUiEvent.OnKeyClick(it)) },
                    onDeleteClick = { onEvent(GameUiEvent.OnDeleteClick) },
                    onEnterClick = { onEvent(GameUiEvent.OnEnterClick) }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.targetWord.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Failed to load word")
                    Button(onClick = { onEvent(GameUiEvent.OnPlayAgainClick) }) {
                        Text("Retry")
                    }
                }
            }

            if (uiState.gameStatus != GameStatus.PLAYING) {
                AlertDialog(
                    onDismissRequest = { },
                    title = {
                        Text(
                            text = if (uiState.gameStatus == GameStatus.WON) "You Won!" else "Game Over"
                        )
                    },
                    text = {
                        Column {
                            Text("The word was: ${uiState.targetWord}")
                            if (uiState.gameStatus == GameStatus.LOST) {
                                Text("Better luck next time!")
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { onEvent(GameUiEvent.OnPlayAgainClick) }) {
                            Text("Play Again")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WordGrid(
    board: List<List<BoardLetter>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.widthIn(max = 350.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        board.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { letter ->
                    LetterTile(
                        char = letter.char,
                        state = letter.state,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    WordlyTheme {
        GameContent(
            uiState = GameUiState(
                board = List(6) { rowIndex ->
                    if (rowIndex == 0) {
                        listOf(
                            BoardLetter('W', LetterState.CORRECT),
                            BoardLetter('O', LetterState.CORRECT),
                            BoardLetter('R', LetterState.WRONG_POSITION),
                            BoardLetter('D', LetterState.NOT_IN_WORD),
                            BoardLetter('S', LetterState.INITIAL)
                        )
                    } else {
                        List(5) { BoardLetter() }
                    }
                },
                keyboardLetterStates = mapOf(
                    'W' to LetterState.CORRECT,
                    'O' to LetterState.CORRECT,
                    'R' to LetterState.WRONG_POSITION,
                    'D' to LetterState.NOT_IN_WORD
                )
            ),
            sideEffect = MutableSharedFlow(),
            onEvent = {}
        )
    }
}
