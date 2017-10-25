//
//  RCTCubeFlipper.m
//  RCTCubeFlipper
//
//  Created by Øyvind Hauge on 11/08/16.
//  Copyright © 2016 Oyvind Hauge. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTCubeFlipper.h"
#import "RCTCubeStackView.h"


NSString * const RCTCubeFlipperDidBecomeActive = @"RCTCubeFlipperDidBecomeActiveNotificaiton";


@interface RCTCubeFlipper()	<UIScrollViewDelegate, UIGestureRecognizerDelegate, CubeStackViewDelegate>
@property (nonatomic, assign) BOOL isViewInitialized;
@property (nonatomic, assign) BOOL shouldSendScrollEvents;
@property (nonatomic, assign) CGFloat maxAngle;
@property (nonatomic, strong) NSMutableArray<UIView*> *childViews;
@property (nonatomic, strong) RCTCubeStackView *stackView;
@property (nonatomic, assign) NSUInteger index;
@property (nonatomic, strong) NSLayoutConstraint *leftConstraint;
@end


@implementation RCTCubeFlipper


#pragma mark - init

- (id)init
{
	self = [super init];
	if(self)
	{
		self.shouldSendScrollEvents = YES;
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
		
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(cubeFlipperDidBecomeActive:) name:RCTCubeFlipperDidBecomeActive object:nil];
	}
	return self;
}

- (void)dealloc
{
	[[NSNotificationCenter defaultCenter] removeObserver:self name:RCTCubeFlipperDidBecomeActive object:nil];
}


#pragma mark - view lifecycle

- (void)layoutSubviews
{
	[super layoutSubviews];
	
	if(!self.isViewInitialized && self.childViews.count > 0 && self.superview)
	{
		// stackView
		if (@available(iOS 9.0, *))
		{
			self.stackView = [RCTCubeStackView new];
			self.stackView.cubeDelegate = self;
			self.stackView.translatesAutoresizingMaskIntoConstraints = NO;
			self.stackView.axis = UILayoutConstraintAxisHorizontal;
			[super addSubview:self.stackView];
			self.leftConstraint = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1 constant:0];
			[self addConstraint:self.leftConstraint];
			[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1 constant:0]];
			[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1 constant:0]];
			[self addConstraint:[NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1 constant:0]];
		}
		
		// subviews
		for(UIView *view in self.childViews)
		{
			view.layer.masksToBounds = YES;
			[self.stackView addArrangedSubview:view];
			
			[self addConstraint:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
		}
		
		self.isViewInitialized = YES;
		[self setNeedsLayout];
		[self layoutIfNeeded];
	}
}

- (void)willMoveToSuperview:(UIView *)newSuperview
{
	[self setNeedsLayout];
}

- (void)addSubview:(UIView *)view
{
	// check if this view was already added (react native calls addSubview when state changes in render method)
	if([self.childViews indexOfObject:view] != NSNotFound) {
		return;
	}
	[self.childViews addObject:view];
	
	// we need to wait for this view to be added to the superview. If that has already happened proceed by adding it to the view hierarchy + constraints
	if(self.isViewInitialized)
	{
		view.layer.masksToBounds = YES;
		[self.stackView addArrangedSubview:view];
		
		[self addConstraint:[NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeWidth multiplier:1 constant:0]];
	}
}


#pragma mark - public

- (void)setPage:(NSInteger)page animationSpeed:(NSInteger)animationSpeed
{
	[self scrollToViewAtIndex:page animated:(animationSpeed > 0)];
}


#pragma mark - notification center

- (void)cubeFlipperDidBecomeActive:(NSNotification*)notification
{
	
}


#pragma mark - helpers

- (void)scrollToViewAtIndex:(NSUInteger)index animated:(BOOL)animated
{
	self.userInteractionEnabled = NO; // prevent user interaction during transition
	
	if(index < self.childViews.count) {
		CGFloat width = self.frame.size.width;
		[self setContentOffset:CGPointMake(index * width, 0) animated:animated];
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

- (void)onScrollAnimationFinished:(UIScrollView*)scrollView
{
	self.index = scrollView.contentOffset.x / scrollView.frame.size.width;
	
	// notify react native
	if(self.shouldSendScrollEvents) {
		self.onPageSelected(@{@"position":@(self.index), @"manual":@(scrollView.dragging)});
		self.onPageScrollStateChanged(@{@"pageScrollState":@"idle"});
	}
}

						 
#pragma mark - <UIScrollViewDelegate>

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
	if(self.shouldSendScrollEvents) {
		self.onPageScrollStateChanged(@{@"pageScrollState":@"dragging"});
	}
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
	NSUInteger newIndex = scrollView.contentOffset.x / scrollView.frame.size.width;
	if(newIndex == self.index) {
		if(!self.userInteractionEnabled) {
			self.userInteractionEnabled = YES;
		}
	} else {
		self.userInteractionEnabled = NO;
	}
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
	[self onScrollAnimationFinished:scrollView];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView
{
	[self onScrollAnimationFinished:scrollView];
}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView
{
	if(self.shouldSendScrollEvents) {
		self.onPageScrollStateChanged(@{@"pageScrollState":@"settling"});
	}
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
	self.index = scrollView.contentOffset.x / scrollView.frame.size.width;
	if(self.shouldSendScrollEvents) {
		self.onPageScroll(@{@"position":@(self.index), @"offset":@(scrollView.contentOffset.x), @"manual":@(scrollView.dragging)});
	}
	
	[self transformViewsInScrollView:scrollView];
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


#pragma mark - <CubeStackViewDelegate>

- (void)cubeStackViewOnRemoveSubview:(UIView *)view
{
	NSUInteger index = [self.childViews indexOfObject:view];
	if(index != NSNotFound)
	{
		[self.childViews removeObject:view];
		
		// fix a layout issue where anchorpoints are broken after this point. fixed by setting left constraint.
		self.leftConstraint.constant = -self.frame.size.width * 0.5f;
		[self setNeedsLayout];
		//[self layoutIfNeeded];
		
		// the current index is further than the index of the view removed. We need to adjust the offset & the current index to display the correct new view
		if(self.index >= index)
		{
			self.shouldSendScrollEvents = NO;
			[self setContentOffset:CGPointMake(self.contentOffset.x - self.bounds.size.width, 0) animated:NO];
			self.shouldSendScrollEvents = YES;
			self.index--;
		}
	}
	
	self.userInteractionEnabled = YES;
}

@end
