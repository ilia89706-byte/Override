package com.override.engine;

import static com.raylib.Raylib.InitAudioDevice;
import static com.raylib.Raylib.CloseAudioDevice;
import static com.raylib.Raylib.IsAudioDeviceReady;
import static com.raylib.Raylib.SetMasterVolume;
import static com.raylib.Raylib.PlaySound;
import static com.raylib.Raylib.PauseSound;
import static com.raylib.Raylib.ResumeSound;
import static com.raylib.Raylib.StopSound;
import static com.raylib.Raylib.IsSoundPlaying;
import static com.raylib.Raylib.SetSoundVolume;
import static com.raylib.Raylib.SetSoundPitch;
import static com.raylib.Raylib.UnloadSound;
import static com.raylib.Raylib.PlayMusicStream;
import static com.raylib.Raylib.UpdateMusicStream;
import static com.raylib.Raylib.StopMusicStream;
import static com.raylib.Raylib.PauseMusicStream;
import static com.raylib.Raylib.ResumeMusicStream;
import static com.raylib.Raylib.IsMusicStreamPlaying;
import static com.raylib.Raylib.SetMusicVolume;
import static com.raylib.Raylib.SetMusicPitch;
import static com.raylib.Raylib.SeekMusicStream;
import static com.raylib.Raylib.GetMusicTimeLength;
import static com.raylib.Raylib.GetMusicTimePlayed;
import static com.raylib.Raylib.UnloadMusicStream;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

class SoundObj {
    public boolean isLoaded = false;
    public boolean isStream = false;
    public float volume = 1.0f;
    public float pitch = 1.0f;

    public com.raylib.Raylib.Sound loadedSound;
    public com.raylib.Raylib.Music loadedMusic;

    public SoundObj() {
    }

    public void load(String filename, boolean isStream) {
        this.isStream = isStream;
        String fullPath = Global.projectPath + File.separator + filename;

        if (isStream) {
            loadedMusic = AssetManager.pickAsset(fullPath, "music");
        } else {
            loadedSound = AssetManager.pickAsset(fullPath, "sound");
        }
        this.isLoaded = true;
    }

    public void play() {
        if (!isLoaded)
            return;
        if (isStream) {
            PlayMusicStream(loadedMusic);
        } else {
            PlaySound(loadedSound);
        }
    }

    public void pause() {
        if (!isLoaded)
            return;
        if (isStream) {
            PauseMusicStream(loadedMusic);
        } else {
            PauseSound(loadedSound);
        }
    }

    public void resume() {
        if (!isLoaded)
            return;
        if (isStream) {
            ResumeMusicStream(loadedMusic);
        } else {
            ResumeSound(loadedSound);
        }
    }

    public void stop() {
        if (!isLoaded)
            return;
        if (isStream) {
            StopMusicStream(loadedMusic);
        } else {
            StopSound(loadedSound);
        }
    }

    public boolean isPlaying() {
        if (!isLoaded)
            return false;
        return isStream ? IsMusicStreamPlaying(loadedMusic) : IsSoundPlaying(loadedSound);
    }

    public void setVolume(float val) {
        if (!isLoaded)
            return;
        this.volume = val;
        if (isStream) {
            SetMusicVolume(loadedMusic, val);
        } else {
            SetSoundVolume(loadedSound, val);
        }
    }

    public void setPitch(float val) {
        if (!isLoaded)
            return;
        this.pitch = val;
        if (isStream) {
            SetMusicPitch(loadedMusic, val);
        } else {
            SetSoundPitch(loadedSound, val);
        }
    }

    public void seek(float positionInSeconds) {
        if (isLoaded && isStream) {
            SeekMusicStream(loadedMusic, positionInSeconds);
        }
    }

    public float getLength() {
        return (isLoaded && isStream) ? GetMusicTimeLength(loadedMusic) : 0.0f;
    }

    public float getTimePlayed() {
        return (isLoaded && isStream) ? GetMusicTimePlayed(loadedMusic) : 0.0f;
    }

    public void unload() {
        if (!isLoaded)
            return;
        if (isStream) {
            UnloadMusicStream(loadedMusic);
        } else {
            UnloadSound(loadedSound);
        }
        isLoaded = false;
    }
}

public class Sound {
    private static final List<SoundObj> sounds = new ArrayList<>();

    public static void setMasterVolume(float volume) {
        SetMasterVolume(volume);
    }

    public static void update() {
        for (SoundObj obj : sounds) {
            if (obj.isLoaded && obj.isStream && obj.isPlaying()) {
                UpdateMusicStream(obj.loadedMusic);
            }
        }
    }

    public static SoundObj create() {
        SoundObj sound = new SoundObj();
        sounds.add(sound);
        return sound;
    }

    public static void stopAll() {
        for (SoundObj obj : sounds) {
            obj.stop();
        }
    }

    public static void closeAll() {
        for (SoundObj obj : sounds) {
            obj.unload();
        }
        sounds.clear();
    }
}
