//
//  RCTCubeFlipper.h
//  RCTCubeFlipper
//
//  Created by Øyvind Hauge on 30/08/16.
//  Copyright © 2016 Oyvind Hauge. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <React/RCTComponent.h>


@protocol CubeFlipperDelegate <NSObject>
- (void)cubeFlipperOnPageScrollWithIndex:(NSInteger)index offset:(CGFloat)offset isDragging:(BOOL)isDragging;
- (void)cubeFlipperOnPageSelectedWithIndex:(NSInteger)index isDragging:(BOOL)isDragging;
- (void)cubeFlipperOnPageScrollStateChangedWithState:(NSString*)state;
@end


@interface RCTCubeFlipper : UIScrollView
@property (nonatomic, weak) id<CubeFlipperDelegate> cubeDelegate;
@property (nonatomic, assign) BOOL disableLeftScrolling;
@property (nonatomic, copy) RCTBubblingEventBlock onPageScroll;
@property (nonatomic, copy) RCTBubblingEventBlock onPageSelected;
@property (nonatomic, copy) RCTBubblingEventBlock onPageScrollStateChanged;

- (void)setPage:(NSInteger)page animationSpeed:(NSInteger)animationSpeed;
@end
