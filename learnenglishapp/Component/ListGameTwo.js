import React, { Component } from 'react'
import { AppRegistry, StyleSheet, Platform, Text, View, AsyncStorage, TouchableOpacity, Dimensions,ImageBackground } from 'react-native'
import Swiper from 'react-native-swiper'
import { api, BASE_URL, Authentication } from '../Base';
import { Audio } from 'expo-av'
import { CachedImageOffline } from './CachedImageOffline';
import * as FileSystem from 'expo-file-system'
import { AntDesign } from '@expo/vector-icons';
import { Octicons } from '@expo/vector-icons';
import { Feather } from '@expo/vector-icons';
const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

export default class ListGameTwo extends Component {

    constructor(props) {
        super(props)
        this.state = {
            gameTwos: [],
            audiouri: "",
            isLoading: false,
            retries: 0
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
        this.setState({ isLoading: true });
        api.authFetch("/api/member/game-two/list", "post", this.props.searchDTO,
            response => {
                response.json().then(responseJson => {
                    this.setState({
                        gameTwos: responseJson.data,
                        isLoading: false,
                        retries: 0
                    })
                    this.saveOfflineData(responseJson.data)
                }, () => {
                    this.offlineLoad()
                    var retries = this.state.retries + 1
                    this.setState({ retries: retries, isLoading: false })
                })
            }
        )
    }

    saveOfflineData = async (data) => {
        await AsyncStorage.setItem("gametwo-" + this.props.searchDTO.unitId, JSON.stringify(data));
    }

    offlineLoad = async () => {
        this.setState({ isLoading: true });
        const data = await AsyncStorage.getItem("gametwo-" + this.props.searchDTO.unitId);
        if (data) {
            this.setState({
                gameTwos: JSON.parse(data),
                isLoading: false,
                retries: 0
            })
        } else {
            if (this.state.retries < 5) {
                this.loadData();
            }
        }
    }

    render() {

        return (
            <ImageBackground source={require("../assets/background2.jpg")} style={styles.image}>
                <Swiper showsButtons={true}
                    showsPagination={false}
                    nextButton={<Text style={styles.buttonText}>›</Text>}
                    prevButton={<Text style={styles.buttonText}>‹</Text>}
                    loop={false}
                >
                    {this.state.gameTwos.map((item, index) => {
                        return <GameTwoItem item={item} key={index} {...this.props} {...this.state} />
                    })}
                </Swiper>
            </ImageBackground>
        )
    }
}

AppRegistry.registerComponent('learnenglishapp', () => ListGameTwo)

class GameTwoItem extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            correct: this.props.item.correct,
            isPlay: false
        }
    }

    loadLocal(uri) {
        this.setState({ audiouri: uri, isPlay: true });
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
        await Audio.Sound.createAsync(source, { shouldPlay: true })
    }

    check = async (show) => {
        if (this.state.isPlay) {
            if (show != "") {
                this.setState({ isshow: show })
            }
            (this.state.correct == show)
                ? await Audio.Sound.createAsync(require('../assets/true.mp3'), { shouldPlay: true }) : await Audio.Sound.createAsync(require('../assets/false2.mp3'), { shouldPlay: true })

        }


    }

    render() {
        return (
            <View style={styles.slide1} >
                <TouchableOpacity onPress={() => (this.runAudio(BASE_URL + '/api/member/file/' + this.props.item.audio))} style={{ marginTop: windowWidth / 50 }}>
                    <AntDesign name="questioncircleo" size={30} color="black" />
                </TouchableOpacity>
                <View style={{ flexDirection: "row", justifyContent: "center", alignContent: "center", margin: windowWidth / 20 }}>
                    <TouchableOpacity onPress={() => this.check(1)}>
                        <CachedImageOffline source={
                            BASE_URL + '/api/member/file/' + this.props.item.image1
                        } style={{
                            height: windowWidth / 5,
                            width: windowWidth / 5,
                            margin: windowWidth / 100,
                            resizeMode: 'cover',
                            borderRadius: 5
                        }} />
                        {(this.state.isshow == 1) ? ((this.state.correct == 1) ? <View style={{ position: "absolute", top: '10%', left: "10%" }}><Octicons name="check" size={28} color="#319e21" /></View> : <View style={{ position: "absolute", top: "10%", left: "10%" }}><Feather name="x" size={28} color="#ed6f55" /></View>) : null}
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => this.check(2)} style={{ position: "relative" }}>
                        <CachedImageOffline source={
                            BASE_URL + '/api/member/file/' + this.props.item.image2
                        } style={{
                            height: windowWidth / 5,
                            width: windowWidth / 5,
                            margin: windowWidth / 100,
                            resizeMode: 'cover',
                            borderRadius: 5
                        }} />
                        {(this.state.isshow == 2) ? ((this.state.correct == 2) ? <View style={{ position: "absolute", top: '10%', left: "10%" }}><Octicons name="check" size={28} color="#319e21" /></View> : <View style={{ position: "absolute", top: "10%", left: "10%" }}><Feather name="x" size={28} color="#ed6f55" /></View>) : null}
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => this.check(3)}>
                        <CachedImageOffline source={
                            BASE_URL + '/api/member/file/' + this.props.item.image3
                        } style={{
                            height: windowWidth / 5,
                            width: windowWidth / 5,
                            margin: windowWidth / 100,
                            resizeMode: 'cover',
                            borderRadius: 5
                        }} />
                        {(this.state.isshow == 3) ? ((this.state.correct == 3) ? <View style={{ position: "absolute", top: '10%', left: "10%" }}><Octicons name="check" size={28} color="#319e21" /></View> : <View style={{ position: "absolute", top: "10%", left: "10%" }}><Feather name="x" size={28} color="#ed6f55" /></View>) : null}
                    </TouchableOpacity>
                    <TouchableOpacity onPress={() => this.check(4)}>
                        <CachedImageOffline source={
                            BASE_URL + '/api/member/file/' + this.props.item.image4
                        } style={{
                            height: windowWidth / 5,
                            width: windowWidth / 5,
                            margin: windowWidth / 100,
                            resizeMode: 'cover',
                            borderRadius: 5
                        }} />
                        {(this.state.isshow == 4) ? ((this.state.correct == 4) ? <View style={{ position: "absolute", top: '10%', left: "10%" }}><Octicons name="check" size={28} color="#319e21" /></View> : <View style={{ position: "absolute", top: "10%", left: "10%" }}><Feather name="x" size={28} color="#ed6f55" /></View>) : null}
                    </TouchableOpacity>

                </View>
            </View>
        )
    }
}

const styles = StyleSheet.create({
    slide1: {
        flex: 1,
        //justifyContent: 'center',
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
        resizeMode:"cover",
        width:windowWidth,
        height: windowHeight-70,
    },

})