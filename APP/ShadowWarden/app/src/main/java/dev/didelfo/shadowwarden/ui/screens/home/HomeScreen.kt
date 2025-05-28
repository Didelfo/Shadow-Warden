package dev.didelfo.shadowwarden.ui.screens.home

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import dev.didelfo.shadowwarden.R
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import dev.didelfo.shadowwarden.ui.navigation.AppScreens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import dev.didelfo.shadowwarden.localfiles.Server
import dev.didelfo.shadowwarden.ui.screens.components.createDialogOpti
import dev.didelfo.shadowwarden.ui.screens.components.loadingView
import dev.didelfo.shadowwarden.ui.theme.*
import dev.didelfo.shadowwarden.ui.viewModel.HomeScreenViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(navController: NavHostController){

    val viewModel: HomeScreenViewModel = HomeScreenViewModel(LocalContext.current)

    viewModel.getServers()

    Scaffold(
        topBar = {
            ToolBar(
                title = "Servidores",
                onImageClick = {
                    navController.navigate(AppScreens.ChatScreen.route)
                },
                onAddClick = {
                    navController.navigate(AppScreens.AddServerScreen.route)
                },
                viewModel
            )
        },

        content = { paddingValues ->
            ServersRecyclerView(
                servers = viewModel.servers,
                modifier = Modifier.padding(paddingValues),
                onItemClick = {server ->
                    viewModel.conectar(server)
                },
                viewModel
            )
            loadingView(viewModel.loadingScreen)
        }
    )


}


// -----------------------------------------------------------
//                   Funciones composables
// -----------------------------------------------------------

// ToolBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolBar(
    title: String,
    onImageClick: () -> Unit,
    onAddClick: () -> Unit,
    viewModel: HomeScreenViewModel
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                color = VerdeMenta,
                fontFamily = OpenSanBold,
                fontSize = 26.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AzulVerdosoOscuro
        ),
        navigationIcon = {
            IconButton(onClick = onImageClick) {

                if (viewModel.file.exists()){

                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(viewModel.cont)
                            .data(Uri.fromFile(viewModel.file))
                            .crossfade(true)
                            .build()
                    )

                    Image(
                        painter = painter,
                        contentDescription = "User avatar",
                        modifier = Modifier
                            .size(52.dp)
                    )

                } else {
                 Icon(
                     painter = painterResource(R.drawable.creeper),
                     contentDescription = "sin skin",
                     tint = VerdeEsmeralda,
                     modifier = Modifier.size(52.dp)
                 )
                }
            }
        },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add",
                    tint = VerdeMenta,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    )
}

// RecyclerView
@Composable
private fun ServersRecyclerView(
    servers: ArrayList<Server>,
    modifier: Modifier = Modifier,
    onItemClick: (Server) -> Unit,
    viewModel: HomeScreenViewModel
){

    LazyColumn (
        modifier = modifier.fillMaxSize()
            .background(AzulOscuroProfundo)
    ){
        items(servers) { server ->
            ServerItem(
                server = server,
                onItemClicck = {onItemClick(server)},
                viewModel
            )

        }
    }

}

// Vista de cada item
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ServerItem(
    server: Server,
    onItemClicck: () -> Unit,
    viewModel: HomeScreenViewModel
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =  16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = onItemClicck,
                onLongClick = {viewModel.showMenuDelete = true}
            ),
        colors = CardDefaults.cardColors(
            containerColor = AzulGrisElegante
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.server),
                contentDescription = "Server Icon",
                tint = VerdeMenta,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = server.name,
                color = VerdeMenta,
                fontSize = 18.sp,
                fontFamily = OpenSanBold,
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (viewModel.showMenuDelete){
        createDialogOpti(
            painterResource(R.drawable.delete),
            RojoCoral,
            "Borrar",
            RojoCoral,
            "¿Deseas borrar este servidor?",
            "Si",
            VerdeEsmeralda,
            "No",
            RojoCoral,
            {
                viewModel.deleteServer(server)
                viewModel.showMenuDelete = false
            },
            {viewModel.showMenuDelete = false}
        )
    }
}
