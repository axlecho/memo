all:Android.mk ndkDrawer.c
	ndk-build
clean:
	ndk-build clean
	rm -rf ../gen/*
	rm -rf ../bin/*
	cp ../AndroidManifest.xml ../bin/
