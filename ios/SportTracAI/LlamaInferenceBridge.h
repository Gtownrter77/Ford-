#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * Objective-C++ bridge contract only.
 *
 * Implementation is intentionally nonfunctional until the shared C++ target and
 * llama.cpp are added to an Xcode project.
 */
@interface LlamaInferenceBridge : NSObject

- (BOOL)loadModelAtPath:(NSString *)modelPath error:(NSError * _Nullable * _Nullable)error;
- (NSString *)generateForPrompt:(NSString *)prompt
                       maxTokens:(NSInteger)maxTokens
                     temperature:(float)temperature
                           error:(NSError * _Nullable * _Nullable)error;
- (void)unloadModel;

@end

NS_ASSUME_NONNULL_END
