//
//  RCTCubeFlipper.m
//  RCTCubeFlipper
//
//  Created by Øyvind Hauge on 11/08/16.
//  Copyright © 2016 Oyvind Hauge. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTCubeFlipper.h"


@interface RCTCubeFlipper()	<UIScrollViewDelegate, UIGestureRecognizerDelegate>
@property (nonatomic, assign) CGFloat maxAngle;
@property (nonatomic, strong) NSMutableArray<UIView*> *childViews;
@property (nonatomic, strong) UIStackView *stackView;
@property (nonatomic, assign) NSUInteger index;
@end


@implementation RCTCubeFlipper


#pragma mark - init

- (id)init
{
	self = [super init];
	if(self)
	{
		self.maxAngle = 60.0f;
		self.childViews = [NSMutableArray new];
		
		// scrollview
		self.backgroundColor = [UIColor blackColor];
		self.showsHorizontalScrollIndicator = NO;
		self.showsVerticalScrollIndicator = NO;
		self.pagingEnabled = YES;
		self.bounces = NO;
		self.delegate = self;
		self.disableLeftScrolling = YES;
	}
	return self;
}


#pragma mark - view lifecycle

- (void)didMoveToSuperview
{
	[super didMoveToSuperview];
	
	// stackView
	self.stackView = [UIStackView new];
	self.stackView.translatesAutoresizingMaskIntoConstraints = NO;
	self.stackView.axis = UILayoutConstraintAxisHorizontal;
	[super addSubview:self.stackView];
	
	// constraints
	[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1 constant:0]];
	[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
	[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
	//[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1 constant:0]];
	[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1 constant:0]];
	[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1 constant:0]];
}

- (void)addSubview:(UIView *)view
{
	view.layer.masksToBounds = YES;
	[self.stackView addArrangedSubview:view];
	
	[self addConstraint:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
	
	[self.childViews addObject:view];
}

- (void)willRemoveSubview:(UIView *)subview
{
	[self.stackView removeArrangedSubview:subview];
	[self.childViews removeObject:subview];
}


#pragma mark - public

- (void)setPage:(NSInteger)page animationSpeed:(NSInteger)animationSpeed
{
	[self scrollToViewAtIndex:page animated:(animationSpeed > 0)];
}


#pragma mark - helpers

- (void)scrollToViewAtIndex:(NSUInteger)index animated:(BOOL)animated
{
	if(index < self.childViews.count) {
		CGFloat width = self.frame.size.width;
		CGFloat height = self.frame.size.height;
		CGRect frame = CGRectMake(index * width, 0, width, height);
		[self scrollRectToVisible:frame animated:animated];
	}
}

- (void)transformViewsInScrollView:(UIScrollView*)scrollView
{
	CGFloat xOffset = scrollView.contentOffset.x;
	CGFloat svWidth = scrollView.frame.size.width;
	CGFloat deg = self.maxAngle / self.bounds.size.width * xOffset;
	
	for(NSUInteger index = 0; index < self.childViews.count; index++)
	{
		UIView *view = self.childViews[index];
		deg = index == 0 ? deg : deg - self.maxAngle;
		CGFloat rad = deg * (M_PI / 180);
		
		CATransform3D transform = CATransform3DIdentity;
		transform.m34 = 1.0f / 500.0f;
		transform = CATransform3DRotate(transform, rad, 0, 1, 0);
		
		view.layer.transform = transform;
		
		CGFloat x = xOffset / svWidth > index ? 1.0 : 0.0;
		
		[self setAnchorPoint:CGPointMake(x, 0.5f) forView:view];
		[self applyShadowForView:view index:index];
	}
}

- (CGRect)frameForPoint:(CGPoint)origin size:(CGSize)size
{
	return CGRectMake(origin.x, origin.y, size.width, size.height);
}

- (void)applyShadowForView:(UIView *)view index:(NSUInteger)index
{
	CGFloat w = self.frame.size.width;
	CGFloat h = self.frame.size.height;
	
	CGRect r1 = [self frameForPoint:self.contentOffset size:self.frame.size];
	CGRect r2 = [self frameForPoint:CGPointMake(index * w, 0) size:CGSizeMake(w, h)];
	
	// Only show shadow on right-hand side
	if(r1.origin.x <= r2.origin.x)
	{
		CGRect intersection = CGRectIntersection(r1, r2);
		CGFloat intArea = intersection.size.width * intersection.size.height;
		CGRect rectUnion = CGRectUnion(r1, r2);
		CGFloat unionArea = rectUnion.size.width * rectUnion.size.height;
		
		view.layer.opacity = intArea / unionArea;
	}
}

- (void)setAnchorPoint:(CGPoint)anchorPoint forView:(UIView*)view
{
	CGPoint newPoint = CGPointMake(view.bounds.size.width * anchorPoint.x, view.bounds.size.height * anchorPoint.y);
	CGPoint oldPoint = CGPointMake(view.bounds.size.width * view.layer.anchorPoint.x, view.bounds.size.height * view.layer.anchorPoint.y);
	
	newPoint = CGPointApplyAffineTransform(newPoint, view.transform);
	oldPoint = CGPointApplyAffineTransform(oldPoint, view.transform);
	
	CGPoint position = view.layer.position;
	position.x -= oldPoint.x;
	position.x += newPoint.x;
	
	position.y -= oldPoint.y;
	position.y += newPoint.y;
	
	view.layer.position = position;
	view.layer.anchorPoint = anchorPoint;
}

						 
#pragma mark - <UIScrollViewDelegate>

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
	self.onPageScrollStateChanged(@{@"state":@"dragging"});
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
	self.index = scrollView.contentOffset.x / scrollView.frame.size.width;
	self.onPageSelected(@{@"position":@(self.index), @"manual":@(scrollView.dragging)});
	self.onPageScrollStateChanged(@{@"state":@"idle"});
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView
{
	self.index = scrollView.contentOffset.x / scrollView.frame.size.width;
	self.onPageSelected(@{@"position":@(self.index), @"manual":@(scrollView.dragging)});
	self.onPageScrollStateChanged(@{@"state":@"idle"});
}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView
{
	self.onPageScrollStateChanged(@{@"state":@"settling"});
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
	self.index = scrollView.contentOffset.x / scrollView.frame.size.width;
	[self transformViewsInScrollView:scrollView];
	self.onPageScroll(@{@"position":@(self.index), @"offset":@(scrollView.contentOffset.x), @"manual":@(scrollView.dragging)});
}


#pragma mark - <UIGestureRecognizerDelegate>

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
	// prevent user from scrolling back
	if(self.disableLeftScrolling) {
		if([gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]) {
			CGPoint translation = [((UIPanGestureRecognizer*)gestureRecognizer) translationInView:self];
			if(translation.x > 0) {
				return NO;
			}
		}
	}
	return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(nonnull UIGestureRecognizer *)otherGestureRecognizer
{
	return NO;
}


@end
