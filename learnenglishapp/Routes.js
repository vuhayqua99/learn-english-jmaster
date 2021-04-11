import React from 'react';
import { Image, Platform, StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { Router, Scene, Stack, Actions } from 'react-native-router-flux';
import { MaterialIcons } from '@expo/vector-icons';

import Home from './Component/Home';
import ListUnit from './Component/ListUnit';
import Login from './Component/Login';
import MusicPlayer from './Component/MusicPlayer';
import Profile from './Component/Profile';
import UnitTabLayout from './Component/UnitTabLayout';
import UpdateProfile from './Component/UpdateProfile';
import VideoPlayer from './Component/VideoPlayer';
import Register from './Component/Register';


const Routes = () => {
      return (
            <Router navigationBarStyle={styles.navBar} titleStyle={{ color: '#e60604', textTransform: 'uppercase', fontSize: 14 }}>
                  <Stack key="root" tintColor='#e60604' backTitle=" " >
                        <Scene key="login" component={Login} title="Đăng Nhập" />
                        <Scene key="home" component={Home} initial={true} renderTitle={() => { return <AppLogo />; }} backButtonImage={() => null} renderRightButton={LoginMenu} />
                        <Scene key="profile" component={Profile} title="Hồ sơ cá nhân" />
                        <Scene key="register" component={Register} title="Đăng ký" />
                        <Scene key="MusicPlayer" component={MusicPlayer} title="Music" />
                        <Scene key="VideoPlayer" component={VideoPlayer} title="Video" hideNavBar={true} />
                        <Scene key="listUnit" component={ListUnit} title="Unit" renderTitle={() => { return <AppLogo />; }} backButtonImage={() => null} renderRightButton={RightMenu} />
                        <Scene key="UnitTabLayout" component={UnitTabLayout} title="Unit" hideNavBar={true} />
                        <Scene key="updateProfile" component={UpdateProfile} title="Cập nhật hồ sơ" />
                  </Stack>
            </Router>
      )
}

const RightMenu = () => {
      return (
            <TouchableOpacity
                  style={{ height: 30, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: '#e60604' }}
                  onPress={() => Actions.profile()}
            >
                  <MaterialIcons
                        name="person"
                        size={25}
                        color="#fff"
                  />
                  <Text style={{ color: '#fff' }}>Hồ Sơ Cá Nhân</Text>
            </TouchableOpacity>
      )
}

const LoginMenu = () => {
      return (
            <TouchableOpacity
                  style={{ height: 30, flexDirection: 'row', alignItems: 'center', justifyContent: 'center', backgroundColor: '#e60604' }}
                  onPress={() => Actions.login()}
            >
                  <MaterialIcons
                        name="person"
                        size={25}
                        color="#fff"
                  />
                  <Text style={{ color: '#fff' }}>Đăng Nhập</Text>
            </TouchableOpacity>
      )
}

const AppLogo = () => {
      return (
            <View style={styles.logo}>
                  <Image source={require('./assets/icon.png')}
                        style={{
                              width: 30, height: 30, borderRadius: 0
                        }} />
            </View>
      );
};


const styles = StyleSheet.create({
      logo: {
            flex: 1,
            alignItems: 'center',
            marginLeft: Platform.OS === 'android' ? -50 : 0
      },
      navBar: {
            flex: 1,
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: '#fff9af', // changing navbar color
            borderBottomColor: 'transparent',
            borderBottomWidth: 0,
            color: '#e60604',
            textAlign: 'center',
            height: 30,
      }
})

export default Routes