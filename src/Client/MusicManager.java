package Client;

import javax.sound.sampled.*;
import java.io.File;

/**
 * 효과 음악 담당 클래스
 */
public class MusicManager {
    private static MusicManager instance;

    public static MusicManager getInstance(){
        if(instance == null) instance = new MusicManager();
        return instance;
    }

    public void playSoundEffect(String filePath) {
        try {
            File soundEffectPath = new File(filePath);

            if (soundEffectPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundEffectPath);
                AudioFormat baseFormat = audioInput.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false);
                AudioInputStream decodedAudioInput = AudioSystem.getAudioInputStream(decodedFormat, audioInput);

                Clip clip = AudioSystem.getClip();
                clip.open(decodedAudioInput);
                clip.start();

                // 볼륨 조정
                FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = volume.getMaximum() - volume.getMinimum();
                float gain = (range * 0.75f) + volume.getMinimum(); // 볼륨 75%
                volume.setValue(gain);

            } else {
                System.out.println("Can't find music file");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

