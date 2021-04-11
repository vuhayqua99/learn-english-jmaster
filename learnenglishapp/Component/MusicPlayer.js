import { MaterialIcons } from '@expo/vector-icons';
import { Audio } from 'expo-av';
import React, { Component } from 'react';
import { AsyncStorage, Dimensions, Image, Alert, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Actions } from 'react-native-router-flux';
import Slider from 'react-native-slider';
import * as FileSystem from 'expo-file-system'

import { Authentication, BASE_URL, CacheManager, api } from '../Base';

var PLAYLIST = [
];

const { width: DEVICE_WIDTH, height: DEVICE_HEIGHT } = Dimensions.get('window');
const BACKGROUND_COLOR = '#FFFFFF';
const DISABLED_OPACITY = 0.5;
const FONT_SIZE = 15;
const LOADING_STRING = 'Downloading...';
const BUFFERING_STRING = 'Buffering...';
const RATE_SCALE = 3.0;

export default class MusicPlayer extends Component {
    constructor(props) {
        super(props);
        this.index = this.props.currentIndex;
        this.isSeeking = false;
        this.shouldPlayAtEndOfSeek = false;
        this.playbackInstance = null;
        this.state = {
            playbackInstanceName: LOADING_STRING,
            playbackInstancePosition: null,
            playbackInstanceDuration: null,
            shouldPlay: false,
            isPlaying: false,
            isBuffering: false,
            isLoading: true,
            volume: 1.0,
            rate: 1.0,
            portrait: null,
            user: {},
            progress: 0
        };
    }

    getLoginUser = async () => {
        const user = await Authentication.loginUser()
        if (user) {
            this.setState({ user: user })
        } else {
            await Authentication.logout()
        }
    }

    async componentDidMount() {
        await this.getLoginUser()
        const unitId = this.props.audio.unitId
        const data = await AsyncStorage.getItem("music-" + unitId);
        PLAYLIST = JSON.parse(data)

        Audio.setAudioModeAsync({
            allowsRecordingIOS: false,
            interruptionModeIOS: Audio.INTERRUPTION_MODE_IOS_DO_NOT_MIX,
            playsInSilentModeIOS: true,
            shouldDuckAndroid: true,
            interruptionModeAndroid: Audio.INTERRUPTION_MODE_ANDROID_DO_NOT_MIX,
            playThroughEarpieceAndroid:false
        });
        this._loadNewPlaybackInstance(true);
    }

    async componentWillUnmount() {
        if (this.playbackInstance != null) {
            await this.playbackInstance.unloadAsync();
            this.playbackInstance.setOnPlaybackStatusUpdate(null);
            this.playbackInstance = null;
        }
    }

    async _loadNewPlaybackInstance(playing) {
        if (this.playbackInstance != null) {
            await this.playbackInstance.unloadAsync();
            this.playbackInstance.setOnPlaybackStatusUpdate(null);
            this.playbackInstance = null;
        }

        Actions.refresh({ title: PLAYLIST[this.index].name })

        let source = { uri: '' };

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
                    volume: this.state.volume,
                };

                const { sound, status } = await Audio.Sound.create(
                    source,
                    initialStatus,
                    this._onPlaybackStatusUpdate
                );
                this.playbackInstance = sound;

                this._updateScreenForLoading(false);
            } else {
                await api.authFetchHead(BASE_URL + "/api/member/file/" + PLAYLIST[this.index].fileName, (response) => {
                    const fileSize = Math.round(response.headers.map["content-length"] / 1000000 * 10) / 10
                    Alert.alert(
                        'Tải Audio?',
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
                                        volume: this.state.volume,
                                    };

                                    const { sound, status } = await Audio.Sound.create(
                                        source,
                                        initialStatus,
                                        this._onPlaybackStatusUpdate
                                    );
                                    this.playbackInstance = sound;

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

    _updateScreenForLoading(isLoading) {
        if (isLoading) {
            this.setState({
                isPlaying: false,
                playbackInstanceName: LOADING_STRING,
                playbackInstanceDuration: null,
                playbackInstancePosition: null,
                isLoading: true,
            });
        } else {
            this.setState({
                playbackInstanceName: PLAYLIST[this.index].name,
                isLoading: false,
            });
            CacheManager.loadCacheFile(BASE_URL + '/api/member/file/' + this.props.unit.image, (uri) => {
                this.setState({ portrait: uri })
            })
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
                volume: status.volume,
            });
            if (status.didJustFinish) {
                this._advanceIndex(true);
                this._updatePlaybackInstanceForIndex(true);
            }
        } else {
            if (status.error) {
                console.log(`FATAL PLAYER ERROR: ${status.error}`);
            }
        }
    };

    _advanceIndex(forward) {
        this.index =
            (this.index + (forward ? 1 : PLAYLIST.length - 1)) %
            PLAYLIST.length;
    }

    async _updatePlaybackInstanceForIndex(playing) {
        this._updateScreenForLoading(true);

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

    _onVolumeSliderValueChange = value => {
        if (this.playbackInstance != null) {
            this.playbackInstance.setVolumeAsync(value);
        }
    };

    _trySetRate = async rate => {
        if (this.playbackInstance != null) {
            try {
                await this.playbackInstance.setRateAsync(rate);
            } catch (error) {
                // Rate changing could not be performed, possibly because the client's Android API is too old.
            }
        }
    };

    _onRateSliderSlidingComplete = async value => {
        this._trySetRate(value * RATE_SCALE);
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
                return '0' + string;
            }
            return string;
        };
        return padWithZero(minutes) + ':' + padWithZero(seconds);
    }

    _getTimestamp() {
        if (
            this.playbackInstance != null &&
            this.state.playbackInstancePosition != null &&
            this.state.playbackInstanceDuration != null
        ) {
            return `${this._getMMSSFromMillis(
                this.state.playbackInstancePosition
            )} / ${this._getMMSSFromMillis(
                this.state.playbackInstanceDuration
            )}`;
        }
        return '';
    }

    render() {
        return (
            <View style={styles.container}>
                <View style={styles.portraitContainer}>
                    <Image
                        style={styles.portrait}
                        source={{ uri: this.state.portrait }}
                    />
                </View>
                <View style={styles.detailsContainer}>
                    <Text style={[styles.text, { color: '#e60604', fontWeight: 'bold' }]}>
                        {this.state.playbackInstanceName} {this.state.playbackInstanceName == LOADING_STRING ? Math.round(this.state.progress * 100) + "%" : null}
                    </Text>
                    <Text style={[styles.text]}>
                        {this.state.isBuffering ? (
                            BUFFERING_STRING
                        ) : (
                                this._getTimestamp()
                            )}
                    </Text>
                </View>
                <View
                    style={[
                        styles.buttonsContainerBase,
                        styles.buttonsContainerTopRow,
                        {
                            opacity: this.state.isLoading
                                ? DISABLED_OPACITY
                                : 1.0,
                        },
                    ]}
                >
                    <TouchableOpacity
                        underlayColor={BACKGROUND_COLOR}
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
                        underlayColor={BACKGROUND_COLOR}
                        style={styles.wrapper}
                        onPress={this._onPlayPausePressed}
                        disabled={this.state.isLoading}
                    >
                        {this.state.isPlaying ? (
                            <MaterialIcons
                                name="pause"
                                size={40}
                                color="#e60604"
                            />
                        ) : (
                                <MaterialIcons
                                    name="play-arrow"
                                    size={40}
                                    color="#e60604"
                                />
                            )}
                    </TouchableOpacity>
                    <TouchableOpacity
                        underlayColor={BACKGROUND_COLOR}
                        style={styles.wrapper}
                        onPress={this._onStopPressed}
                        disabled={this.state.isLoading}
                    >
                        <MaterialIcons
                            name="stop"
                            size={40}
                            color="#e60604"
                        />
                    </TouchableOpacity>
                    <TouchableOpacity
                        underlayColor={BACKGROUND_COLOR}
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
                </View>
                <View
                    style={[
                        styles.playbackContainer,
                        {
                            opacity: this.state.isLoading
                                ? DISABLED_OPACITY
                                : 1.0,
                        },
                    ]}
                >
                    <Slider
                        style={styles.playbackSlider}
                        value={this._getSeekSliderPosition()}
                        onValueChange={this._onSeekSliderValueChange}
                        onSlidingComplete={this._onSeekSliderSlidingComplete}
                        thumbTintColor="#000000"
                        minimumTrackTintColor="#e60604"
                        disabled={this.state.isLoading}
                    />
                </View>
                {/* <View
                    style={[
                        styles.buttonsContainerBase,
                        styles.buttonsContainerMiddleRow,
                    ]}
                >
                    <View style={styles.volumeContainer}>
                        <View>
                            <MaterialIcons
                                name="volume-down"
                                size={40}
                                color="#e60604"
                            />
                        </View>
                        <Slider
                            style={styles.volumeSlider}
                            value={1}
                            onValueChange={this._onVolumeSliderValueChange}
                            thumbTintColor="#000000"
                            minimumTrackTintColor="#4CCFF9"
                        />
                        <View>
                            <MaterialIcons
                                name="volume-up"
                                size={40}
                                color="#e60604"
                            />
                        </View>
                    </View>
                </View> */}
                {/* <View
                    style={[
                        styles.buttonsContainerBase,
                        styles.buttonsContainerBottomRow,
                    ]}
                >
                    <View>
                        <MaterialIcons
                            name="call-received"
                            size={40}
                            color="#e60604"
                        />
                    </View>
                    <Slider
                        style={styles.rateSlider}
                        value={this.state.rate / RATE_SCALE}
                        onSlidingComplete={this._onRateSliderSlidingComplete}
                        thumbTintColor="#000000"
                        minimumTrackTintColor="#4CCFF9"
                    />
                    <View>
                        <MaterialIcons
                            name="call-made"
                            size={40}
                            color="#e60604"
                        />
                    </View>
                </View> */}
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'space-between',
        alignItems: 'center',
        alignSelf: 'stretch',
        backgroundColor: BACKGROUND_COLOR,
    },
    portraitContainer: {
        marginTop: 10,
    },
    portrait: {
        height: 150,
        width: 150,
        resizeMode: 'cover'
    },
    detailsContainer: {
        flex: 1,
        marginTop: 20,
        alignItems: 'center',
    },
    playbackContainer: {
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'space-between',
        alignItems: 'center',
        alignSelf: 'stretch',
    },
    playbackSlider: {
        alignSelf: 'stretch',
        marginLeft: 10,
        marginRight: 10,
    },
    text: {
        fontSize: FONT_SIZE,
        minHeight: FONT_SIZE,
    },
    buttonsContainerBase: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
    },
    buttonsContainerTopRow: {
        maxHeight: 40,
        minWidth: DEVICE_WIDTH / 2.0,
        maxWidth: DEVICE_WIDTH / 2.0,
    },
    buttonsContainerMiddleRow: {
        maxHeight: 40,
        alignSelf: 'stretch',
        paddingRight: 20,
    },
    volumeContainer: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        minWidth: DEVICE_WIDTH - 40,
        maxWidth: DEVICE_WIDTH - 40,
    },
    volumeSlider: {
        width: DEVICE_WIDTH - 80,
    },
    buttonsContainerBottomRow: {
        alignSelf: 'stretch',
    },
    rateSlider: {
        width: DEVICE_WIDTH - 80,
    },
});