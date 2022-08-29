package cc.imorning.chat.utils;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

import cc.imorning.common.utils.FileUtils;
import cc.imorning.common.utils.Log;
import cc.imorning.common.utils.MD5Utils;

public class AudioMessageUtils {
    private static final String TAG = "AudioMessageUtils";

    private static final int RECORD_AUDIO_BUFFER_TIMES = 1;
    private static final int PLAY_AUDIO_BUFFER_TIMES = 1;
    private static final int AUDIO_FREQUENCY = 44100;

    private static final int RECORD_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private static final int PLAY_CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // pcm cache dir
    private static final String pcmCacheDir = FileUtils.Companion.getInstance().getAudioPCMCacheDir();

    private static AudioMessageUtils instance = new AudioMessageUtils();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private AudioRecordThread aRecordThread;
    private volatile AudioUtilsState state = AudioUtilsState.IDLE;
    private File tmpPCMFile = null;
    private File tmpWavFile = null;
    private OnState onStateListener;

    private AudioMessageUtils() {
    }

    public static AudioMessageUtils getInstance() {
        if (null == instance) {
            instance = new AudioMessageUtils();
        }
        return instance;
    }

    private static String getHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString(b)).append(",");
        }
        return sb.toString();
    }

    public void setOnStateListener(OnState onStateListener) {
        this.onStateListener = onStateListener;
    }

    /**
     * start to record audio
     *
     * @param needCreateWAVFile does need to create a wav file
     */
    public synchronized void startRecord(boolean needCreateWAVFile) {
        if (!state.equals(AudioUtilsState.IDLE)) {
            Log.w(TAG, "can't start to record, current state is: " + state);
            return;
        }
        try {
            tmpPCMFile = File.createTempFile("recording", ".pcm", new File(pcmCacheDir));
            if (needCreateWAVFile) {
                tmpWavFile = new File(
                        FileUtils.Companion.getInstance().getAudioMessagePath(Objects.requireNonNull(MD5Utils.INSTANCE.digest(String.valueOf(System.currentTimeMillis())))) + ".wav");
            }
        } catch (IOException e) {
            Log.e(TAG, "start record failed cause :" + e.getLocalizedMessage());
        }
        if (null != aRecordThread) {
            aRecordThread.interrupt();
            aRecordThread = null;
        }
        aRecordThread = new AudioRecordThread(needCreateWAVFile);
        aRecordThread.start();
    }

    public synchronized void stopRecord() {
        if (!state.equals(AudioUtilsState.RECORDING)) {
            return;
        }
        state = AudioUtilsState.STOP_RECORD;
        notifyState(state);
    }

    /**
     * play pcm file
     *
     * @param result PCM file path
     */
    public synchronized void startPlayPCM(String result) {
        if (!isIdle()) {
            return;
        }
        new AudioTrackPlayThread(new File(result)).start();
    }

    public synchronized void stopPlay() {
        if (!state.equals(AudioUtilsState.PLAYING)) {
            return;
        }
        state = AudioUtilsState.STOP_PLAY;
    }

    public synchronized boolean isIdle() {
        return AudioUtilsState.IDLE.equals(state);
    }

    private synchronized void notifyState(final AudioUtilsState currentState) {
        if (null != onStateListener) {
            mainHandler.post(() -> onStateListener.onStateChanged(currentState));
        }
    }

    /**
     * @param out           wav音频文件流
     * @param totalAudioLen 不包括header的音频数据总长度
     * @param channels      audioRecord的频道数量
     * @throws IOException 写文件错误
     */
    private void writeWavFileHeader(FileOutputStream out, long totalAudioLen, int channels) throws IOException {
        byte[] header = generateWavFileHeader(totalAudioLen, channels);
        out.write(header, 0, header.length);
    }

    /**
     * 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，
     * wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的
     *
     * @param pcmAudioByteCount 不包括header的音频数据总长度
     * @param channels          audioRecord的频道数量
     */
    @NonNull
    @Contract(pure = true)
    private byte[] generateWavFileHeader(long pcmAudioByteCount, int channels) {
        long totalDataLen = pcmAudioByteCount + 36; // 不包含前8个字节的WAV文件总长度
        long byteRate = (long) AudioMessageUtils.AUDIO_FREQUENCY * 2 * channels;
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);

        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) ((long) AudioMessageUtils.AUDIO_FREQUENCY & 0xff);
        header[25] = (byte) (((long) AudioMessageUtils.AUDIO_FREQUENCY >> 8) & 0xff);
        header[26] = (byte) (((long) AudioMessageUtils.AUDIO_FREQUENCY >> 16) & 0xff);
        header[27] = (byte) (((long) AudioMessageUtils.AUDIO_FREQUENCY >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (2 * channels);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (pcmAudioByteCount & 0xff);
        header[41] = (byte) ((pcmAudioByteCount >> 8) & 0xff);
        header[42] = (byte) ((pcmAudioByteCount >> 16) & 0xff);
        header[43] = (byte) ((pcmAudioByteCount >> 24) & 0xff);
        return header;
    }

    public enum AudioUtilsState {
        ERROR,
        IDLE,
        RECORDING,
        STOP_RECORD,
        PLAYING,
        STOP_PLAY
    }

    public interface OnState {
        void onStateChanged(AudioUtilsState currentState);
    }

    private class AudioRecordThread extends Thread {
        AudioRecord audioRecord;
        int bufferSize;
        boolean createWav;

        @SuppressLint("MissingPermission")
        AudioRecordThread(boolean needCreateWav) {
            this.createWav = needCreateWav;
            bufferSize = AudioRecord.getMinBufferSize(AUDIO_FREQUENCY, RECORD_CHANNEL_CONFIG, AUDIO_ENCODING)
                    * RECORD_AUDIO_BUFFER_TIMES;
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_FREQUENCY,
                    RECORD_CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize);
        }

        @Override
        public void run() {
            state = AudioUtilsState.RECORDING;
            notifyState(state);
            Log.d(TAG, "start record");
            try {
                FileOutputStream pcmFos = new FileOutputStream(tmpPCMFile);
                FileOutputStream wavFos = new FileOutputStream(tmpWavFile);
                if (createWav) {
                    writeWavFileHeader(wavFos, bufferSize, audioRecord.getChannelCount());
                }
                audioRecord.startRecording();
                byte[] byteBuffer = new byte[bufferSize];
                while (state.equals(AudioUtilsState.RECORDING) && !isInterrupted()) {
                    int end = audioRecord.read(byteBuffer, 0, byteBuffer.length);
                    pcmFos.write(byteBuffer, 0, end);
                    pcmFos.flush();
                    if (createWav) {
                        wavFos.write(byteBuffer, 0, end);
                        wavFos.flush();
                    }
                }
                audioRecord.stop();
                pcmFos.close();
                wavFos.close();
                if (createWav) {
                    // 修改header
                    RandomAccessFile wavRaf = new RandomAccessFile(tmpWavFile, "rw");
                    byte[] header = generateWavFileHeader(tmpPCMFile.length(), audioRecord.getChannelCount());
                    Log.d(TAG, "header: " + getHexString(header));
                    wavRaf.seek(0);
                    wavRaf.write(header);
                    wavRaf.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "AudioRecordThread:", e);
                notifyState(AudioUtilsState.ERROR);
            }
            notifyState(state);
            state = AudioUtilsState.IDLE;
            notifyState(state);
            Log.d(TAG, "record done");
        }

    }

    /**
     * play Audio with AudioTrack
     */
    private class AudioTrackPlayThread extends Thread {
        AudioTrack track;
        int bufferSize = 10240;
        File audioFile;

        AudioTrackPlayThread(File file) {
            setPriority(Thread.MAX_PRIORITY);
            audioFile = file;
            int bufferSize = AudioTrack.getMinBufferSize(AUDIO_FREQUENCY,
                    PLAY_CHANNEL_CONFIG, AUDIO_ENCODING) * PLAY_AUDIO_BUFFER_TIMES;
            track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    AUDIO_FREQUENCY,
                    PLAY_CHANNEL_CONFIG, AUDIO_ENCODING, bufferSize,
                    AudioTrack.MODE_STREAM);
        }

        @Override
        public void run() {
            super.run();
            state = AudioUtilsState.PLAYING;
            notifyState(state);
            try {
                FileInputStream fis = new FileInputStream(audioFile);
                track.play();
                byte[] aByteBuffer = new byte[bufferSize];
                while (state.equals(AudioUtilsState.PLAYING) &&
                        fis.read(aByteBuffer) >= 0) {
                    track.write(aByteBuffer, 0, aByteBuffer.length);
                }
                track.stop();
                track.release();
            } catch (Exception e) {
                Log.e(TAG, "AudioTrackPlayThread: ", e);
                notifyState(AudioUtilsState.ERROR);
            }
            state = AudioUtilsState.STOP_PLAY;
            notifyState(state);
            state = AudioUtilsState.IDLE;
            notifyState(state);
        }
    }
}