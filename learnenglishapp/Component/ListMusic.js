import React, { Component } from 'react';
import { AsyncStorage, Dimensions, FlatList, Text, TouchableOpacity, View } from 'react-native';
import { Actions } from 'react-native-router-flux';
import Icon from 'react-native-vector-icons/Ionicons';

import { api, styles } from '../Base';

export default class ListMusic extends Component {

  constructor(props) {
    super(props)
    this.state = {
      musics: [],
      isLoading: false,
      retries: 0
    }
  }

  componentDidMount() {
    this.offlineLoad();
  }

  refresh = () => {
    this.loadData();
  }

  async loadData() {
    this.setState({ isLoading: true });
    api.authFetch("/api/member/music/list", "post", this.props.searchDTO,
      response => {
        response.json().then(responseJson => {
          this.setState({
            musics: responseJson.data,
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
    await AsyncStorage.setItem("music-" + this.props.searchDTO.unitId, JSON.stringify(data));
  }

  offlineLoad = async () => {
    this.setState({ isLoading: true });
    const data = await AsyncStorage.getItem("music-" + this.props.searchDTO.unitId);
    if (data) {
      this.setState({
        musics: JSON.parse(data),
        isLoading: false,
        retries: 0
      })
    } else {
      if (this.state.retries < 5) {
        this.loadData();
      }
    }
  }
  // END METHOD API
  //FLATLIST DATA

  renderRowItem = ({ item, index }) => {
    const { height, width } = Dimensions.get('window');
    const itemWidth = (width) / 2
    return (
      <TouchableOpacity onPress={() => Actions.MusicPlayer({ audio: item, currentIndex: index, title: item.name, unit: this.props.searchDTO.unit })} style={[{
        width: itemWidth, padding: 5, backgroundColor: "#fff", margin: 5,
        flexDirection: 'row', flex: 1,
        alignItems: 'center'
      }]}>
        <Icon
          name="ios-volume-high"
          size={25}
          color={"#000"}
        />
        <Text style={[{ color: '#000', marginLeft: 10 }]}>{item.name}</Text>
      </TouchableOpacity>
    )
  }

  renderNoDataComponent = () => {
    if (this.state.musics.length == 0 && this.state.isLoading == false) {
      return (
        <Text style={{ flex: 1, textAlign: 'center' }}>No musics!</Text>
      )
    }
    return null;
  }

  renderData() {
    return (
      <FlatList
        style={{ margin: 10 }}
        numColumns={2}                  // set number of columns 
        refreshing={this.state.isLoading}
        onRefresh={() => this.refresh()}
        data={this.state.musics}
        renderItem={this.renderRowItem}
        onEndReachedThreshold={0.5}
        keyExtractor={item => item.id.toString()}
        ListEmptyComponent={this.renderNoDataComponent}
      />
    )
  }
  //END FLATLIST

  ///RENDER VIEW
  render() {
    return (
      <View style={styles.vContainer}>
        {this.renderData()}
      </View>
    )
  }
}