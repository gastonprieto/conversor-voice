/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor */

#ifndef _Included_utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor
#define _Included_utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor
 * Method:    alloc
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor_alloc
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor_free
  (JNIEnv *, jobject, jlong);

/*
 * Class:     utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor
 * Method:    processFrame
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_utn_frba_ps_conversorvoice_dsp_processors_NativeTimescaleProcessor_processFrame
  (JNIEnv *, jobject, jlong, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif