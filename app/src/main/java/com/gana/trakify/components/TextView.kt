package com.gana.trakify.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.gana.trakify.ui.theme.Black

@Composable
fun TextView(
    text: String = "",
    modifier: Modifier = Modifier,
    color: Color = Black,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Center, // Center text alignment
    maxLines: Int = Int.MAX_VALUE
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Center the text in available space
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            maxLines = maxLines,
            modifier = Modifier.fillMaxSize() // Take full available space
        )
    }
}

// Alternative version without Box (simpler)
@Composable
fun SimpleTextView(
    text: String = "",
    modifier: Modifier = Modifier,
    color: Color = Black,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier
    )
}