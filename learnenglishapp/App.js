import { Notifications } from 'expo';
import * as Permissions from 'expo-permissions';
import React, { Component } from 'react';
import { Platform, Text, TextInput, View, StatusBar } from 'react-native';

import { DeviceToken } from './Base';
import Routes from './Routes.js';


console.disableYellowBox = true;
console.ignoredYellowBox = true;

Text.defaultProps = Text.defaultProps || {};
TextInput.defaultProps = Text.defaultProps || {};
Text.defaultProps.allowFontScaling = false;
TextInput.defaultProps.allowFontScaling = false;
export default class App extends Component {
  registerForPushNotificationsAsync = async () => {
    const { status: existingStatus } = await Permissions.getAsync(
      Permissions.NOTIFICATIONS
    );
    let finalStatus = existingStatus;

    if (existingStatus !== 'granted') {
      const { status } = await Permissions.askAsync(Permissions.NOTIFICATIONS);
      finalStatus = status;
    }

    // Stop here if the user did not grant permissions
    if (finalStatus !== 'granted') {
      return;
    }

    // Get the token that uniquely identifies this device
    let token = await Notifications.getExpoPushTokenAsync();
    await DeviceToken.setTokenDevice(token)
  }

  componentDidMount() {
    this.registerForPushNotificationsAsync();
    if (Platform.OS === 'android') {
      Notifications.createChannelAndroidAsync('notification', {
        name: 'notification',
        priority: 'max',
        vibrate: [0, 250, 250, 250],
        sound: true
      });
    }
  }
  render() {
    return (
      <View style={{ flex: 1 }}>
        <StatusBar hidden={ true} />
        <Routes />
      </View>
    );
  }
}
