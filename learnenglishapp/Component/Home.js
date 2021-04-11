import React, { Component } from 'react';
import { AsyncStorage, Dimensions, FlatList, Image, Platform, Text, TouchableOpacity, View } from 'react-native';
import { Actions } from 'react-native-router-flux';

import { api, Authentication, BASE_URL, styles } from '../Base';

export default class Home extends Component {
  searchDTO = {
    length: 12,
    start: null,
    search: {
      value: ""
    },
    columns: [{ data: "id" }],
    order: [
      {
        column: 0,
        dir: "asc"
      }
    ]
  };
  constructor(props) {
    super(props);
    this.state = {
      units: [],
      isLoading: false,
      numberOfColumns: Platform.isPad ? 4 : 3,
      retries: 0
    }
  }

  componentDidMount() {
    this.checkAuthen()
  }

  checkAuthen = async () => {
    const user = await Authentication.loginUser()
    if (user) {
      Actions.reset("listUnit")
    } else {
      this.offlineLoad()
    }
  }

  refresh = () => {
    this.loadData()
  }

  async loadData() {
    this.setState({ isLoading: true });
    api.authFetchNoAuth("/api/unit/list", "post", this.searchDTO,
      response => {
        response.json().then(responseJson => {
          this.setState({
            units: responseJson.data,
            isLoading: false,
            retries: 0
          })
          //this.saveOfflineData(responseJson.data)
        })
      }, () => {
        // this.offlineLoad()
        // var retries = this.state.retries + 1
        // this.setState({
        //   retries: retries, isLoading: false
        // })
      }
    )
  }

  saveOfflineData = async (data) => {
    await AsyncStorage.setItem("units", JSON.stringify(data));
  }

  offlineLoad = async () => {
    this.setState({ isLoading: true });
    // const data = await AsyncStorage.getItem("units");
    // if (data) {
    //   let units = this.state.units
    //   if (this.state.refresh) {
    //     units = []
    //   }
    //   this.setState({
    //     retries: 0,
    //     units: units.concat(JSON.parse(data)),
    //     isLoading: false
    //   })
    // } else {
    //   if (this.state.retries < 5) {
    this.loadData();
    // }
    // }
  }
  // END METHOD API

  renderRowItem = ({ item }) => {
    const { height, width } = Dimensions.get('window');
    const itemWidth = (width) / this.state.numberOfColumns
    return (
      <TouchableOpacity onPress={() => Actions.login()} style={{
        flex: 1, marginBottom: 60, width: itemWidth,
        alignItems: 'center'
      }}>
        <Image
          style={{
            height: itemWidth - 80,
            width: itemWidth - 80,
            resizeMode: 'cover',
            borderRadius: 5
          }}
          source={{ uri: BASE_URL + '/file/' + item.image }}
        />
        <Text style={[styles.itemHeadText, { padding: 5, backgroundColor: '#fff', width: itemWidth - 80, textAlign: 'center' }]}>{item.name}</Text>
      </TouchableOpacity>
    )
  }
  renderNoDataComponent = () => {
    if (this.state.units.length == 0 && this.state.isLoading == false) {
      return (
        <Text style={{ flex: 1, textAlign: 'center' }}>No units! Contact admin to support!</Text>
      )
    }
    return null;
  }

  renderData() {
    return (
      <FlatList
        style={{ margin: 10 }}
        numColumns={this.state.numberOfColumns}                  // set number of columns 
        refreshing={this.state.isLoading}
        onRefresh={() => this.refresh()}
        data={this.state.units}
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
