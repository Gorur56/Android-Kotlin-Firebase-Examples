package com.example.androidvideocallwithwebrtcandfirebasebackup.repository

import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModel
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModelType
import com.example.androidvideocallwithwebrtcandfirebasebackup.data.DataModelType.*
import com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient.FirebaseClient
import com.example.androidvideocallwithwebrtcandfirebasebackup.firebaseClient.FirebaseClientListener
import com.example.androidvideocallwithwebrtcandfirebasebackup.utils.UserStatus
import com.example.androidvideocallwithwebrtcandfirebasebackup.webrtc.MyPeerObserver
import com.example.androidvideocallwithwebrtcandfirebasebackup.webrtc.WebRTCClient
import com.example.androidvideocallwithwebrtcandfirebasebackup.webrtc.WebRTCListener
import com.google.gson.Gson
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val webRTCClient: WebRTCClient,
    private val gson: Gson
) : WebRTCListener {

    private var target:String ?= null
    var listener: MainRepositoryListener ?= null
    private var remoteView: SurfaceViewRenderer ?= null

    fun login( username: String, password: String, isDone: (Boolean,String?) -> Unit) {
        firebaseClient.login(username, password, isDone)
    }

    fun observeUsersStatus( status: (List<Pair<String, String>>) -> Unit) {
        firebaseClient.observeUsersStatus(status)
    }

    fun initFirebase() {
        firebaseClient.subscribeForLatestEvent(object : FirebaseClientListener {
            override fun onLatestEventReceived(event: DataModel) {
                listener?.onLatestEventReceived(event)

                when(event.type){
                    Offer -> {
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.OFFER,
                                event.data.toString()
                            )
                        )
                        webRTCClient.answer(target!!)
                    }
                    Answer -> {
                        webRTCClient.onRemoteSessionReceived(
                            SessionDescription(
                                SessionDescription.Type.ANSWER,
                                event.data.toString()
                            )
                        )
                    }
                    IceCandidates -> {
                        val candidate: IceCandidate? = try {
                            gson.fromJson(event.data.toString(), IceCandidate::class.java)
                        } catch ( e: Exception) {
                            null
                        }
                        candidate?.let{
                            webRTCClient.addIceCandidateToPeer(it)
                        }
                    }
                    EndCall -> {
                        listener?.endCall()
                    }

                    else -> Unit
                }
            }

        })
    }

    fun sendConnectionRequest( target: String, isVideoCall: Boolean, success:(Boolean) -> Unit) {
        firebaseClient.sendMessageToOtherClient(
            DataModel(
                type = if (isVideoCall) StartVideoCall else StartAudioCall,
                target = target
            ), success
        )
    }

    fun setTarget(target: String) {
        this.target = target
    }

    fun initWebrtcClient(username: String) {
        webRTCClient.listener = this
        webRTCClient.initializeWebrtcClient(username, object : MyPeerObserver() {

            override fun onAddStream(p0: MediaStream?) {
                super.onAddStream(p0)
                try {
                    p0?.videoTracks?.get(0)?.addSink(remoteView)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRTCClient.sendIceCandidate(target!!, it)
                }
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                super.onConnectionChange(newState)
                if (newState == PeerConnection.PeerConnectionState.CONNECTED) {
                    // 1. change my status to in call
                    changeMyStatus(UserStatus.IN_CALL)
                    // 2. clear latest event inside my user section in firebase database
                    firebaseClient.clearLatestEvent()
                }
            }
        })
    }

    fun initLocalSurfaceView( view: SurfaceViewRenderer, isVideoCall: Boolean) {
        webRTCClient.initLocalSurfaceView(view, isVideoCall)
    }

    fun initRemoteSurfaceView(view: SurfaceViewRenderer) {
        webRTCClient.initRemoteSurface(view)
        this.remoteView = view
    }

    fun startCall() {
        webRTCClient.call(target!!)
    }

    fun endCall() {
        webRTCClient.closeConnection()
        changeMyStatus(UserStatus.ONLINE)
    }

    fun sendEndCall() {
        onTransferEventSocket(
            DataModel(
                type = EndCall,
                target = target!!
            )
        )
    }

    private fun changeMyStatus( status: UserStatus) {
        firebaseClient.changeMyStatus( status )
    }

    fun toggleAudio(shouldBeMuted: Boolean) {
        webRTCClient.toggleAudio(shouldBeMuted)
    }

    fun toggleVideo(shouldBeMuted: Boolean) {
        webRTCClient.toggleVideo(shouldBeMuted)
    }

    fun switchCamera() {
        webRTCClient.switchCamera()
    }

    override fun onTransferEventSocket(data: DataModel) {
        firebaseClient.sendMessageToOtherClient(data) {}
    }
}