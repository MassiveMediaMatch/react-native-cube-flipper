//
//  RCTCubeStackView.m
//  RCTCubeStackView
//
//  Created by Øyvind Hauge on 11/08/16.
//  Copyright © 2016 Oyvind Hauge. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTCubeStackView.h"


@interface RCTCubeStackView()

@end


@implementation RCTCubeStackView


#pragma mark - view lifecycle

- (void)didMoveToSuperview
{
	[super didMoveToSuperview];
}

- (void)willRemoveSubview:(UIView *)subview
{	
	// notify delegate that a view was removed
	if ([self.cubeDelegate respondsToSelector:@selector(cubeStackViewOnRemoveSubview:)]) {
		[self.cubeDelegate cubeStackViewOnRemoveSubview:subview];
	}
	
	[super willRemoveSubview:subview];
}

@end
