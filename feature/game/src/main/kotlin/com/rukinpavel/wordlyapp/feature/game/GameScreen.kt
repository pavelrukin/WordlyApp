package com.rukinpavel.wordlyapp.feature.game

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.FloralBackground
import com.rukinpavel.wordlyapp.core.ui.Keyboard
import com.rukinpavel.wordlyapp.core.ui.LetterTile
import com.rukinpavel.wordlyapp.core.ui.WordlyTheme
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GameScreen(
    onSettingsClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    GameContent(
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onEvent = viewModel::onEvent,
        onSettingsClick = onSettingsClick,
        getString = { resId, args -> context.getString(resId, *args.toTypedArray()) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameUiState,
    sideEffect: SharedFlow<GameSideEffect>,
    onEvent: (GameUiEvent) -> Unit,
    onSettingsClick: () -> Unit,
    getString: (Int, List<Any>) -> String
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        sideEffect.collectLatest { effect ->
            when (effect) {
                is GameSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(getString(effect.messageRes, effect.args))
                }
                GameSideEffect.GameFinished -> {
                    // Logic for game finished
                }
            }
        }
    }

    FloralBackground(darkTheme = isSystemInDarkTheme()) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(CoreUiR.string.wordly_title),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.hintCount.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { onEvent(GameUiEvent.OnHintClick) }) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = stringResource(CoreUiR.string.tutorial_hint_title),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(CoreUiR.string.settings),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
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
                        .fillMaxSize(),
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

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                            Keyboard(
                                language = uiState.language,
                                letterStates = uiState.keyboardLetterStates,
                                onKeyClick = { onEvent(GameUiEvent.OnKeyClick(it)) },
                                onDeleteClick = { onEvent(GameUiEvent.OnDeleteClick) },
                                onEnterClick = { onEvent(GameUiEvent.OnEnterClick) },
                                vibrationEnabled = uiState.vibrationEnabled
                            )
                        }
                    }
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
                        Text(stringResource(CoreUiR.string.failed_load_word))
                        Button(
                            onClick = { onEvent(GameUiEvent.OnPlayAgainClick) },
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(stringResource(CoreUiR.string.retry))
                        }
                    }
                }

                if (uiState.showAdDialog) {
                    AlertDialog(
                        onDismissRequest = { onEvent(GameUiEvent.OnDismissAdDialog) },
                        shape = RoundedCornerShape(28.dp),
                        title = { Text(stringResource(CoreUiR.string.out_of_hints)) },
                        text = { Text(stringResource(CoreUiR.string.watch_ad_description)) },
                        confirmButton = {
                            Button(
                                onClick = { onEvent(GameUiEvent.OnWatchAdClick) },
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Text(stringResource(CoreUiR.string.watch_video))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { onEvent(GameUiEvent.OnDismissAdDialog) }) {
                                Text(stringResource(CoreUiR.string.cancel))
                            }
                        }
                    )
                }

                if (uiState.gameStatus != GameStatus.PLAYING) {
                    AlertDialog(
                        onDismissRequest = { },
                        shape = RoundedCornerShape(28.dp),
                        title = {
                            Text(
                                text = if (uiState.gameStatus == GameStatus.WON) stringResource(CoreUiR.string.you_won) else stringResource(CoreUiR.string.game_over)
                            )
                        },
                        text = {
                            Column {
                                Text(stringResource(CoreUiR.string.word_was, uiState.targetWord))
                                if (uiState.gameStatus == GameStatus.LOST) {
                                    Text(stringResource(CoreUiR.string.better_luck))
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { onEvent(GameUiEvent.OnPlayAgainClick) },
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Text(stringResource(CoreUiR.string.play_again))
                            }
                        }
                    )
                }
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
            onEvent = {},
            onSettingsClick = {},
            getString = { _, _ -> "" }
        )
    }
}
