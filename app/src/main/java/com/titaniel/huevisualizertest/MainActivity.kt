package com.titaniel.huevisualizertest

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track


class MainActivity : AppCompatActivity() {

    private val CLIENT_ID = "4e40dab2cefc4544ac431ec30100b9d2"
    private val REDIRECT_URI = "http://com.titaniel.huevisualizertest/callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null

    private lateinit var tvHelloWorld: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvHelloWorld = findViewById(R.id.tvHelloWorld)
        tvHelloWorld.setText("Hallo Welt!");
    }

    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build()
        SpotifyAppRemote.connect(this, connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote
                        Log.d("MainActivity", "Connected! Yay!")

                        // Now you can start interacting with App Remote
                        connected()
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                })
    }

    private fun connected() {
        //mSpotifyAppRemote!!.playerApi.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");// Subscribe to PlayerState
        mSpotifyAppRemote!!.playerApi
                .subscribeToPlayerState()
                .setEventCallback { playerState: PlayerState ->
                    val track: Track? = playerState.track
                    if (track != null) {
                        Log.d("MainActivity", track.name.toString() + " by " + track.artist.name)
                    }
                    Log.d("MainActivity", "Playback position: " + playerState.playbackPosition)
                }
        mSpotifyAppRemote!!.playerApi.subscribeToPlayerContext();
    }

    override fun onStop() {
        super.onStop()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}