package id.sidiqimawan.cupcake.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.sidiqimawan.cupcake.R
import id.sidiqimawan.cupcake.ui.theme.CupcakeTheme
import id.sidiqimawan.cupcake.ui.theme.components.FormattedPriceLabel

// Fungsi utama untuk layar pilihan opsi
@Composable
fun SelectOptionScreen(
    subtotal: String, // Subtotal harga
    options: List<String>, // Daftar opsi yang dapat dipilih
    onSelectionChanged: (String) -> Unit = {}, // Callback saat opsi berubah
    onCancelButtonClicked: () -> Unit = {}, // Callback saat tombol cancel diklik
    onNextButtonClicked: () -> Unit = {}, // Callback saat tombol next diklik
    modifier: Modifier = Modifier // Modifier untuk komponen
) {
    var selectedValue by rememberSaveable { mutableStateOf("") }
//State yang menyimpan nilai opsi yang dipilih

    // Kolom utama untuk layout layar
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Kolom untuk daftar opsi
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            options.forEach { item ->
                Row(
                    modifier = Modifier.selectable(
                        selected = selectedValue == item, // Mengecek apakah opsi saat ini dipilih
                        onClick = {
                            selectedValue = item // Memperbarui opsi yang dipilih
                            onSelectionChanged(item) // Memanggil callback ketika opsi berubah
                        }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedValue == item,
                        onClick = {
                            selectedValue = item
                            onSelectionChanged(item)
                        }
                    )
                    Text(item) // Menampilkan teks opsi
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium)),
                thickness = dimensionResource(R.dimen.thickness_divider)
            )
            FormattedPriceLabel(
                subtotal = subtotal, // Menampilkan subtotal
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        top = dimensionResource(R.dimen.padding_medium),
                        bottom = dimensionResource(R.dimen.padding_medium)
                    )
            )
        }
        // Baris untuk tombol Cancel dan Next
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)), // Padding untuk tombol
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)), // Jarak antar tombol
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onCancelButtonClicked
            ) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                modifier = Modifier.weight(1f),
                enabled = selectedValue.isNotEmpty(),
                onClick = onNextButtonClicked
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }

}

// Fungsi preview untuk melihat tampilan SelectOptionScreen
@Preview
@Composable
fun SelectOptionPreview() {
    CupcakeTheme {
        SelectOptionScreen(
            subtotal = "299.99",
            options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
            modifier = Modifier.fillMaxHeight()
        )
    }
}