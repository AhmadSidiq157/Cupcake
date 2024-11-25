package id.sidiqimawan.cupcake.ui.theme

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.sidiqimawan.cupcake.R
import id.sidiqimawan.cupcake.data.DataSource
import id.sidiqimawan.cupcake.data.OrderUiState
import id.sidiqimawan.cupcake.ui.theme.OrderSummaryScreen
import id.sidiqimawan.cupcake.ui.theme.OrderViewModel
import id.sidiqimawan.cupcake.ui.theme.SelectOptionScreen
import id.sidiqimawan.cupcake.ui.theme.StartOrderScreen


enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name), // memilih jumlah kue
    Flavor(title = R.string.choose_flavor), // memilih rasa kue
    Pickup(title = R.string.choose_pickup_date), // memilih tanggal untuk pengambailan kue
    Summary(title = R.string.order_summary) // ringkasan pesanan
}

// membuat top app bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen, // Parameter layar saat ini
    canNavigateBack: Boolean, // Parameter apakah bisa navigasi ke belakang
    navigateUp: () -> Unit, // Lambda untuk aksi navigasi ke atas
    modifier: Modifier = Modifier // Modifikasi tambahan
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors( // Mengatur warna AppBar
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = { // Menentukan ikon navigasi
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) { // Tombol untuk kembali
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button) // Deskripsi ikon
                    )
                }
            }
        }
    )
}
// komonen utama yang mengatur navigasi antar layar dan state aplikasi
@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(), // Mengambil instance ViewModel untuk mengelola state aplikasi
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf( // Menentukan layar saat ini berdasarkan route
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )

    Scaffold(
        topBar = { // Membuat AppBar di bagian atas layar
            CupcakeAppBar(
                currentScreen = currentScreen, // Menentukan layar yang sedang aktif untuk AppBar
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding -> // Inner padding untuk konten di dalam Scaffold
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController, // NavController untuk navigasi antar layar
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier
                .fillMaxSize() // Mengisi seluruh ukuran layar
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = CupcakeScreen.Start.name) { // Komposisi untuk layar Start
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions, // Pilihan jumlah kue dari sumber data
                    onNextButtonClicked = { // Aksi ketika tombol Next diklik
                        viewModel.setQuantity(it) // Mengatur jumlah pesanan di ViewModel
                        navController.navigate(CupcakeScreen.Flavor.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }
            composable(route = CupcakeScreen.Flavor.name) { // Komposisi untuk layar Flavor
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price, // Menampilkan subtotal saat ini
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Pickup.name) { // Komposisi untuk layar Pickup
                SelectOptionScreen(
                    subtotal = uiState.price, // Menampilkan subtotal saat ini
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Summary.name) { // Komposisi untuk layar Summary
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}
// Fungsi untuk membatalkan pesanan dan kembali ke layar Start
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}
// Fungsi untuk membagikan detail pesanan
private fun shareOrder(context: Context, subject: String, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser( // Membuka chooser untuk aplikasi yang dapat berbagi
            intent,
            context.getString(R.string.new_cupcake_order)
        )
    )
}