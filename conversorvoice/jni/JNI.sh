#!/bin/bash

# Run this script only from the project root directory!

CLASSPATH=bin/classes:$ANDROID_SDK_HOME/platforms/android-8/android.jar

function GenerateJNI()
{
	CLASSNAME=$1
	PACKAGE=$2
	INPUTDIR=$3
	OUTPUTDIR=$4

	# Compile the Java source file
	javac -classpath $CLASSPATH -d bin/classes src/$INPUTDIR/$CLASSNAME.java

	# Make the JNI header file
	javah -verbose -jni -classpath $CLASSPATH -o jni/$OUTPUTDIR/$CLASSNAME.h $PACKAGE.$CLASSNAME

	# Only if using rather SDK/JDK version than Eclipse --
	# delete the compiled Java class file
	# rm bin/classes/$INPUTDIR/$CLASSNAME.class
}

GenerateJNI "Math" \
	"utn.frba.ps.conversorvoice.dsp" \
	"utn/frba/ps/conversorvoice/dsp" \
	"Math"

GenerateJNI "KissFFT" \
	"utn.frba.ps.conversorvoice.dsp" \
	"utn/frba/ps/conversorvoice/dsp" \
	"KissFFT"

GenerateJNI "NativeResampleProcessor" \
	"utn.frba.ps.conversorvoice.dsp.processors" \
	"utn/frba/ps/conversorvoice/dsp/processors" \
	"Processors"

GenerateJNI "NativeTimescaleProcessor" \
	"utn.frba.ps.conversorvoice.dsp.processors" \
	"utn/frba/ps/conversorvoice/dsp/processors" \
	"Processors"