//
//  RNCubeTransitionManager.m
//  RNCubeTransition
//
//  Created by Thomas Lackemann on 12/21/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CubeFlipperManager.h"
#import "CubeFlipper.h"

@implementation CubeFlipperManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [[CubeFlipper alloc] init];
}

@end
