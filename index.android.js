import React from 'react';
import { requireNativeComponent, ViewPagerAndroid } from 'react-native';

export default class CubeFlipper extends ViewPagerAndroid {
	render() {
		return (
			<RCTCubeFlipper
				{...this.props}
			/>
		);
	}
}

const RCTCubeFlipper = requireNativeComponent('RCTCubeFlipper', ViewPagerAndroid);
