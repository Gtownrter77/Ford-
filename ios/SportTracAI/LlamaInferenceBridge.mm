#import "LlamaInferenceBridge.h"

static NSString * const LlamaBridgeErrorDomain = @"com.sporttrac.localai.llama";

@implementation LlamaInferenceBridge

- (BOOL)loadModelAtPath:(NSString *)modelPath error:(NSError * _Nullable * _Nullable)error {
    if (error != nil) {
        *error = [NSError errorWithDomain:LlamaBridgeErrorDomain
                                      code:1
                                  userInfo:@{NSLocalizedDescriptionKey:
                                      @"LlamaInferenceBridge is a scaffold; no llama.cpp target is linked."}];
    }
    return NO;
}

- (NSString *)generateForPrompt:(NSString *)prompt
                       maxTokens:(NSInteger)maxTokens
                     temperature:(float)temperature
                           error:(NSError * _Nullable * _Nullable)error {
    if (error != nil) {
        *error = [NSError errorWithDomain:LlamaBridgeErrorDomain
                                      code:2
                                  userInfo:@{NSLocalizedDescriptionKey:
                                      @"LlamaInferenceBridge cannot generate until native inference is wired."}];
    }
    return @"";
}

- (void)unloadModel {
    // Placeholder: no model object exists yet.
}

@end
