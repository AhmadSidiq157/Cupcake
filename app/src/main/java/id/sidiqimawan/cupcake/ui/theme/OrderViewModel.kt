package id.sidiqimawan.cupcake.ui.theme

import androidx.lifecycle.ViewModel
import id.sidiqimawan.cupcake.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val PRICE_PER_CUPCAKE = 2.00 //menentukan harga per kua/cake
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00 // biaya tambahan jika pesanan diambail hari ini

class OrderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()
    fun setQuantity(numberCupcakes: Int) { //jumlah kue yang dipesan
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberCupcakes,
                price = calculatePrice(quantity = numberCupcakes)
            )
        }
    }
    fun setFlavor(desiredFlavor: String) { // rasa kue yang di inginkan
        _uiState.update { currentState ->
            currentState.copy(flavor = desiredFlavor)
        }
    }
    fun setDate(pickupDate: String) { // data pengambilan pesanan yang dipilih
        _uiState.update { currentState ->
            currentState.copy(
                date = pickupDate,
                price = calculatePrice(pickupDate = pickupDate)
            )
        }
    }

    fun resetOrder() {
        _uiState.value = OrderUiState(pickupOptions = pickupOptions())
    }

    private fun calculatePrice( //
        quantity: Int = _uiState.value.quantity,
        pickupDate: String = _uiState.value.date
    ): String {
        var calculatedPrice = quantity * PRICE_PER_CUPCAKE // menghitung harga awal
        if (pickupOptions()[0] == pickupDate) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        val formattedPrice = NumberFormat.getCurrencyInstance().format(calculatedPrice)
        return formattedPrice
    }
    private fun pickupOptions(): List<String> { //Membuat daftar 4 tanggal pengambilan, mulai dari hari ini hingga 3 hari berikutnya
        val dateOptions = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // add current date and the following 3 dates.
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }
}