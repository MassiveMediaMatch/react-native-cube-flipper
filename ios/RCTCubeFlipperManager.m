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

@implementation RCTCubeFlipperManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [[RCTCubeFlipper alloc] init];
}

@end
