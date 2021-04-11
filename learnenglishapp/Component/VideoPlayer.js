import { MaterialIcons } from '@expo/vector-icons';
import { Audio, Video } from 'expo-av';
import React from 'react';
import { AsyncStorage, Dimensions, Slider, StyleSheet, Text, Alert, TouchableOpacity, TouchableWithoutFeedback, View } from 'react-native';
import { Actions } from 'react-native-router-flux';

import { Authentication, BASE_URL, CacheManager, api } from '../Base';
import { isRequired } from 'react-native/Libraries/DeprecatedPropTypes/DeprecatedColorPropType';
import * as FileSystem from 'expo-file-system'

var PLAYLIST = [
];

const LOOPING_TYPE_ALL = 0;
const LOOPING_TYPE_ONE = 1;

const { width: DEVICE_WIDTH, height: DEVICE_HEIGHT } = Dimensions.get("window");
const BACKGROUND_COLOR = "#FFFFFF";
const DISABLED_OPACITY = 0.5;
const FONT_SIZE = 15;
const LOADING_STRING = "Downloading...";
const BUFFERING_STRING = "Buffering...";
const RATE_SCALE = 3.0;
const VIDEO_CONTAINER_HEIGHT = DEVICE_HEIGHT
export default class VideoPlayer extends React.Component {
    constructor(props) {
        super(props);
        this.index = this.props.currentIndex;
        this.isSeeking = false;
        this.shouldPlayAtEndOfSeek = false;
        this.playbackInstance = null;
        this.state = {
            showVideo: true,
            playbackInstanceName: LOADING_STRING,
            loopingType: LOOPING_TYPE_ALL,
            muted: false,
            playbackInstancePosition: null,
            playbackInstanceDuration: null,
            shouldPlay: false,
            isPlaying: false,
            isBuffering: false,
            isLoading: true,
            shouldCorrectPitch: true,
            volume: 1.0,
            rate: 1.0,
            videoWidth: DEVICE_WIDTH,
            videoHeight: VIDEO_CONTAINER_HEIGHT,
            useNativeControls: false,
            throughEarpiece: false,
            user: {},
            progress: 0,
            showControl: true
        };
        this.isFirst = true
    }

    hideControl = () => {
        setTimeout(() => {
            this.setState({
                showControl: false
            });
        }, 3000)
    }

    getLoginUser = async () => {
        const user = await Authentication.loginUser()
        if (user) {
            this.setState({ user: user })
        } else {
            await Authentication.logout()
        }
    }

    async _loadNewPlaybackInstance(playing) {
        if (this.playbackInstance != null) {
            await this.playbackInstance.unloadAsync();
            this.playbackInstance = null;
        }
        Actions.refresh({ title: PLAYLIST[this.index].name })
        let source = { uri: "" };

        const fileName = PLAYLIST[this.index].fileName

        await FileSystem.getInfoAsync(`${FileSystem.documentDirectory + fileName}`).then(async (data) => {
            if (data.exists) {
                await CacheManager.loadCacheFile(BASE_URL + "/api/member/file/" + PLAYLIST[this.index].fileName, (uri, progress) => {
                    source = {
                        uri: uri
                    }
                    if (progress) {
                        this.setState({ progress: progress })
                    }
                })

                const initialStatus = {
                    shouldPlay: playing,
                    rate: this.state.rate,
                    shouldCorrectPitch: this.state.shouldCorrectPitch,
                    volume: this.state.volume,
                    isMuted: this.state.muted,
                    isLooping: this.state.loopingType === LOOPING_TYPE_ONE
                };

                await this._video.loadAsync(source, initialStatus);
                this.playbackInstance = this._video;
                await this._video.getStatusAsync();

                this._updateScreenForLoading(false);
            } else {
                await api.authFetchHead(BASE_URL + "/api/member/file/" + PLAYLIST[this.index].fileName, (response) => {
                    const fileSize = Math.round(response.headers.map["content-length"] / 1000000 * 10) / 10
                    Alert.alert(
                        'Tải Video?',
                        'Ứng dụng cần tải nội dung từ server. file size: ' + fileSize + ' MB',
                        [
                            {
                                text: 'Hủy',
                                onPress: () => { Actions.pop() },
                                style: 'cancel',
                            },
                            {
                                text: 'OK', onPress: async () => {
                                    await CacheManager.loadCacheFile(BASE_URL + "/api/member/file/" + PLAYLIST[this.index].fileName, (uri, progress) => {
                                        source = {
                                            uri: uri
                                        }
                                        if (progress) {
                                            this.setState({ progress: progress })
                                        }
                                    })
                                    const initialStatus = {
                                        shouldPlay: playing,
                                        rate: this.state.rate,
                                        shouldCorrectPitch: this.state.shouldCorrectPitch,
                                        volume: this.state.volume,
                                        isMuted: this.state.muted,
                                        isLooping: this.state.loopingType === LOOPING_TYPE_ONE
                                    };

                                    await this._video.loadAsync(source, initialStatus);
                                    this.playbackInstance = this._video;
                                    await this._video.getStatusAsync();

                                    this._updateScreenForLoading(false);
                                }
                            },
                        ],
                        { cancelable: false }
                    );
                })
            }
        })
    }

    _mountVideo = async component => {
        this._video = component;

        await this.getLoginUser()
        const unitId = this.props.video.unitId
        const data = await AsyncStorage.getItem("video-" + unitId);
        PLAYLIST = JSON.parse(data)
        if (this.isFirst) {
            this.isFirst = false
            this._loadNewPlaybackInstance(true);
        }
    };

    async componentDidMount() {
    }

    _updateScreenForLoading(isLoading) {
        if (isLoading) {
            this.setState({
                showVideo: false,
                isPlaying: false,
                playbackInstanceName: LOADING_STRING,
                playbackInstanceDuration: null,
                playbackInstancePosition: null,
                isLoading: true
            });
        } else {
            this.setState({
                playbackInstanceName: PLAYLIST[this.index].name,
                showVideo: true,
                isLoading: false
            });
            this.hideControl()
        }
    }

    _onPlaybackStatusUpdate = status => {
        if (status.isLoaded) {
            this.setState({
                playbackInstancePosition: status.positionMillis,
                playbackInstanceDuration: status.durationMillis,
                shouldPlay: status.shouldPlay,
                isPlaying: status.isPlaying,
                isBuffering: status.isBuffering,
                rate: status.rate,
                muted: status.isMuted,
                volume: status.volume,
                loopingType: status.isLooping ? LOOPING_TYPE_ONE : LOOPING_TYPE_ALL,
                shouldCorrectPitch: status.shouldCorrectPitch
            });
            if (status.didJustFinish && !status.isLooping) {
                this._advanceIndex(true);
                this._updatePlaybackInstanceForIndex(true);
            }
        } else {
            if (status.error) {
                console.log(`FATAL PLAYER ERROR: ${status.error}`);
            }
        }
    };

    _onLoadStart = () => {
        console.log(`ON LOAD START`);
    };

    _onLoad = status => {
        // console.log(`ON LOAD : ${JSON.stringify(status)}`);
    };

    _onError = error => {
        console.log(`ON ERROR : ${error}`);
    };

    _onReadyForDisplay = event => {
        const widestHeight =
            (DEVICE_WIDTH * event.naturalSize.height) / event.naturalSize.width;
        if (widestHeight > VIDEO_CONTAINER_HEIGHT) {
            this.setState({
                videoWidth:
                    (VIDEO_CONTAINER_HEIGHT * event.naturalSize.width) /
                    event.naturalSize.height,
                videoHeight: VIDEO_CONTAINER_HEIGHT
            });
        } else {
            this.setState({
                videoWidth: DEVICE_WIDTH,
                videoHeight:
                    (DEVICE_WIDTH * event.naturalSize.height) / event.naturalSize.width
            });
        }
    };

    _onFullscreenUpdate = event => {
        console.log(
            `FULLSCREEN UPDATE : ${JSON.stringify(event.fullscreenUpdate)}`
        );
    };

    _advanceIndex(forward) {
        this.index =
            (this.index + (forward ? 1 : PLAYLIST.length - 1)) % PLAYLIST.length;
    }

    async _updatePlaybackInstanceForIndex(playing) {
        this._updateScreenForLoading(true);

        this.setState({
            videoWidth: DEVICE_WIDTH,
            videoHeight: VIDEO_CONTAINER_HEIGHT
        });

        this._loadNewPlaybackInstance(playing);
    }

    _onPlayPausePressed = () => {
        if (this.playbackInstance != null) {
            if (this.state.isPlaying) {
                this.playbackInstance.pauseAsync();
            } else {
                this.playbackInstance.playAsync();
            }
        }
    };

    _onStopPressed = () => {
        if (this.playbackInstance != null) {
            this.playbackInstance.stopAsync();
        }
    };

    _onForwardPressed = () => {
        if (this.playbackInstance != null) {
            this._advanceIndex(true);
            this._updatePlaybackInstanceForIndex(this.state.shouldPlay);
        }
    };

    _onBackPressed = () => {
        if (this.playbackInstance != null) {
            this._advanceIndex(false);
            this._updatePlaybackInstanceForIndex(this.state.shouldPlay);
        }
    };

    _onMutePressed = () => {
        if (this.playbackInstance != null) {
            this.playbackInstance.setIsMutedAsync(!this.state.muted);
        }
    };

    _onLoopPressed = () => {
        if (this.playbackInstance != null) {
            this.playbackInstance.setIsLoopingAsync(
                this.state.loopingType !== LOOPING_TYPE_ONE
            );
        }
    };

    _trySetRate = async (rate, shouldCorrectPitch) => {
        if (this.playbackInstance != null) {
            try {
                await this.playbackInstance.setRateAsync(rate, shouldCorrectPitch);
            } catch (error) {
                // Rate changing could not be performed, possibly because the client's Android API is too old.
            }
        }
    };

    _onRateSliderSlidingComplete = async value => {
        this._trySetRate(value * RATE_SCALE, this.state.shouldCorrectPitch);
    };

    _onPitchCorrectionPressed = async value => {
        this._trySetRate(this.state.rate, !this.state.shouldCorrectPitch);
    };

    _onSeekSliderValueChange = value => {
        if (this.playbackInstance != null && !this.isSeeking) {
            this.isSeeking = true;
            this.shouldPlayAtEndOfSeek = this.state.shouldPlay;
            this.playbackInstance.pauseAsync();
        }
    };

    _onSeekSliderSlidingComplete = async value => {
        if (this.playbackInstance != null) {
            this.isSeeking = false;
            const seekPosition = value * this.state.playbackInstanceDuration;
            if (this.shouldPlayAtEndOfSeek) {
                this.playbackInstance.playFromPositionAsync(seekPosition);
            } else {
                this.playbackInstance.setPositionAsync(seekPosition);
            }
        }
    };

    _getSeekSliderPosition() {
        if (
            this.playbackInstance != null &&
            this.state.playbackInstancePosition != null &&
            this.state.playbackInstanceDuration != null
        ) {
            return (
                this.state.playbackInstancePosition /
                this.state.playbackInstanceDuration
            );
        }
        return 0;
    }

    _getMMSSFromMillis(millis) {
        const totalSeconds = millis / 1000;
        const seconds = Math.floor(totalSeconds % 60);
        const minutes = Math.floor(totalSeconds / 60);

        const padWithZero = number => {
            const string = number.toString();
            if (number < 10) {
                return "0" + string;
            }
            return string;
        };
        return padWithZero(minutes) + ":" + padWithZero(seconds);
    }

    _getTimestamp() {
        if (
            this.playbackInstance != null &&
            this.state.playbackInstancePosition != null &&
            this.state.playbackInstanceDuration != null
        ) {
            return `${this._getMMSSFromMillis(
                this.state.playbackInstancePosition
            )} / ${this._getMMSSFromMillis(this.state.playbackInstanceDuration)}`;
        }
        return "";
    }

    _onFullscreenPressed = () => {
        try {
            this._video.presentFullscreenPlayer();
        } catch (error) {
            console.log(error.toString());
        }
    };

    render() {
        return (
            <View style={styles.container}>
                {this.state.showControl ? <TouchableOpacity
                    style={[styles.wrapper, { position: 'absolute', top: 15, left: 15, zIndex: 9999 }]}
                    onPress={() => Actions.pop()}
                >
                    <MaterialIcons
                        name="arrow-back"
                        size={40}
                        color="#e60604"
                    />
                </TouchableOpacity> : null}
                <TouchableWithoutFeedback style={styles.videoContainer} onPress={() => {
                    this.setState({ showControl: !this.state.showControl })
                }}>
                    <Video
                        ref={this._mountVideo}
                        style={[
                            styles.video,
                            {
                                opacity: this.state.showVideo ? 1.0 : 0.0,
                                width: this.state.videoWidth,
                                height: this.state.videoHeight
                            }
                        ]}
                        resizeMode={Video.RESIZE_MODE_CONTAIN}
                        onPlaybackStatusUpdate={this._onPlaybackStatusUpdate}
                        onLoadStart={this._onLoadStart}
                        onLoad={this._onLoad}
                        onError={this._onError}
                        onFullscreenUpdate={this._onFullscreenUpdate}
                        onReadyForDisplay={this._onReadyForDisplay}
                        useNativeControls={this.state.useNativeControls}
                    />
                </TouchableWithoutFeedback>
                {
                    this.state.showControl ? <View
                        style={[
                            styles.buttonsContainerBase,
                            styles.buttonsContainerTopRow,
                            {
                                opacity: this.state.isLoading ? DISABLED_OPACITY : 1.0
                            }
                        ]}
                    >
                        <TouchableOpacity
                            style={styles.wrapper}
                            onPress={() => this._trySetRate(this.state.rate > 1 ? this.state.rate - 1.0 : 1.0, this.state.shouldCorrectPitch)}
                            disabled={this.state.isLoading}
                        >
                            <MaterialIcons
                                name="replay-30"
                                size={35}
                                color="#e60604"
                            />
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={styles.wrapper}
                            onPress={this._onBackPressed}
                            disabled={this.state.isLoading}
                        >
                            <MaterialIcons
                                name="skip-previous"
                                size={40}
                                color="#e60604"
                            />
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={styles.wrapper}
                            onPress={this._onPlayPausePressed}
                            disabled={this.state.isLoading}
                        >
                            {this.state.isPlaying ? (
                                <MaterialIcons
                                    name="pause-circle-filled"
                                    size={40}
                                    color="#e60604"
                                />
                            ) : (
                                    <MaterialIcons
                                        name="play-circle-filled"
                                        size={40}
                                        color="#e60604"
                                    />
                                )}
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={styles.wrapper}
                            onPress={this._onForwardPressed}
                            disabled={this.state.isLoading}
                        >
                            <MaterialIcons
                                name="skip-next"
                                size={40}
                                color="#e60604"
                            />
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={styles.wrapper}
                            onPress={() => this._trySetRate(this.state.rate < 3 ? this.state.rate + 1.0 : RATE_SCALE, this.state.shouldCorrectPitch)}
                            disabled={this.state.isLoading}
                        >
                            <MaterialIcons
                                name="forward-30"
                                size={35}
                                color="#e60604"
                            />
                        </TouchableOpacity>
                    </View>
                        : null
                }
                {
                    this.state.showControl ? <View
                        style={[
                            styles.playbackContainer,
                            {
                                opacity: this.state.isLoading ? DISABLED_OPACITY : 1.0
                            }
                        ]}
                    >
                        <View style={styles.timestampRow}>
                            <Text style={[styles.text, { paddingLeft: 20 }]}>
                                {this.state.playbackInstanceName} {this.state.playbackInstanceName == LOADING_STRING ? Math.round(this.state.progress * 100) + "%" : null}
                            </Text>
                            <Text
                                style={[
                                    styles.text,
                                    styles.buffering,
                                ]}
                            >
                                {this.state.isBuffering ? BUFFERING_STRING : ""}
                            </Text>
                            <Text
                                style={[
                                    styles.text,
                                    styles.timestamp,
                                ]}
                            >
                                {this._getTimestamp()}
                            </Text>
                        </View>
                        <Slider
                            style={[styles.playbackSlider]}
                            value={this._getSeekSliderPosition()}
                            onValueChange={this._onSeekSliderValueChange}
                            onSlidingComplete={this._onSeekSliderSlidingComplete}
                            minimumTrackTintColor="#e60604"
                            disabled={this.state.isLoading}
                            thumbImage={require("../assets/dot.png")}
                        />
                    </View> : null
                }
                <View />
            </View>
        );
    }
}

const styles = StyleSheet.create({
    emptyContainer: {
        alignSelf: "stretch",
        backgroundColor: BACKGROUND_COLOR
    },
    container: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "space-between",
        alignItems: "center",
        alignSelf: "stretch",
        backgroundColor: BACKGROUND_COLOR
    },
    wrapper: {},
    nameContainer: {
        height: FONT_SIZE
    },
    space: {
        height: FONT_SIZE
    },
    videoContainer: {
        //height: VIDEO_CONTAINER_HEIGHT,
        zIndex: 1,
        position: 'absolute'
    },
    video: {
        //maxWidth: DEVICE_WIDTH
    },
    playbackContainer: {
        zIndex: 2,
        position: 'absolute',
        bottom: 5,
        left: 0,
        right: 0,
        flexDirection: "column",
        justifyContent: "space-between",
        alignItems: "center",
        alignSelf: "stretch",
        height: 40
    },
    playbackSlider: {
        height: 10,
        alignSelf: "stretch"
    },
    timestampRow: {
        flex: 1,
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        alignSelf: "stretch",
        minHeight: FONT_SIZE
    },
    text: {
        fontSize: FONT_SIZE,
        minHeight: FONT_SIZE
    },
    buffering: {
        textAlign: "left",
        paddingLeft: 20
    },
    timestamp: {
        textAlign: "right",
        paddingRight: 20
    },
    button: {
        backgroundColor: BACKGROUND_COLOR
    },
    buttonsContainerBase: {
        zIndex: 2,
        position: 'absolute',
        bottom: 40,
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
    },
    buttonsContainerTopRow: {
        maxHeight: 40,
        minWidth: DEVICE_WIDTH / 2.0,
        maxWidth: DEVICE_WIDTH / 2.0
    },
    buttonsContainerMiddleRow: {
        maxHeight: 30,
        alignSelf: "stretch",
        paddingRight: 20
    },
    buttonsContainerBottomRow: {
        maxHeight: 30,
        alignSelf: "stretch",
        paddingRight: 20,
        paddingLeft: 20
    },
    rateSlider: {
        width: DEVICE_WIDTH / 2.0
    },
    buttonsContainerTextRow: {
        maxHeight: FONT_SIZE,
        alignItems: "center",
        paddingRight: 20,
        paddingLeft: 20,
        minWidth: DEVICE_WIDTH,
        maxWidth: DEVICE_WIDTH
    }
});
