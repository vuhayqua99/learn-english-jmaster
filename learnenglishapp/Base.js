import React, { Component } from 'react';
import { ActivityIndicator, AsyncStorage, StyleSheet, View, Alert } from 'react-native';
import { Actions } from 'react-native-router-flux';
import * as FileSystem from 'expo-file-system'

export const BASE_URL ="http://192.168.225.161:8080";
///"http://192.168.1.207:8080"; 192.168.1.205
// "http://150.95.111.161";

const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
export const Base64 = {
  btoa: (input = '') => {
    let str = input;
    let output = '';

    for (let block = 0, charCode, i = 0, map = chars;
      str.charAt(i | 0) || (map = '=', i % 1);
      output += map.charAt(63 & block >> 8 - i % 1 * 8)) {

      charCode = str.charCodeAt(i += 3 / 4);

      if (charCode > 0xFF) {
        throw new Error("'btoa' failed: The string to be encoded contains characters outside of the Latin1 range.");
      }

      block = block << 8 | charCode;
    }

    return output;
  }
}

export const Authentication = {
  setAuth: async (user) => {
    await AsyncStorage.setItem("user", JSON.stringify(user));
  },
  loginUser: async () => {
    const json = await AsyncStorage.getItem("user");
    if (json) {
      return JSON.parse(json);
    }
    return
  },
  logout: async () => {
    await AsyncStorage.removeItem("user");
    Actions.reset("login")
  }
}

export const DeviceToken = {
  setTokenDevice: async (deviceId) => {
    await AsyncStorage.setItem("deviceId", deviceId);
  },
  getTokenDevice: async () => {
    return await AsyncStorage.getItem("deviceId");
  }
}

export const api = {
  authFetch: async (path, method, data, handleSuccess, handleFail) => {
    const user = await Authentication.loginUser()

    if (!user) {
      Authentication.logout()
      return
    }

    fetch(BASE_URL + path, {
      method: method,
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        Authorization: "Basic " + user.base64
      },
      body: JSON.stringify(data)
    })
      .then(async response => {
        if (response.status == 200) {
          handleSuccess(response)
        } else if (response.status == 401) {
          await Authentication.logout();
        } else {
          handleFail(response.status);
        }
      })
      .catch(error => {
        console.log(error);
        handleFail()
      });
  },
  authFetchNoAuth: async (path, method, data, handleSuccess, handleFail) => {
    fetch(BASE_URL + path, {
      method: method,
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data)
    })
      .then(async response => {
        if (response.status == 200) {
          handleSuccess(response)
        } else if (response.status == 401) {
          await Authentication.logout();
        } else {
          handleFail(response.status);
        }
      })
      .catch(error => {
        console.log(error);
        handleFail()
      });
  },
  authFetchHead: async (source, handleSuccess, handleFail) => {
    const user = await Authentication.loginUser()

    if (!user) {
      Authentication.logout()
      return
    }

    fetch(source, {
      method: "head",
      headers: {
        Authorization: "Basic " + user.base64
      }
    })
      .then(async response => {
        if (response.status == 200) {
          handleSuccess(response)
        } else {
          handleFail(response.status);
        }
      })
      .catch(error => {
        console.log(error);
        handleFail()
      });
  }
}

//render processing icon loading
export class RenderProcessing extends Component {
  render() {
    return (
      <View
        style={{
          backgroundColor: "#000",
          opacity: 0.5,
          position: "absolute",
          zIndex: 99999,
          width: "100%",
          height: "100%",
          justifyContent: 'center'
        }}
      >
        <ActivityIndicator size="small" color="white" />
      </View>
    )
  }
}

export const CacheManager = {
  loadCacheFile: async (source, callback) => {
    let user = await Authentication.loginUser()

    if (!user) {
      user = { base64: "" }
    }

    const fileName = source.slice((source.lastIndexOf("/") - 1 >>> 0) + 2)

    await FileSystem.getInfoAsync(`${FileSystem.documentDirectory + fileName}`).then(async (data) => {
      if (data.exists) {
        callback(FileSystem.documentDirectory + fileName);
      } else {
        const callbackProgress = downloadProgress => {
          const progress = downloadProgress.totalBytesWritten / downloadProgress.totalBytesExpectedToWrite;
          callback(undefined, progress)
        };

        const downloadResumable = FileSystem.createDownloadResumable(
          source,
          FileSystem.documentDirectory + fileName,
          {
            headers: {
              Authorization: "Basic " + user.base64
            },
            cache: true
          },
          callbackProgress
        );

        try {
          const { uri } = await downloadResumable.downloadAsync();
          callback(uri)
        } catch (e) {
          // callback(FileSystem.documentDirectory + fileName);
          alert('Xảy ra lỗi tải file. Vui lòng thử lại.');
        }
      }
    })
  }
}
export const styles = StyleSheet.create({
  text: {
    marginBottom: 10,
    textAlign: "left",
    fontSize: 14,
    fontWeight: "bold",
    marginLeft: 5,
    color: 'red'
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch'
  },
  vContainer: {
    flex: 1,
    flexDirection: 'column'
  },
  hContainer: {
    flex: 1,
    flexDirection: 'row',
  },
  inputs: {
    backgroundColor: "#FFFFFF",
    padding: 10,
    borderRadius: 5,
    margin: 10,
    borderWidth: 1,
    borderColor: "#C4C4C4"
  },
  buttonContainer: {
    justifyContent: 'center',
    borderRadius: 5,
    backgroundColor: "#e60604",
    margin: 10,
    shadowOpacity: 1,
    shadowRadius: 4,
    padding: 10,
    shadowColor: 'rgba(143, 184, 140, 0.1)',
    shadowOffset: { height: 0, width: 0 },
  },
  buttonText: {
    color: '#fff',
    textAlign: 'center',
    fontSize: 14,
    lineHeight: 17,
    fontWeight: 'bold',
    textTransform: 'uppercase'
  },
  textArea: {
    height: 100,
    backgroundColor: "#FFFFFF",
    padding: 10,
    margin: 10,
    borderRadius: 5,
    textAlignVertical: "top",
    borderWidth: 1,
    borderColor: "#f0f0f0"
  },
  searchInput: {
    height: 35,
    backgroundColor: "#FFFFFF",
    padding: 5,
    borderRadius: 5,
    margin: 10,
    borderWidth: 1,
    borderColor: "#f0f0f0"
  },
  itemHeadText: {
    color: "#e60604",
    fontSize: 14,
    fontWeight: "bold"
  },
  item: {
    backgroundColor: "#fff",
    marginLeft: 10,
    marginRight: 10,
    marginBottom: 5,
    marginTop: 5,
    padding: 10,
    borderRadius: 5
  },
  itemHead: {
    flex: 1,
    flexDirection: "row",
    borderBottomWidth: 1,
    borderBottomColor: "#D5d5d5"
  },
  itemBody: {
    flex: 1,
    flexDirection: "row",
    borderBottomWidth: 1,
    borderBottomColor: "#D5d5d5",
    paddingBottom: 5,
    paddingTop: 5
  },
  itemFoot: {
    flex: 1,
    flexDirection: "row",
  },
  itemImage: {
    backgroundColor: "#fff",
    margin: 10
  },
  info: {
    fontSize: 13,
    color: "#262626",
    lineHeight: 18,
    flex: 1
  },
  title: {
    color: "#089101",
    fontSize: 13,
    lineHeight: 18
  },
  fab: {
    backgroundColor: "transparent",
    position: "absolute",
    right: 10,
    bottom: 10,
    width: 45,
    height: 45,
    alignItems: "center"
  },
  textRed: {
    color: '#247158'
  },
  itemIconButton: {
    marginLeft: 20
  },
  autocompleteContainer: {
    backgroundColor: '#ffffff',
    flex: 1,
    left: 0,
    position: 'absolute',
    right: 0,
    top: 0,
    bottom: 0,
    height: 45,
    zIndex: 1
  },
  buttonList: {
    flexDirection: "row",
    backgroundColor: "#ffffff",
    alignItems: "center",
    height: 40,
    borderBottomWidth: 1,
    borderBottomColor: '#D5d5d5',

  },
  active: {
    textAlign: "center",
    paddingTop: 10,
    flex: 1,
    borderBottomWidth: 2,
    borderBottomColor: '#e60604',
  },
  inactive: {
    paddingTop: 10,
    textAlign: "center",
    flex: 1
  },
  activeText: {
    color: "#e60604",
    fontWeight: "500"
  },
  inactiveText: {
    color: "#000",
  },
  backgroundImage: {
    flex: 1,
    position: 'absolute',
    width: '100%',
    height: '100%',
    resizeMode: 'contain'
  },
  boxContainer: {
    margin: 30,
    backgroundColor: '#fff',
    borderRadius: 4,
    padding: 10,
    shadowOpacity: 1,
    shadowRadius: 4,
    shadowColor: 'rgba(18, 108, 184, 0.15)',
    shadowOffset: { height: 0, width: 0 },
  },
  label: {
    fontSize: 14,
    lineHeight: 19,
    color: '#59616B',
    marginLeft: 10,
    marginRight: 10,
    marginTop: 10
  },
  headingText: {
    fontSize: 24,
    lineHeight: 29,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 30,
    marginBottom: 30,
    color: '#16384F'
  },
  errorLabel: {
    fontStyle: 'italic',
    fontSize: 12,
    lineHeight: 16,
    textAlign: 'left',
    color: '#F15931',
    marginLeft: 10,
    marginRight: 10
  }
});