// adapted from https://www.baeldung.com/java-play-sound
// Energy Bounce by "magnuswalker" at https://freesound.org/s/523088/ shared via Creative Commons

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SoundPlayer implements LineListener
{
    private boolean isPlaybackCompleted;
    private HashMap<String, Clip> clipDictionary;
    private HashMap<String, AudioInputStream> streamDictionary;

    public SoundPlayer()
    {
        clipDictionary = new HashMap<String, Clip>();
        streamDictionary = new HashMap<String, AudioInputStream>();
    }

    public void loadSound(String soundFilename)
    {
        try
        {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Sounds/"+soundFilename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);
            Clip audioClip = AudioSystem.getClip();
            audioClip.addLineListener(this);
            audioClip.open(audioStream);

            clipDictionary.put(soundFilename,audioClip);
            streamDictionary.put(soundFilename,audioStream);

        }
        catch (IOException ioExp)
        {
            System.out.println("Error loading sound file: "+soundFilename);
        } catch (UnsupportedAudioFileException uafExp)
        {
            throw new RuntimeException(uafExp);
        } catch (LineUnavailableException luExp)
        {
            throw new RuntimeException(luExp);
        }
    }

    public void playSound(String soundFilename)
    {
        if (!clipDictionary.containsKey(soundFilename))
            loadSound(soundFilename);
        clipDictionary.get(soundFilename).setMicrosecondPosition(0);
        clipDictionary.get(soundFilename).start();
    }

    public void closeSound(String soundFilename)
    {
        if (clipDictionary.containsKey(soundFilename))
        {
            clipDictionary.remove(soundFilename);
            streamDictionary.remove(soundFilename);
        }
    }

    public void closeAllSounds()
    {
        for (String s: clipDictionary.keySet())
            closeSound(s);
    }

    @Override
    public void update(LineEvent event) {
        if (LineEvent.Type.START == event.getType()) {
            ; //System.out.println("Playback started.");
        } else if (LineEvent.Type.STOP == event.getType()) {
            isPlaybackCompleted = true;
            //System.out.println("Playback completed.");
        }
    }

}
