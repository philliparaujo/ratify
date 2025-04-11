package com.example.ratify.mocks

import com.example.ratify.core.model.Rating
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

// Real song attributes
private const val mockAlbumName = "HOTSHOT"
private const val mockArtistName = "YSB Tril"
private const val mockSongName = "Touchdown (feat. Bankrol Hayden)"
private const val mockSongUri = "spotify:track:7xWnMfIVVnI3Y3zPp9Ukvi"
private const val mockImageUri = "spotify:image:ab67616d0000b2739fe3277e1c1295755de75305"
private const val mockDuration: Long = 141581

// Other made up attributes
private const val mockLongSongName = "Symphony No. 40 in G Minor, K. 550: I.Allegro molto"
private const val mockTimestamp = 1737132383109
private const val mockTimesPlayed = 1
private val mockRating = Rating.from(10)

private val mockCount = 12
private val mockTotalTimesPlayed = 55
private val mockAverageRating = 6.4f

private const val unspecifiedString = "foo"

// Exported Spotify types
val mockArtist = Artist(mockArtistName, unspecifiedString)
val mockAlbum = Album(mockAlbumName, unspecifiedString)
val mockSong = Song(
    album = mockAlbum,
    artist = mockArtist,
    artists = listOf(
        mockArtist,
        Artist("Bankrol Hayden", unspecifiedString)
    ),
    duration = mockDuration,
    imageUri = ImageUri(mockImageUri),
    name = mockSongName,
    uri = mockSongUri,
    lastPlayedTs = mockTimestamp,
    timesPlayed = mockTimesPlayed,
    lastRatedTs = mockTimestamp,
    rating = mockRating
)
val longMockSong = Song(
    album = Album(mockLongSongName, unspecifiedString),
    artist = Artist("Wolfgang Amadeus Mozart", unspecifiedString),
    artists = listOf(
        Artist("Wolfgang Amadeus Mozart", unspecifiedString),
        Artist("Capella Instropolitana", unspecifiedString),
        Artist("Barry Wordsworth", unspecifiedString)
    ),
    duration = 451333,
    imageUri = ImageUri(unspecifiedString),
    name = mockLongSongName,
    uri = unspecifiedString,
    lastPlayedTs = mockTimestamp,
    timesPlayed = mockTimesPlayed,
    lastRatedTs = mockTimestamp,
    rating = mockRating
)
val mockGroupedSong = GroupedSong(
    artist = mockArtist,
    album = null,
    count = mockCount,
    totalTimesPlayed = mockTotalTimesPlayed,
    averageRating = mockAverageRating,
    lastPlayedTs = mockTimestamp,
    lastRatedTs = mockTimestamp,
    imageUri = ImageUri(mockImageUri)
)