#include <jni.h>

#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <cmath>

/* Define codec specific settings */
#define MAX_BYTES_ENC_PER_FRAME     250 // Equals peak bitrate of 100 kbps 
#define MAX_BYTES_DEC_PER_FRAME     1024

#define MAX_INPUT_FRAMES        5
#define MAX_LBRR_DELAY          2
#define MAX_FRAME_LENGTH        480

#define    MAX_FRAME            320

#include <android/log.h>

#define LOG_TAG "silk_16" // text for log tag

#include "SKP_Silk_SDK_API.h"
#include "SKP_Silk_SigProc_FIX.h"

#define DEBUG_SILK16

// the header length of the RTP frame (must skip when en/decoding)
#define    RTP_HDR_SIZE    12

static int codec_open = 0;

static JavaVM *gJavaVM;
const char *kInterfacePath = "org/sipdroid/pjlib/silk16";

/* encoder parameters */

SKP_int32 encSizeBytes;
void *psEnc;

/* default settings */
SKP_int fs_kHz = 16;
SKP_int targetRate_bps = 20000;
SKP_int packetSize_ms = 20;
SKP_int frameSizeReadFromFile_ms = 20;
SKP_int packetLoss_perc = 0, smplsSinceLastPacket;
SKP_int INBandFec_enabled = 0, DTX_enabled = 0, quiet = 0;
SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder


/* decoder parameters */

jbyte payloadToDec[MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES * (MAX_LBRR_DELAY + 1)];
jshort out[(MAX_FRAME_LENGTH << 1) * MAX_INPUT_FRAMES], *outPtr;
SKP_int32 decSizeBytes;
void *psDec;
SKP_SILK_SDK_DecControlStruct DecControl;

extern "C"
JNIEXPORT jint JNICALL
Java_cc_imorning_silk_Silk16_open(JNIEnv *env, jobject obj, jint compression) {
    int ret;

    if (codec_open++ != 0)
        return (jint) 0;

    /* Set the samplingrate that is requested for the output */
    DecControl.API_sampleRate = 16000;

    /* Create decoder */
    ret = SKP_Silk_SDK_Get_Decoder_Size(&decSizeBytes);
    if (ret) {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "\n!!!!!!!! SKP_Silk_SDK_Get_Decoder_Size returned %d", ret);
    }
#ifdef DEBUG_SILK16
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "### INIT Decoder decSizeBytes = %d\n", decSizeBytes);
#endif
    psDec = malloc(decSizeBytes);
    /* Reset decoder */
    ret = SKP_Silk_SDK_InitDecoder(psDec);
    if (ret) {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "\n!!!!!!!! SKP_Silk_InitDecoder returned %d", ret);
    }
    /* Create Encoder */
    ret = SKP_Silk_SDK_Get_Encoder_Size(&encSizeBytes);
    if (ret) {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "\n!!!!!!!! SKP_Silk_SDK_Get_Encoder_Size returned %d", ret);
    }
#ifdef DEBUG_SILK16
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "### INIT Encoder encSizeBytes = %d\n", encSizeBytes);
#endif
    psEnc = malloc(encSizeBytes);

    /* Reset Encoder */
    ret = SKP_Silk_SDK_InitEncoder(psEnc, &encControl);
    if (ret) {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "\n!!!!!!!! SKP_Silk_SDK_InitEncoder returned %d", ret);
    }

    /* Set Encoder parameters */
    encControl.API_sampleRate = fs_kHz * 1000;
    encControl.packetSize = packetSize_ms * fs_kHz;
    encControl.packetLossPercentage = packetLoss_perc;
    encControl.useInBandFEC = INBandFec_enabled;
    encControl.useDTX = DTX_enabled;
    encControl.complexity = compression;
    encControl.bitRate = targetRate_bps;

    return (jint) 0;
}

void Print_Decode_Error_Msg(int errcode) {
    switch (errcode) {
        case SKP_SILK_DEC_INVALID_SAMPLING_FREQUENCY:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nOutput sampling frequency lower than internal decoded sampling frequency\n",
                                errcode);
            break;
        case SKP_SILK_DEC_PAYLOAD_TOO_LARGE:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nPayload size exceeded the maximum allowed 1024 bytes\n",
                                errcode);
            break;
        case SKP_SILK_DEC_PAYLOAD_ERROR:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nPayload has bit errors\n",
                                errcode);
            break;
        default:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "unknown error: %d", errcode);
            break;
    }
}

void Print_Encode_Error_Msg(int errcode) {
    switch (errcode) {
        case SKP_SILK_ENC_INPUT_INVALID_NO_OF_SAMPLES:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nInput length is not a multiplum of 10 ms, or length is longer than the packet length\n",
                                errcode);
            break;
        case SKP_SILK_ENC_FS_NOT_SUPPORTED:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nSampling frequency not 8000, 12000, 16000 or 24000 Hertz \n",
                                errcode);
            break;
        case SKP_SILK_ENC_PACKET_SIZE_NOT_SUPPORTED:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nPacket size not 20, 40, 60, 80 or 100 ms\n",
                                errcode);
            break;
        case SKP_SILK_ENC_PAYLOAD_BUF_TOO_SHORT:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nAllocated payload buffer too short \n",
                                errcode);
            break;
        case SKP_SILK_ENC_INTERNAL_ERROR:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!!!!! Decode_Error_Message: %d\nInternal encoder error\n",
                                errcode);
            break;
        default:
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "unknown error: %d", errcode);
    }
}

extern "C"
JNIEXPORT jint JNICALL Java_cc_imorning_silk_Silk16_encode
        (JNIEnv *env, jobject obj, jshortArray lin, jint offset, jbyteArray encoded, jint size) {

    jbyte enc_payload[MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES];
    jshort in[MAX_FRAME_LENGTH * MAX_INPUT_FRAMES];
    int ret, i, frsz = MAX_FRAME;
    SKP_int16 nBytes;
    unsigned int lin_pos = 0;

    if (!codec_open)
        return 0;

#ifdef DEBUG_SILK16
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "encoding frame size: %d\toffset: %d\n", size, offset);
#endif


    for (i = 0; i < size; i += MAX_FRAME) {
#ifdef DEBUG_SILK16
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "encoding frame size: %d\toffset: %d i: %d\n", size, offset, i);
#endif

        env->GetShortArrayRegion(lin, offset + i, frsz, in);
        /* max payload size */
        nBytes = MAX_BYTES_ENC_PER_FRAME * MAX_INPUT_FRAMES;

        ret = SKP_Silk_SDK_Encode(psEnc, &encControl, in, (SKP_int16) frsz,
                                  (SKP_uint8 *) enc_payload, &nBytes);
        if (ret) {
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                                "!!!!!!!! SKP_Silk_Encode returned: %d\n", ret);
            Print_Encode_Error_Msg(ret);
            break;
        }
#ifdef DEBUG_SILK16
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "Enocded nBytes: %d\n", nBytes);
#endif
        /* Write payload */
        env->SetByteArrayRegion(encoded, RTP_HDR_SIZE + lin_pos, nBytes, enc_payload);
        lin_pos += nBytes;
    }
#ifdef DEBUG_SILK16
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "encoding **END** frame size: %d\toffset: %d i: %d lin_pos: %d\n", size,
                        offset, i, lin_pos);
#endif

    return (jint) lin_pos;
}

extern "C"
JNIEXPORT jint JNICALL Java_cc_imorning_silk_Silk16_decode
        (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {

    jbyte buffer[MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES * (MAX_LBRR_DELAY + 1)];
    jshort output_buffer[(MAX_FRAME_LENGTH << 1) * MAX_INPUT_FRAMES];
//	SKP_int16	*outPtr;

    int ret;
    SKP_int16 len;
//	int	tot_len,frames;

    if (!codec_open)
        return 0;

#ifdef DEBUG_SILK16
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "##### BEGIN DECODE ********  decoding frame size: %d\n", size);
#endif

    env->GetByteArrayRegion(encoded, RTP_HDR_SIZE, size, buffer);

//	outPtr = output_buffer;
//    tot_len = 0;
//	frames = 0;

//	do {
    ret = SKP_Silk_SDK_Decode(psDec, &DecControl, 0, (SKP_uint8 *) buffer, size, output_buffer,
                              &len);
    if (ret) {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                            "!!!!!!!! SKP_Silk_SDK_Decode returned: %d\n", ret);
        Print_Decode_Error_Msg(ret);
    }
#ifdef DEBUG_SILK16
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,
                        "##### DECODED length: %d\n\t Frame #: %d", len);
#endif
    env->SetShortArrayRegion(lin, 0, len, output_buffer);
    return (jint) len;
}

extern "C"
JNIEXPORT void JNICALL Java_cc_imorning_silk_Silk16_close(JNIEnv *env, jobject obj) {
    if (--codec_open != 0)
        return;
/* Free decoder */
    free(psDec);
/* Free Encoder */
    free(psEnc);
}