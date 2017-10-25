//
//  RCTCubeStackView.h
//  RCTCubeFlipper
//
//  Created by Øyvind Hauge on 30/08/16.
//  Copyright © 2016 Oyvind Hauge. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <React/RCTComponent.h>


@protocol CubeStackViewDelegate <NSObject>
- (void)cubeStackViewOnRemoveSubview:(UIView*)view;
@end


@interface RCTCubeStackView : UIStackView
@property (nonatomic, weak) id<CubeStackViewDelegate> cubeDelegate;
@end
