package com.ehan.app4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import com.ehan.app4.ui.theme.App4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App4Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: CalculatorViewModel = viewModel()) {
    CalculatorScreen(viewModel)
    // Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
    //     Surface(
    //         modifier = modifier
    //             .fillMaxSize()
    //             .padding(innerPadding)
    //     ) {
    //         Box {
    //             Text(
    //                 text = stringResource(R.string.app_name),
    //                 style = MaterialTheme.typography.headlineMedium,
    //             )
    //         }
    //     }
    // }
}

private val PageBackground = Color(0xFFF3F4FF)
private val Ink = Color(0xFF26375A)
private val MutedInk = Color(0xFF8791AE)
private val NumberKey = Color(0xFFE8E9FA)
private val FunctionKey = Color(0xFFD8F0EA)
private val OperatorKey = Color(0xFFD9EBF7)
private val EqualsKey = Color(0xFF5CB7A5)
private val InactiveGray = Color(0xFF777777)
private val ActiveOrange = Color(0xFFFF7043)

@Composable
private fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var expression by rememberSaveable { mutableStateOf("") }
    var lastWasResult by rememberSaveable { mutableStateOf(false) }
    val topBarChange by viewModel.topBarChange.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
    val preview = remember(expression) { CalculatorEngine.evaluate(expression) }

    fun enterDigit(digit: String) {
        if (lastWasResult) {
            expression = ""
            lastWasResult = false
        }
        expression += digit
    }

    fun enterDecimalPoint() {
        if (lastWasResult) {
            expression = ""
            lastWasResult = false
        }
        val currentNumberStart = expression.indexOfLast { it in "+*/-" } + 1
        val currentNumber = expression.substring(currentNumberStart)
        if ('.' !in currentNumber) {
            expression += if (currentNumber.isEmpty()) "0." else "."
        }
    }

    fun enterOperator(operator: String) {
        if (expression.isEmpty()) {
            if (operator == "-") expression = "-"
            lastWasResult = false
            return
        }

        val last = expression.last()
        if (last in "+-*/") {
            val previousIsOperator = expression.length > 1 &&
                expression[expression.lastIndex - 1] in "+-*/"

            expression = when {
                operator == "-" && last != '-' -> expression + operator
                last == '-' && previousIsOperator ->
                    expression.dropLast(2) + operator
                else -> expression.dropLast(1) + operator
            }
        } else {
            expression += operator
        }
        lastWasResult = false
    }

    fun toggleSign() {
        if (expression.isEmpty()) {
            expression = "-"
            lastWasResult = false
            return
        }

        var start = 0
        for (index in expression.lastIndex downTo 0) {
            val character = expression[index]
            if (character == '+' || character == '*' || character == '/') {
                start = index + 1
                break
            }
            if (character == '-') {
                val isUnary = index == 0 || expression[index - 1] in "+-*/"
                if (!isUnary) {
                    start = index + 1
                    break
                }
            }
        }

        val currentNumber = expression.substring(start)
        expression = if (currentNumber.startsWith("-")) {
            expression.removeRange(start, start + 1)
        } else {
            expression.substring(0, start) + "-" + currentNumber
        }
        lastWasResult = false
    }

    fun press(key: String) {
        when (key) {
            "AC" -> {
                expression = ""
                lastWasResult = false
            }
            "±" -> toggleSign()
            "%" -> {
                if (expression.lastOrNull()?.isDigit() == true ||
                    expression.lastOrNull() == '.'
                ) {
                    expression += "%"
                    lastWasResult = false
                }
            }
            "÷" -> enterOperator("/")
            "×" -> enterOperator("*")
            "−" -> enterOperator("-")
            "+" -> enterOperator("+")
            "." -> enterDecimalPoint()
            "=" -> {
                preview?.let { result ->
                    expression = CalculatorEngine.formatNumber(result)
                    lastWasResult = true
                }
            }
            else -> if (key.all(Char::isDigit)) enterDigit(key)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PageBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Header(
                topBarChange = topBarChange,
                topBarChangeSet = { topBarChangeNew ->
                    viewModel.topBarChangeSet(topBarChangeNew)
                }
            )

            when (topBarChange) {
                TopBarList.MAIN -> {
                    DisplayPanel(
                        modifier = Modifier.weight(0.34f),
                        topBarChange = TopBarList.MAIN,
                        expression = expression,
                        preview = preview,
                        onBackspace =  {
                            if (expression.isNotEmpty()) {
                                expression = expression.dropLast(1)
                                lastWasResult = false
                            }
                        }
                    )

                    Keypad(
                        modifier = Modifier.weight(0.66f),
                        onKeyPress = ::press
                    )
                } else -> {
                    Text("Belum Diatur")
                }
            }
        }
    }
}

@Composable
private fun Header(
    topBarChange: TopBarList,
    topBarChangeSet: (TopBarList) -> Unit
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs<List<TopBarList>> = listOf(
                TopBarList.MAIN,
                TopBarList.UANG,
                TopBarList.LAINNYA
            )

            tabs.forEachIndexed { index, topBar ->
            
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            topBarChangeSet(topBar)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = topBar.icon,
                        contentDescription = "Tab $index",
                        tint = if (topBarChange == topBar) {
                            ActiveOrange
                        } else {
                            InactiveGray
                        },
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
    }
    @Composable
    fun a() {
        
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (topBarChange == TopBarList.MAIN) {
                Button(
                    onClick = {
                    }
                ) {
                    Text(
                        text = "More",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (topBarChange != TopBarList.MAIN) {
                Surface(
                    modifier = Modifier
                        .padding(start = 11.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    shape = RoundedCornerShape(15.dp),
                    color = Color(0xFFD7EDEB)
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            topBarChangeSet(TopBarList.MAIN)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Calculate,
                                contentDescription = null,
                                tint = Color(0xFF388B83)
                            )
                        }
                    }
                }
            }
            if (topBarChange != TopBarList.LAINNYA) {
                Button(
                    onClick = {
                        topBarChangeSet(TopBarList.LAINNYA)
                    }
                ) {
                    Text("Lainnya")
                }
            }
            /**
            Column(modifier = Modifier.padding(start = 11.dp)) {
                Text(
                    text = "Kalkulator",
                    color = Ink,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "HITUNG DENGAN MUDAH",
                    color = MutedInk,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            }*/
        }
/**
        Surface(
            shape = CircleShape,
            color = Color(0xFFE5E3F7)
        ) {
            Text(
                text = "STANDAR",
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                color = Color(0xFF67689A),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }*/
    }
    }
}

@Composable
private fun DisplayPanel(
    modifier: Modifier = Modifier,
    topBarChange: TopBarList,
    expression: String,
    preview: Double?,
    onBackspace: () -> Unit
) {
    when (topBarChange) {
        TopBarList.MAIN -> {
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFE6EAF8),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                 /** Text(
                    text = "HASIL SAAT INI",
                    color = MutedInk,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                */
                        IconButton(onClick = onBackspace) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Hapus karakter terakhir",
                                tint = Color(0xFF71809F)
                            )
                        }
                    }

                    Text(
                        text = expression.toDisplayExpression().ifEmpty { "0" },
                        modifier = Modifier.fillMaxWidth(),
                        color = Ink,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = preview?.let { CalculatorEngine.formatNumber(it) }
                            ?: if (expression.isEmpty()) "0" else "—",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF438F91),
                        fontSize = 39.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        else -> {
            Text("Belum Diatur")
        }
    }
}

@Composable
private fun Keypad(
    modifier: Modifier = Modifier,
    onKeyPress: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        KeyRow {
            CalculatorKey("AC", Modifier.weight(1f), KeyStyle.Function, onKeyPress)
            CalculatorKey("±", Modifier.weight(1f), KeyStyle.Function, onKeyPress)
            CalculatorKey("%", Modifier.weight(1f), KeyStyle.Function, onKeyPress)
            CalculatorKey("÷", Modifier.weight(1f), KeyStyle.Operator, onKeyPress)
        }
        KeyRow {
            CalculatorKey("7", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("8", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("9", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("×", Modifier.weight(1f), KeyStyle.Operator, onKeyPress)
        }
        KeyRow {
            CalculatorKey("4", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("5", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("6", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("−", Modifier.weight(1f), KeyStyle.Operator, onKeyPress)
        }
        KeyRow {
            CalculatorKey("1", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("2", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("3", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("+", Modifier.weight(1f), KeyStyle.Operator, onKeyPress)
        }
        KeyRow {
            CalculatorKey("00", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("0", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey(".", Modifier.weight(1f), KeyStyle.Number, onKeyPress)
            CalculatorKey("=", Modifier.weight(1f), KeyStyle.Equals, onKeyPress)
        }
    }
}

@Composable
private fun ColumnScope.KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        content()
    }
}

private enum class KeyStyle {
    Number,
    Function,
    Operator,
    Equals
}

enum class TopBarList(val icon: ImageVector) {
    MAIN(Icons.Outlined.Calculate),
    UANG(Icons.AutoMirrored.Filled.Backspace),
    LAINNYA(Icons.Outlined.Calculate);
}

@Composable
private fun CalculatorKey(
    label: String,
    modifier: Modifier,
    style: KeyStyle,
    onKeyPress: (String) -> Unit
) {
    val background = when (style) {
        KeyStyle.Number -> NumberKey
        KeyStyle.Function -> FunctionKey
        KeyStyle.Operator -> OperatorKey
        KeyStyle.Equals -> EqualsKey
    }
    val foreground = if (style == KeyStyle.Equals) Color.White else Ink

    Surface(
        onClick = { onKeyPress(label) },
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(21.dp),
        color = background,
        contentColor = foreground,
        shadowElevation = if (style == KeyStyle.Equals) 3.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = foreground,
                fontSize = if (label.length > 1) 19.sp else 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(code: Int = 1) {
    val viewModel: CalculatorViewModel = viewModel()
    when (code) {
        2 -> {
            App4Theme(
                darkTheme = true
            ) {
                MainScreen(viewModel = viewModel)
            }
        }
        3 -> {
            App4Theme(
                darkTheme = false
            ) {
                viewModel.topBarChangeSet(TopBarList.LAINNYA)
                MainScreen(viewModel = viewModel)
            }
        }
        4 -> {
            App4Theme(
                darkTheme = true
            ) {
                viewModel.topBarChangeSet(TopBarList.LAINNYA)
                MainScreen(viewModel = viewModel)
            }
        }
        else -> {
            App4Theme(
                darkTheme = false
            ) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

private fun String.toDisplayExpression(): String =
    replace("*", "×")
        .replace("/", "÷")
        .replace("-", "−")