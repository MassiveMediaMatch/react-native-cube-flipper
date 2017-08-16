import React from 'react';
import { requireNativeComponent, Platform, ViewPagerAndroid } from 'react-native';

class CubeFlipper extends React.Component {
	static propTypes = {
		...ViewPagerAndroid.propTypes
	}

	render() {
		return (
			<RCTCubeFlipper
				{...this.props}
			/>
		);
	}
}

var RCTCubeFlipper = requireNativeComponent('RCTCubeFlipper', CubeFlipper);

module.exports = CubeFlipper;