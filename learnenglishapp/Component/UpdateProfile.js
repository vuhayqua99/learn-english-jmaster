import React, { Component } from 'react';
import { Alert, Text, TextInput, TouchableOpacity, View } from 'react-native';
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import { Actions } from 'react-native-router-flux';

import { api, Authentication, styles, RenderProcessing } from '../Base';

class UpdateProfile extends Component {
  constructor(props) {
    super(props)
    this.state = {
      user: {},
      isLoading: false,
      name: '',
      address: '',
    }
  }

  componentDidMount() {
    this.getUser();
  }

  async getUser() {
    const user = await Authentication.loginUser();
    this.setState({
      user: user,
      name: user.name,
      address: user.address,
      nameInvalid: false,
      addressInvalid: false,
    })
  }

  async updateProfile() {
    this.setState({ isLoading: true })
    api.authFetch("/api/member/profile", "put", this.state.user,
      (response) => {
        this.setState({ isLoading: false })
        if (response.status == 401) {
          Authentication.logout();
          return;
        } else {
          this.state.user.name = this.state.name;
          this.state.user.address = this.state.address;
          Authentication.setAuth(this.state.user)
          Alert.alert('Thành công', 'Cập nhật hồ sơ thành công.',
            [
              {
                text: 'OK', onPress: () => {
                  Actions.pop({ refresh: true });
                }
              },
            ]
          )
        }
      }).catch(error => {
        this.setState({ isLoading: false })
      });
  }

  submit = () => {
    // if (!this.state.name || this.state.name.length == 0) {
    //   this.setState({ nameInvalid: true })
    // } else if (!this.state.address || this.state.address.length == 0) {
    //   this.setState({ addressInvalid: true })
    // } else {
    this.updateProfile()
    // }
  }

  renderProcessing = () => {
    if (this.state.isLoading) {
      return (<RenderProcessing />)
    }
    return null
  }

  render() {
    return (
      <KeyboardAwareScrollView contentContainerStyle={{ flexGrow: 1 }} enableOnAndroid={true} keyboardShouldPersistTaps="handled" extraScrollHeight={120}>
        <View style={{ flex: 1 }}>
          {this.renderProcessing()}
          <View style={styles.vContainer}>
            <Text style={styles.label}>Tên của bạn</Text>
            <TextInput style={[styles.inputs, this.state.nameInvalid ? { borderColor: '#F15931' } : { borderColor: '#c4c4c4' }]}
              placeholder="Tên của bạn"
              underlineColorAndroid='transparent'
              value={this.state.name}
              onChangeText={(name) => this.setState({ name: name, nameInvalid: false })} />
            {this.state.nameInvalid ?
              <Text style={styles.errorLabel}>Vui lòng nhập tên</Text>
              : null}

            {/* <Text style={styles.label}>Địa chỉ</Text>
            <TextInput style={[styles.inputs, this.state.addressInvalid ? { borderColor: '#F15931' } : { borderColor: '#c4c4c4' }]}
              placeholder="Địa chỉ"
              underlineColorAndroid='transparent'
              value={this.state.address}
              onChangeText={(address) => this.setState({ address: address, addressInvalid: false })} />
            {this.state.addressInvalid ?
              <Text style={styles.errorLabel}>Vui lòng nhập địa chỉ</Text>
              : null} */}

            <TouchableOpacity style={[styles.buttonContainer]} onPress={this.submit}>
              <Text style={styles.buttonText}>Cập nhật</Text>
            </TouchableOpacity>
          </View>
        </View>
      </KeyboardAwareScrollView>
    );
  }
}
export default UpdateProfile;
