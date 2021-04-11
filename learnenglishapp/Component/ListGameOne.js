import React, { Component, Fragment } from 'react'
import { AppRegistry, StyleSheet, Platform, Text, View, AsyncStorage, TouchableOpacity, Dimensions,ActivityIndicator, ImageBackground } from 'react-native'
import Swiper from 'react-native-swiper'
import { api, BASE_URL, Authentication } from '../Base';
import { Audio } from 'expo-av'
import { CachedImageOffline } from './CachedImageOffline';
import * as FileSystem from 'expo-file-system'
import { MaterialCommunityIcons } from '@expo/vector-icons';
const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

export default class ListGameOne extends Component {

    constructor(props) {
        super(props)
        this.state = {
            gameOnes: [],
            audiouri: "",
            isLoading: false,
            retries: 0,
            auto: false

        }
    }

    componentDidMount() {
        this.offlineLoad();
        this.getLoginUser();

    }

    getLoginUser = async () => {
        const user = await Authentication.loginUser()
        if (user) {
            this.setState({ user: user })
        }
    }

    async loadData() {
        api.authFetch("/api/member/game-one/list", "post", this.props.searchDTO,
            response => {
                response.json().then(responseJson => {
                    this.setState({
                        gameOnes: responseJson.data,
                        retries: 0
                    })
                    this.saveOfflineData(responseJson.data)
                }, () => {
                    this.offlineLoad()
                    var retries = this.state.retries + 1
                    this.setState({ retries: retries, isLoading: true })
                })
            }
        )
    }

    saveOfflineData = async (data) => {
        await AsyncStorage.setItem("gameone-" + this.props.searchDTO.unitId, JSON.stringify(data));
    }

    offlineLoad = async () => {
        const data = await AsyncStorage.getItem("gameone-" + this.props.searchDTO.unitId);
        if (data) {
            this.setState({
                gameOnes: JSON.parse(data),
                isLoading: true,
                retries: 0
            })
        } else {
            if (this.state.retries < 5) {
                this.loadData();
            }
        }
    }

    changeAuto = (auto) => {
        this.setState({ auto: auto })
    }

    render() {

        return (
            this.state.isLoading ?
                <ImageBackground source={require("../assets/background1.jpg")} style={styles.image} >
                    <Swiper showsButtons={true}
                        showsPagination={false}
                        nextButton={<Text style={styles.buttonText}>›</Text>}
                        prevButton={<Text style={styles.buttonText}>‹</Text>}
                        loop={false}
                        autoplay={this.state.auto}
                        autoplayTimeout={1.5}
                        onIndexChanged={(index) => {
                            this.changeAuto(false)
                        }}

                    >
                        {this.state.gameOnes.map((item, index) => {
                            return <GameOneItem item={item} key={index} {...this.props} {...this.state} changeAuto={this.changeAuto} />
                        })}
                    </Swiper>
                </ImageBackground> : <View style={[styles.container, styles.horizontal]}>
                    <ActivityIndicator size="large"/>
                </View>

        )
    }
}

AppRegistry.registerComponent('learnenglishapp', () => ListGameOne)

class GameOneItem extends React.Component {

    constructor(props) {
        super(props)
    }

    loadLocal(uri) {
        this.setState({ audiouri: uri });
    }

    loadAudio = async (source) => {
        const fileName = source.slice((source.lastIndexOf("/") - 1 >>> 0) + 2)
        await FileSystem.getInfoAsync(`${FileSystem.documentDirectory + fileName}`).then(async (data) => {
            if (data.exists) {
                this.loadLocal(`${FileSystem.documentDirectory + fileName}`);
            } else {
                await FileSystem.downloadAsync(
                    source,
                    `${FileSystem.documentDirectory + fileName}`,
                    {
                        headers: {
                            Authorization: "Basic " + this.props.user.base64
                        },
                        cache: true
                    }
                )
                    .then(({ uri }) => {
                        this.loadLocal(Platform.OS === 'ios' ? uri : uri);
                    })
                    .catch(e => {
                        console.log('Image loading error:', e);
                        // if the online download fails, load the local version
                        this.loadLocal(`${FileSystem.documentDirectory + fileName}`);
                    });
            }
        })
    }

    runAudio = async (audio) => {
        await this.loadAudio(audio)
        let source = { uri: this.state.audiouri }

        const soundObject = new Audio.Sound();
        try {
            await soundObject.loadAsync(source);
            soundObject.setOnPlaybackStatusUpdate(async (status) => {
                if (status.didJustFinish === true) {
                    // audio has finished!
                    await this.props.changeAuto(true)
                }
            })
            await soundObject.playAsync();
        } catch (error) {
            // An error occurred!
        }
    }

    render() {
        return (
            <View style={styles.slide1} >
                <TouchableOpacity onPress={() => (this.runAudio(BASE_URL + '/api/member/file/' + this.props.item.audio))}>
                    <View style={{ position: "relative" }}>
                        <CachedImageOffline source={
                            BASE_URL + '/api/member/file/' + this.props.item.image
                        } style={{
                            height: windowWidth / 3.5,
                            width: windowWidth / 3.5,
                            resizeMode: 'cover',
                            borderRadius: 5
                        }} />
                        <Text style={{ position: "absolute", top: '1%', left: '1%' }}><MaterialCommunityIcons name="speaker-wireless" size={24} color="black" /></Text>
                    </View>
                    <Text style={styles.text}>{this.props.item.name}</Text>
                </TouchableOpacity>
            </View>
        )
    }
}

const styles = StyleSheet.create({
    slide1: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    text: {
        color: 'black',
        fontSize: 20,
        fontWeight: '300',
        textAlign: "center"
    },
    buttonText: {
        fontSize: 70,
        color: "red"
    },
    image: {
        resizeMode: "cover",
        height: windowHeight - 70,
        width: windowWidth,

    },
    container: {
        flex: 1,
        justifyContent: "center"
      },
      horizontal: {
        flexDirection: "row",
        justifyContent: "space-around",
        padding: 10
      }

})