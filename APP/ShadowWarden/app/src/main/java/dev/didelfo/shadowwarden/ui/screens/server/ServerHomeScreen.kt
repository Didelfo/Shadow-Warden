package dev.didelfo.shadowwarden.ui.screens.server

import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.utils.GridItem
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.animateFloatAsState
import kotlin.math.roundToInt

@Composable
fun ServerHomeScreen(navController: NavHostController) {
    val isEditing = remember { mutableStateOf(false) }
    val items = remember {
        mutableStateListOf(
            GridItem(Icons.Default.Home, "Inicio"),
            GridItem(Icons.Default.Person, "Perfil"),
            GridItem(Icons.Default.Settings, "Ajustes"),
            GridItem(Icons.Default.Info, "Acerca de"),
            GridItem(Icons.Default.Phone, "Contacto"),
            GridItem(Icons.Default.Email, "Correo"),
            GridItem(Icons.Default.Star, "Favoritos"),
            GridItem(Icons.Default.ShoppingCart, "Carrito")
        )
    }

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var proposedTargetIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            ToolBarNewHome(
                title = "",
                { navController.navigate(AppScreens.HomeScreen.route) },
                {},
                { isEditing.value = !isEditing.value }
            )
        },
        content = { paddingValues ->
            val itemSizeDp = 80.dp
            val itemSizePx = with(LocalDensity.current) { itemSizeDp.toPx() }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                itemsIndexed(items) { index, item ->
                    val isBeingDragged = draggingIndex == index
                    val animatedOffsetX by animateFloatAsState(if (isBeingDragged) offsetX else 0f, label = "")
                    val animatedOffsetY by animateFloatAsState(if (isBeingDragged) offsetY else 0f, label = "")

                    GridItemCard(
                        item = item,
                        isEditing = isEditing.value,
                        isBeingDragged = isBeingDragged,
                        onClick = {
                            if (!isEditing.value) {
                                println("Item clicado: ${item.text}")
                            }
                        },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .pointerInput(isEditing.value) {
                                if (!isEditing.value) return@pointerInput

                                detectDragGestures(
                                    onDragStart = { draggingIndex = index },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y

                                        proposedTargetIndex = detectTargetIndex(
                                            listSize = items.size,
                                            fromIndex = draggingIndex!!,
                                            offsetX = offsetX,
                                            offsetY = offsetY,
                                            columns = 4,
                                            itemSizePx = itemSizePx
                                        )
                                    },
                                    onDragEnd = {
                                        if (proposedTargetIndex != null && proposedTargetIndex != draggingIndex) {
                                            items.swap(draggingIndex!!, proposedTargetIndex!!)
                                        }
                                        offsetX = 0f
                                        offsetY = 0f
                                        draggingIndex = null
                                        proposedTargetIndex = null
                                    },
                                    onDragCancel = {
                                        offsetX = 0f
                                        offsetY = 0f
                                        draggingIndex = null
                                        proposedTargetIndex = null
                                    }
                                )
                            }
                            .graphicsLayer {
                                translationX = animatedOffsetX
                                translationY = animatedOffsetY
                                scaleX = if (isBeingDragged) 1.05f else 1f
                                scaleY = if (isBeingDragged) 1.05f else 1f
                                shadowElevation = if (isBeingDragged) 8f else 0f
                            }
                    )
                }
            }
        }
    )
}


// -----------------------------------------------------------
//                   Funciones logicas
// -----------------------------------------------------------

fun <T> MutableList<T>.swap(from: Int, to: Int) {
    if (from in indices && to in indices) {
        val temp = this[from]
        this[from] = this[to]
        this[to] = temp
    }
}

fun detectTargetIndex(
    listSize: Int,
    fromIndex: Int,
    offsetX: Float,
    offsetY: Float,
    columns: Int,
    itemSizePx: Float
): Int? {
    val dx = (offsetX / itemSizePx).roundToInt()
    val dy = (offsetY / itemSizePx).roundToInt()
    val row = fromIndex / columns
    val col = fromIndex % columns
    val targetRow = row + dy
    val targetCol = col + dx
    val targetIndex = targetRow * columns + targetCol
    return if (targetIndex in 0 until listSize) targetIndex else null
}


// -----------------------------------------------------------
//                   Funciones composables
// -----------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBarNewHome(
    title: String,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onEditClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = VerdeMenta,
                fontFamily = OpenSanBold,
                fontSize = 26.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AzulVerdosoOscuro
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Atrás",
                    tint = VerdeMenta
                )
            }
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    painter = painterResource(R.drawable.reload),
                    contentDescription = "Refrescar",
                    tint = VerdeMenta
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = "Editar",
                    tint = VerdeMenta
                )
            }
        }
    )
}

@Composable
fun GridItemCard(
    item: GridItem,
    isEditing: Boolean,
    isBeingDragged: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .clickable(enabled = !isEditing, onClick = onClick)
            .background(
                color = if (isEditing) Color(0xFFEDE7F6) else Color(0xFFFFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isBeingDragged) 3.dp else if (isEditing) 2.dp else 1.dp,
                color = when {
                    isBeingDragged -> Color.Red
                    isEditing -> Color(0xFF7E57C2)
                    else -> Color.LightGray
                },
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.text,
                tint = if (isEditing) Color(0xFF512DA8) else Color(0xFF3F51B5),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            if (isEditing) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    painter = painterResource(R.drawable.reload),
                    contentDescription = "Arrastrar",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
