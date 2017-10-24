//
//  RCTCubeFlipperManager.m
//  RCTCubeFlipper
//
//  Created by Thomas Lackemann on 12/21/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTCubeFlipperManager.h"
#import "RCTCubeFlipper.h"

@interface RCTCubeFlipperManager()
@property (nonatomic, strong) RCTCubeFlipper *cubeFlipper;
@end

@implementation RCTCubeFlipperManager

RCT_EXPORT_MODULE()
RCT_EXPORT_VIEW_PROPERTY(onPageScroll, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(onPageScrollStateChanged, RCTBubblingEventBlock);

- (UIView *)view {
	if(!self.cubeFlipper) {
		self.cubeFlipper = [RCTCubeFlipper new];
	}
	return self.cubeFlipper;
}

RCT_REMAP_METHOD(setPage, setPage:(nonnull NSNumber*)reactTag page:(NSInteger)page animationSpeed:(NSInteger)animationSpeed)
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[self.cubeFlipper setPage:page animationSpeed:animationSpeed];
	});
}

RCT_REMAP_METHOD(setPageWithoutAnimation, setPageWithoutAnimation:(nonnull NSNumber*)reactTag page:(NSInteger)page)
{
	dispatch_async(dispatch_get_main_queue(), ^{
		[self.cubeFlipper setPage:page animationSpeed:0];
	});
}


@end
