package dev.didelfo.shadowwarden.ui.screens.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.didelfo.shadowwarden.ui.theme.AzulOscuroProfundo
import dev.didelfo.shadowwarden.ui.theme.Cian

// Vista de carga
@Composable
fun loadingView(isLoading:Boolean){
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulOscuroProfundo)
                .clickable(enabled = false) {}
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Cian
            )
        }
    }
}
