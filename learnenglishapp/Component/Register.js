import React, { Component } from 'react';
import { Alert, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import { Actions } from 'react-native-router-flux';

import { BASE_URL, RenderProcessing, styles } from '../Base';

export default class Register extends Component {
  constructor(props) {
    super(props)
    this.state = {
      phone: '',
      password: '',
      repassword: '',
      name: '',
      isLoading: false,
      registerFail: false,
      phoneInvalid: false,
      passwordInvalid: false,
      nameInvalid: false,
      addressInvalid: false,
      repasswordInvalid: false,
    }
  }

  submit = () => {
    if (this.state.phone.length == 0) {
      this.setState({ phoneInvalid: true })
    } else if (this.state.password.length == 0) {
      this.setState({ passwordInvalid: true })
    } else if (this.state.password != this.state.repassword) {
      this.setState({ repasswordInvalid: true })
    } else {
      this.register()
    }
  }


  register() {
    this.setState({ isLoading: true })
    const customer = this.state

    fetch(BASE_URL + "/api/user/register", {
      method: "post",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      },
      body: JSON.stringify(customer)
    }).then(response => {
      this.setState({ isLoading: false })
      if (response.status && response.status == 409) {
        return { status: 409 };
      }
      return response.json()
    })
      .then(responseJson => {
        if (responseJson.status && responseJson.status == 409) {
          this.setState({ registerFail: true })
        } else {
          Alert.alert('Thành công', 'Đăng ký tài khoản thành công. Vui lòng đăng nhập.',
            [
              {
                text: 'OK', onPress: () => {
                  Actions.pop();
                }
              },
            ]
          )
        }
      }).catch(error => {
        console.log(error);
      });
  }

  renderProcessing = () => {
    if (this.state.isLoading) {
      return <RenderProcessing />
    }
    return null
  }

  render() {
    return (
      <KeyboardAwareScrollView
        contentContainerStyle={{ flexGrow: 1 }}
        enableOnAndroid={true}
        keyboardShouldPersistTaps="handled"
      >
        {this.renderProcessing()}
        <View style={styles.boxContainer}>
          <View style={[styles.vContainer]}>
            <Text style={styles.headingText}>ĐĂNG KÝ</Text>
            {this.state.registerFail ?
              <Text style={styles.errorLabel}>Tài khoản đã tồn tại!</Text>
              : null}

            <Text style={styles.label}>Mã tài khoản</Text>
            <TextInput style={[styles.inputs, this.state.phoneInvalid ? { borderColor: '#F15931' } : { borderColor: '#c4c4c4' }]}
              placeholder="Mã tài khoản"
              underlineColorAndroid='transparent'
              value={this.state.phone}
              onChangeText={(phone) => this.setState({ phone: phone, registerFail: false, phoneInvalid: false })} />
            {this.state.phoneInvalid ?
              <Text style={styles.errorLabel}>Vui lòng nhập giá trị</Text>
              : null}

            <Text style={styles.label}>Mật khẩu</Text>
            <TextInput style={[styles.inputs, this.state.passwordInvalid ? { borderColor: '#F15931' } : { borderColor: '#c4c4c4' }]}
              placeholder="Mật khẩu"
              secureTextEntry={true}
              underlineColorAndroid='transparent'
              value={this.state.password}
              onChangeText={(password) => this.setState({ password: password, registerFail: false, passwordInvalid: false })}
            />
            {this.state.passwordInvalid ?
              <Text style={styles.errorLabel}>Vui lòng nhập giá trị</Text>
              : null}

            <Text style={styles.label}>Nhập lại mật khẩu</Text>
            <TextInput style={[styles.inputs, this.state.repasswordInvalid ? { borderColor: '#F15931' } : { borderColor: '#c4c4c4' }]}
              placeholder="Nhập lại mật khẩu"
              secureTextEntry={true}
              underlineColorAndroid='transparent'
              value={this.state.repassword}
              onChangeText={(repassword) => this.setState({ repassword: repassword, registerFail: false, repasswordInvalid: false })}
            />
            {this.state.repasswordInvalid ?
              <Text style={styles.errorLabel}>Mật khẩu không khớp</Text>
              : null}
            <TouchableOpacity style={[styles.buttonContainer, styles.loginButton]} onPress={this.submit}>
              <Text style={styles.buttonText}>Đăng ký</Text>
            </TouchableOpacity>
            <View style={{ marginTop: 20, alignItems: 'center', flexDirection: 'row', justifyContent: 'center' }}>
              <Text style={styles.label}>Bạn đã có tài khoản?</Text>
              <TouchableOpacity onPress={() => Actions.pop()}>
                <Text style={{
                  fontSize: 14,
                  marginTop: 10,
                  lineHeight: 19,
                  fontWeight: 'bold',
                  color: '#247158',
                  textTransform: 'uppercase'
                }}>Đăng nhập</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </KeyboardAwareScrollView>
    );
  }
}
