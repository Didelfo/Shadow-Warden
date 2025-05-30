package dev.didelfo.shadowwarden.ui.screens.server.serverhome

import dev.didelfo.shadowwarden.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import dev.didelfo.shadowwarden.ui.theme.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.platform.LocalContext
import dev.didelfo.shadowwarden.ui.screens.server.serverhome.ServerHomeScreenViewModel
import kotlin.math.roundToInt

@Composable
fun ServerHomeScreen(navController: NavHostController, permissions: List<String>) {

    var viewModel: ServerHomeScreenViewModel = ServerHomeScreenViewModel(LocalContext.current, navController)


    var permissionss by remember {mutableStateOf(permissions)}
    val filteredItems = viewModel.allItems
        .filter { item -> permissionss.contains(item.id) }
        .toMutableList()


    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var proposedTargetIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            ToolBar(
                title = "Opciones",
                { navController.navigate(AppScreens.HomeScreen.route) },
                { viewModel.isEditing = !viewModel.isEditing},
                viewModel
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
                itemsIndexed(filteredItems) { index, item ->
                    val isBeingDragged = draggingIndex == index
                    val animatedOffsetX by animateFloatAsState(if (isBeingDragged) offsetX else 0f, label = "")
                    val animatedOffsetY by animateFloatAsState(if (isBeingDragged) offsetY else 0f, label = "")

                    GridItemCard(
                        item = item,
                        isEditing = viewModel.isEditing,
                        isBeingDragged = isBeingDragged,
                        onClick = {
                            if (!viewModel.isEditing) {
                                viewModel.clickItem(item)
                            }
                        },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .pointerInput(viewModel.isEditing) {
                                if (!viewModel.isEditing) return@pointerInput

                                detectDragGestures(
                                    onDragStart = { draggingIndex = index },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y

                                        proposedTargetIndex = detectTargetIndex(
                                            listSize = filteredItems.size,
                                            fromIndex = draggingIndex!!,
                                            offsetX = offsetX,
                                            offsetY = offsetY,
                                            columns = 4,
                                            itemSizePx = itemSizePx
                                        )
                                    },
                                    onDragEnd = {
                                        if (proposedTargetIndex != null && proposedTargetIndex != draggingIndex) {
                                            filteredItems.swap(draggingIndex!!, proposedTargetIndex!!)
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
private fun ToolBar(
    title: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: ServerHomeScreenViewModel
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
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = if (viewModel.isEditing) {
                        painterResource(R.drawable.save)
                    } else {
                        painterResource(R.drawable.edit)
                    },
                    contentDescription = "Editar",
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
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
                color = AzulGrisElegante,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isBeingDragged) 3.dp else if (isEditing) 2.dp else 1.dp,
                color = when {
                    isBeingDragged -> VerdeEsmeralda
                    isEditing -> RojoCoral
                    else -> AzulGrisElegante
                },
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = item.text,
                tint = VerdeMenta,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = VerdeMenta
            )
        }
    }
}

data class GridItem(
    val icon: Int,
    val text: String,
    val id: String
)
