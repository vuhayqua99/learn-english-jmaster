import React, { Component } from 'react';
import { AsyncStorage, Text, TouchableOpacity, View } from 'react-native';

import { styles } from '../Base';
import ListMusic from './ListMusic';
import ListVideo from './ListVideo';
import ListGameOne from './ListGameOne'
import ListGameTwo from './ListGameTwo';
import { AntDesign } from '@expo/vector-icons';
import { FontAwesome } from '@expo/vector-icons';
export default class UnitTabLayout extends Component {
  constructor(props) {
    super(props)
    this.searchDTO = {
      length: 12,
      start: null,
      columns: [{ data: "id" }],
      order: [{ column: 0, dir: "asc" }],
      search: {
        value: ""
      },
      unitId: this.props.unit.id,
      unit: this.props.unit
    }
    this.state = {
      tabId: this.props.tabId ? this.props.tabId : 1
    }
  }

  componentDidMount() {
    this.setState({
      tabId: this.props.tabId ? this.props.tabId : 1
    })
  }

  _onPressButton(tabId) {
    if (this.state.tabId != tabId) {
      this.searchDTO.status = tabId
      this.setState({ tabId: tabId })
    }
  }

  renderTabLayout() {
    const buttonStyle1 =
      this.state.tabId == 1 ? styles.active : styles.inactive;
    const textStyle1 =
      this.state.tabId == 1 ? styles.activeText : styles.inactiveText;

    const buttonStyle2 =
      this.state.tabId == 2 ? styles.active : styles.inactive;
    const textStyle2 =
      this.state.tabId == 2 ? styles.activeText : styles.inactiveText;

    const buttonStyle3 =
      this.state.tabId == 3 ? styles.active : styles.inactive;
    const textStyle3 =
      this.state.tabId == 3 ? styles.activeText : styles.inactiveText;

    const buttonStyle4 =
      this.state.tabId == 4 ? styles.active : styles.inactive;
    const textStyle4 =
      this.state.tabId == 4 ? styles.activeText : styles.inactiveText;

    return (
      <View style={styles.buttonList}>
        <TouchableOpacity onPress={text => this._onPressButton(1)} style={buttonStyle1}>
          <View style={{ alignItems: "center", height: "100%" }}>
            <Text style={textStyle1}>Audio</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity onPress={text => this._onPressButton(2)} style={buttonStyle2}>
          <View style={{ alignItems: "center", height: "100%" }}>
            <Text style={textStyle2}>Video</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity onPress={text => this._onPressButton(3)} style={buttonStyle3}>
          <View style={{ alignItems: "center", height: "100%" }}>
            <Text style={textStyle3}>Words</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity onPress={text => this._onPressButton(4)} style={buttonStyle4}>
          <View style={{ alignItems: "center", height: "100%" }}>
            <Text style={textStyle4}>Activity</Text>
          </View>
        </TouchableOpacity>
      </View>)
  }

  render() {
    return (
      <View style={{ flex: 1 }}>
        <Back {...this.props} searchDTO={this.searchDTO}></Back>
        {this.renderTabLayout()}
        <View style={{ flex: 1, height: 30 }}>
          {(this.state.tabId == 1) ?
            <ListMusic searchDTO={this.searchDTO} /> :
            (this.state.tabId == 2) ? <ListVideo searchDTO={this.searchDTO} /> : (this.state.tabId == 3) ? <ListGameOne searchDTO={this.searchDTO} /> : <ListGameTwo searchDTO={this.searchDTO} />}
        </View>
      </View>
    );
  }
}

class Back extends React.Component {
  constructor(props) {
    super(props)
  }
  refresh=async()=>{
      await AsyncStorage.removeItem("gameone-"+this.props.searchDTO.unitId)
      await AsyncStorage.removeItem("gametwo-"+this.props.searchDTO.unitId)
      await this.props.navigation.goBack()
  }
  render() {
    return (
      <View style={{ height: 30, backgroundColor: "#fff9af", paddingHorizontal: 20,flexDirection:"row", justifyContent:"space-between"}}>
        <TouchableOpacity onPress={() => this.props.navigation.goBack()}>
          <Text style={{ fontSize: 15, color: "red", fontWeight: "600" }}>  <AntDesign name="left" size={20} color="red" /> {this.props.unit.name}</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={ this.refresh}>
          <Text style={{ fontSize: 15, color: "red", fontWeight: "600" }}>  <FontAwesome name="refresh" size={20} color="red" /> Refresh</Text>
        </TouchableOpacity>
      </View>

    )
  }
}