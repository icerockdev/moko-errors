#
# Copyright 2024 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
#

set -e

log() {
  echo "\033[0;32m> $1\033[0m"
}

./gradlew detekt
log "detekt success"

./gradlew assembleDebug
log "android success"

./gradlew compileKotlinIosX64
log "ios success"

./gradlew build publishToMavenLocal
log "full build success"

if ! command -v xcodebuild &> /dev/null
then
    log "xcodebuild could not be found, skip ios checks"
else
    ./gradlew syncMultiPlatformLibraryDebugFrameworkIosX64
    log "sync success"

    (
    cd sample/ios-app &&
    pod install &&
    set -o pipefail &&
    xcodebuild -scheme TestProj -workspace TestProj.xcworkspace -configuration Debug -sdk iphonesimulator -arch x86_64 build CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO | xcpretty
    )
    log "ios xcode success"
fi
