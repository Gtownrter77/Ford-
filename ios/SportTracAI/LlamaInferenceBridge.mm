#import "LlamaInferenceBridge.h"

#include <string>

#include "LlamaInference.h"

static NSString * const LlamaBridgeErrorDomain = @"com.sporttrac.localai.llama";

@implementation LlamaInferenceBridge

- (instancetype)init {
    self = [super init];
    if (self) {
        _engine = new sporttrac::llama::LlamaInference();
    }
    return self;
}

- (void)dealloc {
    delete static_cast<sporttrac::llama::LlamaInference *>(_engine);
    _engine = nullptr;
}

- (BOOL)loadModelAtPath:(NSString *)modelPath error:(NSError * _Nullable * _Nullable)error {
    auto *engine = static_cast<sporttrac::llama::LlamaInference *>(_engine);
    if (engine == nullptr || !engine->loadModel(std::string(modelPath.UTF8String))) {
        if (error != nil) {
            *error = [NSError errorWithDomain:LlamaBridgeErrorDomain
                                          code:1
                                      userInfo:@{NSLocalizedDescriptionKey:
                                          @"Unable to load the selected GGUF model file."}];
        }
        return NO;
    }
    return YES;
}

- (NSString *)generateForPrompt:(NSString *)prompt
                       maxTokens:(NSInteger)maxTokens
                     temperature:(float)temperature
                           error:(NSError * _Nullable * _Nullable)error {
    auto *engine = static_cast<sporttrac::llama::LlamaInference *>(_engine);
    if (engine == nullptr) {
        if (error != nil) {
            *error = [NSError errorWithDomain:LlamaBridgeErrorDomain
                                          code:2
                                      userInfo:@{NSLocalizedDescriptionKey:
                                          @"The native llama engine is not initialized."}];
        }
        return @"";
    }
    const auto result = engine->generate(
        std::string(prompt.UTF8String),
        sporttrac::llama::GenerationConfig{
            static_cast<std::int32_t>(maxTokens),
            temperature,
            4096
        }
    );
    if (!result.error.empty()) {
        if (error != nil) {
            *error = [NSError errorWithDomain:LlamaBridgeErrorDomain
                                          code:2
                                      userInfo:@{NSLocalizedDescriptionKey:
                                          [NSString stringWithUTF8String:result.error.c_str()]}];
        }
        return @"";
    }
    return [NSString stringWithUTF8String:result.text.c_str()];
}

- (void)unloadModel {
    auto *engine = static_cast<sporttrac::llama::LlamaInference *>(_engine);
    if (engine != nullptr) {
        engine->unloadModel();
    }
}

@end
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
