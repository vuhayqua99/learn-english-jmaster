import { MaterialIcons } from '@expo/vector-icons';
import React, { Component } from 'react';
import { Image, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { Actions } from 'react-native-router-flux';

import { Authentication, BASE_URL, DeviceToken, RenderProcessing } from '../Base';

class Profile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      user: {},
      isLoading: false,
      point: ""
    };
  }

  async getLoginUser() {
    const user = await Authentication.loginUser();
    this.setState({ user: user });
  }

  componentDidMount() {
    this.getLoginUser();
  }

  componentWillReceiveProps() {
    this.getLoginUser();
  }

  async logout() {
    const token = await DeviceToken.getTokenDevice()
    this.setState({ isLoading: true });
    fetch(BASE_URL + "/api/member/logout", {
      method: "put",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
        Authorization: "Basic " + this.state.user.base64
      },
      body: "token=" + token
    }).then(response => {
      Authentication.logout()
    }).catch(error => {
      this.setState({ isLoading: false });
    })
  }

  renderProcessing = () => {
    if (this.state.isLoading) {
      return (<RenderProcessing />)
    }
    return null
  }

  render() {
    return (
      <ScrollView contentContainerStyle={{ flex: 1 }}>
        {this.renderProcessing()}
        <View style={styles.infoContainer}>
          <Image source={require('../assets/icon.png')} style={styles.avatar} />
          <Text style={styles.textStyle}>{this.state.user.name}</Text>
          <Text style={[styles.textInfo]}>{this.state.user.phone}</Text>
          <Text style={styles.textInfo}> {this.state.user.address} </Text>
        </View>
        <View style={{ marginTop: 20 }}>
          {/* <TouchableOpacity
            style={styles.buttonContainer}
            onPress={() => Actions.updateProfile()}
          >
            <MaterialIcons
              name="edit"
              size={25}
              color="#808080"
            />
            <Text style={styles.info}>Cập nhật hồ sơ</Text>
          </TouchableOpacity> */}
          <TouchableOpacity
            style={styles.buttonContainer}
            onPress={() => this.logout()}
          >
            <MaterialIcons
              name="exit-to-app"
              size={25}
              color="#808080"
            />
            <Text style={styles.info}>Đăng xuất</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    )
  }
}
export default Profile;

const styles = StyleSheet.create({
  avatar: {
    height: 80,
    width: 80,
    borderRadius: 40,
    resizeMode: 'contain',
    borderWidth: 3,
    borderColor: '#fff'
  },
  infoContainer: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: "#fff9af",
    height: 200,
    shadowOpacity: 1,
    shadowRadius: 10,
    shadowColor: '#fff9af',
    shadowOffset: { height: 0, width: 0 },
    borderRadius: 5
  },
  buttonContainer: {
    borderBottomWidth: 1,
    borderBottomColor: "#e2e2e2",
    padding: 10,
    flexDirection: 'row',
    alignItems: 'center',
  },
  textStyle: {
    marginTop: 10,
    marginBottom: 10,
    fontSize: 16,
    fontWeight: "bold",
    color: '#e60604'
  },
  textInfo: {
    fontSize: 14,
    color: '#e60604',
    marginBottom: 5,
    textAlign: 'center'
  },
  info: { fontSize: 14, color: '#59616B', marginLeft: 10 }
});
