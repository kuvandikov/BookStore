import data.Book
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BooksUiState(
    val books: List<Book> = emptyList(),
    val selectedCategory: String? = null,
) {
    val categories = books.map { it.category }.toSet()
    val selectedBooks = books.filter { it.category == selectedCategory }
}

class BirdsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState(emptyList()))
    val uiState = _uiState.asStateFlow()

    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }

    fun updateBooks() {
        viewModelScope.launch {
            val books = getBooks()
            _uiState.update {
                it.copy(books = books)
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { state ->
            if (state.selectedCategory == category) {
                state.copy(selectedCategory = null)
            } else {
                state.copy(selectedCategory = category)
            }
        }
    }

    override fun onCleared() {
        httpClient.close()
    }

    private suspend fun getBooks(): List<Book>{
        val bodyString: String = httpClient.get("https://raw.githubusercontent.com/AnvarbekKuvandikov/Resources/master/books/response.json").bodyAsText()
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        }
        return json.decodeFromString(bodyString)
    }
}