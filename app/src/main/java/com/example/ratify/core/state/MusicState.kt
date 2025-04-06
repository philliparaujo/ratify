import com.example.ratify.core.model.Rating

data class MusicState(
    val currentRating: Rating? = null,  // Represents song's currently selected rating, shown on UI
)