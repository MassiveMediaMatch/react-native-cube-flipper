import React from 'react';
import { requireNativeComponent, UIManager, findNodeHandle, Keyboard, View } from 'react-native';
import PropTypes from 'prop-types';

const CUBEFLIPPER_REF = 'cubeFlipper';

class CubeFlipper extends React.Component {
	props: {
		onPageScroll?: Function,
		onPageScrollStateChanged?: Function,
		onPageSelected?: Function,
		peekEnabled?: boolean,
		keyboardDismissMode?: 'none' | 'on-drag',
		scrollEnabled?: boolean,
	};

	static propTypes = {
		...View.propTypes,

		/**
		 * Executed when transitioning between pages (ether because of animation for
		 * the requested page change or when user is swiping/dragging between pages)
		 * The `event.nativeEvent` object for this callback will carry following data:
		 *  - position - index of first page from the left that is currently visible
		 *  - offset - value from range [0,1) describing stage between page transitions.
		 *    Value x means that (1 - x) fraction of the page at "position" index is
		 *    visible, and x fraction of the next page is visible.
		 */
		onPageScroll: PropTypes.func,

		/**
		 * Function called when the page scrolling state has changed.
		 * The page scrolling state can be in 3 states:
		 * - idle, meaning there is no interaction with the page scroller happening at the time
		 * - dragging, meaning there is currently an interaction with the page scroller
		 * - settling, meaning that there was an interaction with the page scroller, and the
		 *   page scroller is now finishing it's closing or opening animation
		 */
		onPageScrollStateChanged: PropTypes.func,

		/**
		 * This callback will be called once ViewPager finish navigating to selected page
		 * (when user swipes between pages). The `event.nativeEvent` object passed to this
		 * callback will have following fields:
		 *  - position - index of page that has been selected
		 */
		onPageSelected: PropTypes.func,

		/**
		 * Determines whether the keyboard gets dismissed in response to a drag.
		 *   - 'none' (the default), drags do not dismiss the keyboard.
		 *   - 'on-drag', the keyboard is dismissed when a drag begins.
		 */
		keyboardDismissMode: PropTypes.oneOf([
			'none', // default
			'on-drag'
		]),

		/**
		* When false, the content does not scroll.
		* The default value is true.
		*/
		scrollEnabled: PropTypes.bool,

		/**
		 * Whether enable showing peekFraction or not. If this is true, the preview of
		 * last and next page will show in current screen. Defaults to false.
		 */
		peekEnabled: PropTypes.bool
	};

	getInnerViewNode = (): ReactComponent => {
		return this.refs[CUBEFLIPPER_REF].getInnerViewNode();
	};

	_childrenWithOverridenStyle = (): Array => {
		// Override styles so that each page will fill the parent. Native component
		// will handle positioning of elements, so it's not important to offset
		// them correctly.
		return React.Children.map(this.props.children, (child) => {
			if (!child) {
				return null;
			}

			const newProps = {
				...child.props,
				style: [child.props.style, {
					position: 'absolute',
					left: 0,
					top: 0,
					right: 0,
					bottom: 0,
					width: undefined,
					height: undefined
				}],
				collapsable: false
			};

			if (child.type && child.type.displayName && (child.type.displayName !== 'RCTView') && (child.type.displayName !== 'View')) {
				console.warn(`Each ViewPager child must be a <View>. Was ${child.type.displayName}`);
			}

			return React.createElement(child.type, newProps);
		});
	};

	_onPageScroll = (e: Event) => {
		if (this.props.onPageScroll) {
			this.props.onPageScroll(e);
		}

		if (this.props.keyboardDismissMode === 'on-drag') {
			Keyboard.dismiss();
		}
	};

	_onPageScrollStateChanged = (e: Event) => {
		if (this.props.onPageScrollStateChanged) {
			this.props.onPageScrollStateChanged(e.nativeEvent.pageScrollState);
		}
	};

	_onPageSelected = (e: Event) => {
		if (this.props.onPageSelected) {
			this.props.onPageSelected(e);
		}
	};

	/**
	* A helper function to scroll to a specific page in the ViewPager.
	* The transition between pages will be animated.
	*/
	setPage = (selectedPage: number) => {
		UIManager.dispatchViewManagerCommand(
			findNodeHandle(this),
			UIManager.RCTCubeFlipper.Commands.setPage,
			[selectedPage],
		);
	};

	/**
	* A helper function to scroll to a specific page in the ViewPager.
	* The transition between pages will *not* be animated.
	*/
	setPageWithoutAnimation = (selectedPage: number) => {
		UIManager.dispatchViewManagerCommand(
			findNodeHandle(this),
			UIManager.RCTCubeFlipper.Commands.setPageWithoutAnimation,
			[selectedPage],
		);
	};

	render() {
		return (
			<RCTCubeFlipper
				{...this.props}
				ref={CUBEFLIPPER_REF}
				style={this.props.style}
				onPageScroll={this._onPageScroll}
				onPageScrollStateChanged={this._onPageScrollStateChanged}
				onPageSelected={this._onPageSelected}
				children={this._childrenWithOverridenStyle()}
			/>
		);
	}
}

const RCTCubeFlipper = requireNativeComponent('RCTCubeFlipper', CubeFlipper);

module.exports = CubeFlipper;
