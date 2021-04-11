import React, { Component } from 'react'
import { View, Image, ActivityIndicator, Dimensions, Platform } from 'react-native'
import * as FileSystem from 'expo-file-system'
import { Authentication } from '../Base'

export class CachedImageOffline extends Component {
    state = {
        loading: true,
        failed: false,
        imguri: '',
        width: 300,
        height: 300,
        user: { base64: "" }
    }

    getLoginUser = async () => {
        const user = await Authentication.loginUser()
        if (user) {
            this.setState({ user: user })
        }
    }

    async componentDidMount() {
        await this.getLoginUser()
        const fileName = this.props.source.slice((this.props.source.lastIndexOf("/") - 1 >>> 0) + 2)
        const extension = fileName.slice((fileName.lastIndexOf(".") - 1 >>> 0) + 2)
        if ((extension.toLowerCase() !== 'jpg') && (extension.toLowerCase() !== 'png') && (extension.toLowerCase() !== 'gif')) {
            this.setState({ loading: false, failed: true })
        }
        await FileSystem.getInfoAsync(`${FileSystem.documentDirectory + fileName}`).then(async (data) => {
            if (data.exists) {
                this.loadLocal(`${FileSystem.documentDirectory + fileName}`);
            } else {
                await FileSystem.downloadAsync(
                    this.props.source,
                    `${FileSystem.documentDirectory + fileName}`,
                    {
                        headers: {
                            Authorization: "Basic " + this.state.user.base64
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
    loadLocal(uri) {
        Image.getSize(uri, (width, height) => {
            // once we have the original image dimensions, set the state to the relative ones
            this.setState({ imguri: uri, loading: false, width: Dimensions.get('window').width, height: (height / width) * Dimensions.get('window').width });
        }, (e) => {
            // As always include an error fallback
            console.log('getSize error:', e);
            this.setState({ loading: false, failed: true })
        })
    }
    render() {
        const { style } = this.props
        {
            if (this.state.loading) {
                // while the image is being checked and downloading
                return (
                    <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }}>
                        <ActivityIndicator
                            color='#fff'
                            size='small'
                        />
                    </View>
                );
            }
        }
        {
            if (this.state.failed) {
                // if the image url has an issue
                return <Image
                    style={[{ width: this.state.width, height: this.state.height }, style]}
                    source={require('../assets/logo.png')}
                />;
            }
        }
        // otherwise display the image
        return (
            <Image
                style={[{ width: this.state.width, height: this.state.height }, style]}
                source={{ uri: this.state.imguri }}
            />
        );
    }
}