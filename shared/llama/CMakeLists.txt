cmake_minimum_required(VERSION 3.22)

project(SportTracLlamaNative LANGUAGES C CXX)

include(FetchContent)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Pin the exact upstream revision inspected for this integration. Update deliberately,
# then revalidate LlamaInference.cpp against the new include/llama.h API.
set(SPORTTRAC_LLAMA_CPP_REVISION "fe8156f789011f6ea0baf6917ea09f88b89d9554")

# Keep the embedded library minimal. Backends are selected explicitly by platform
# toolchains rather than inheriting host-only defaults.
set(LLAMA_BUILD_COMMON OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
set(GGML_NATIVE OFF CACHE BOOL "" FORCE)
set(GGML_OPENMP OFF CACHE BOOL "" FORCE)

FetchContent_Declare(
    llama_cpp
    GIT_REPOSITORY https://github.com/ggml-org/llama.cpp.git
    GIT_TAG ${SPORTTRAC_LLAMA_CPP_REVISION}
    GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(llama_cpp)

set(SPORTTRAC_LLAMA_SOURCES
    ${CMAKE_CURRENT_SOURCE_DIR}/src/LlamaInference.cpp
)

# Android builds compile the JNI entry points into the same .so as the C++ engine.
if(ANDROID)
    list(APPEND SPORTTRAC_LLAMA_SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/../../app/src/main/cpp/AndroidLlamaBridge.cpp
    )
endif()

if(APPLE)
    enable_language(OBJCXX)
    list(APPEND SPORTTRAC_LLAMA_SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/../../ios/SportTracAI/LlamaInferenceBridge.mm
    )
endif()

# CMake produces libsporttrac_llama.so for Android. For Apple platform builds it
# produces libsporttrac_llama.a, which is later packaged as an XCFramework.
if(APPLE)
    add_library(sporttrac_llama STATIC ${SPORTTRAC_LLAMA_SOURCES})
else()
    add_library(sporttrac_llama SHARED ${SPORTTRAC_LLAMA_SOURCES})
endif()

target_include_directories(sporttrac_llama
    PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)

if(ANDROID)
    target_include_directories(sporttrac_llama
        PRIVATE
            ${CMAKE_CURRENT_SOURCE_DIR}/../../app/src/main/cpp
    )
    find_library(ANDROID_LOG_LIB log)
    target_link_libraries(sporttrac_llama PRIVATE ${ANDROID_LOG_LIB})
endif()

if(APPLE)
    find_library(FOUNDATION_FRAMEWORK Foundation)
    target_link_libraries(sporttrac_llama PRIVATE ${FOUNDATION_FRAMEWORK})
endif()

target_link_libraries(sporttrac_llama PRIVATE llama)

set_target_properties(sporttrac_llama PROPERTIES
    OUTPUT_NAME "sporttrac_llama"
    POSITION_INDEPENDENT_CODE ON
)
