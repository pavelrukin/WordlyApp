package com.rukinpavel.wordlyapp.feature.game

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rukinpavel.wordlyapp.core.model.LetterState
import com.rukinpavel.wordlyapp.core.ui.FloralBackground
import com.rukinpavel.wordlyapp.core.ui.Keyboard
import com.rukinpavel.wordlyapp.core.ui.LetterTile
import com.rukinpavel.wordlyapp.core.ui.LocalLocalizedContext
import com.rukinpavel.wordlyapp.core.ui.WordlyDialog
import com.rukinpavel.wordlyapp.core.ui.WordlyDialogButton
import com.rukinpavel.wordlyapp.core.ui.WordlyTheme
import com.rukinpavel.wordlyapp.core.ui.localizedString
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR

@Composable
fun GameScreen(
    onSettingsClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    GameContent(
        uiState = uiState,
        sideEffect = viewModel.sideEffect,
        onEvent = viewModel::onEvent,
        onSettingsClick = onSettingsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    uiState: GameUiState,
    sideEffect: SharedFlow<GameSideEffect>,
    onEvent: (GameUiEvent) -> Unit,
    onSettingsClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val localizedContext = LocalLocalizedContext.current

    LaunchedEffect(Unit) {
        sideEffect.collectLatest { effect ->
            when (effect) {
                is GameSideEffect.ShowError -> {
                    val message = if (effect.args.isEmpty()) {
                        localizedContext.getString(effect.messageRes)
                    } else {
                        localizedContext.getString(effect.messageRes, *effect.args.toTypedArray())
                    }
                    snackbarHostState.showSnackbar(message)
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
                            localizedString(CoreUiR.string.wordly_title),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (uiState.isPremium) "∞" else uiState.hintCount.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (uiState.isPremium) 20.sp else 16.sp
                            )
                            IconButton(onClick = { onEvent(GameUiEvent.OnHintClick) }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Lightbulb,
                                    contentDescription = localizedString(CoreUiR.string.tutorial_hint_title),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                                contentDescription = localizedString(CoreUiR.string.settings),
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
                        Text(localizedString(CoreUiR.string.failed_load_word))
                        Button(
                            onClick = { onEvent(GameUiEvent.OnPlayAgainClick) },
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(localizedString(CoreUiR.string.retry))
                        }
                    }
                }

                if (uiState.showAdDialog) {
                    WordlyDialog(
                        onDismissRequest = { onEvent(GameUiEvent.OnDismissAdDialog) },
                        title = localizedString(CoreUiR.string.out_of_hints),
                        icon = androidx.compose.material.icons.Icons.Default.Info,
                        text = {
                            Text(
                                text = localizedString(CoreUiR.string.watch_ad_description),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        confirmButton = {
                            WordlyDialogButton(
                                onClick = { onEvent(GameUiEvent.OnWatchAdClick) },
                                text = localizedString(CoreUiR.string.watch_video)
                            )
                        },
                        dismissButton = {
                            WordlyDialogButton(
                                onClick = { onEvent(GameUiEvent.OnDismissAdDialog) },
                                text = localizedString(CoreUiR.string.cancel),
                                isPrimary = false
                            )
                        }
                    )
                }

                if (uiState.gameStatus != GameStatus.PLAYING) {
                    val isWon = uiState.gameStatus == GameStatus.WON
                    WordlyDialog(
                        onDismissRequest = { },
                        title = if (isWon) localizedString(CoreUiR.string.you_won) else localizedString(CoreUiR.string.game_over),
                        icon = if (isWon) androidx.compose.material.icons.Icons.Default.AutoAwesome else androidx.compose.material.icons.Icons.Default.SentimentDissatisfied,
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = localizedString(CoreUiR.string.word_was, uiState.targetWord),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (!isWon) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = localizedString(CoreUiR.string.better_luck),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            WordlyDialogButton(
                                onClick = { onEvent(GameUiEvent.OnPlayAgainClick) },
                                text = localizedString(CoreUiR.string.play_again)
                            )
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
            onSettingsClick = {}
        )
    }
}
