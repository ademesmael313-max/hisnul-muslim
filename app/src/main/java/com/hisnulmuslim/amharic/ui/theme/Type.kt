package com.hisnulmuslim.amharic.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hisnulmuslim.amharic.R

/** Noto Sans Ethiopic (SIL OFL 1.1) keeps Amharic shaping consistent on every device. */
val EthiopicFont = FontFamily(
    Font(R.font.noto_sans_ethiopic, FontWeight.Normal),
    Font(R.font.noto_sans_ethiopic, FontWeight.Medium),
    Font(R.font.noto_sans_ethiopic, FontWeight.SemiBold),
    Font(R.font.noto_sans_ethiopic, FontWeight.Bold),
)

private fun amharic(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = EthiopicFont,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

val AppTypography = Typography(
    headlineMedium = amharic(26, 38, FontWeight.SemiBold),
    headlineSmall = amharic(22, 34, FontWeight.SemiBold),
    titleLarge = amharic(20, 32, FontWeight.SemiBold),
    titleMedium = amharic(17, 28, FontWeight.Medium),
    titleSmall = amharic(15, 24, FontWeight.Medium),
    bodyLarge = amharic(16, 28),
    bodyMedium = amharic(15, 26),
    bodySmall = amharic(13, 22),
    labelLarge = amharic(14, 22, FontWeight.Medium),
    labelMedium = amharic(12, 20, FontWeight.Medium),
    labelSmall = amharic(11, 18, FontWeight.Medium),
)
