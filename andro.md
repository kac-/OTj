*OTjAndro* 8coin OT Android client (transfer only)
=======

## build requirements:
* android [SDK](http://developer.android.com/sdk/index.html)
* maven

## build:

1. build and install my *jeromq* fork for *android* https://github.com/kactech/jeromq/tree/android7
2. set *ANDROID_HOME* environment variable, e.g.:`export ANDROID_HOME=/opt/google/android-sdk-linux`
3. `mvn -DskipTests=true package`
4. take `./andro/target/otj-andro.apk` and have fun!


