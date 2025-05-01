package com.example.aloe.files.properties;

import com.example.aloe.utils.Translator;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import com.github.kokorin.jaffree.ffprobe.Format;
import com.github.kokorin.jaffree.ffprobe.Stream;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public record VideoProperties(@NotNull File file) implements Properties {

    private static final AtomicReference<FFprobeResult> cachedMetadata = new AtomicReference<>();

    private FFprobeResult getMetadata() {
        if (cachedMetadata.get() == null) {
            FFprobe ffprobe;
            try {
                ffprobe = FFprobe.atPath();
                ffprobe.setInput(file.getAbsolutePath()).execute();
            } catch (Exception e) {
                ffprobe = FFprobe.atPath(Paths.get("ffmpeg"));
            }

            FFprobeResult result = ffprobe
                    .setShowStreams(true)
                    .setShowFormat(true)
                    .setInput(file.getAbsolutePath())
                    .execute();
            cachedMetadata.set(result);
        }
        return cachedMetadata.get();
    }


    private Optional<Stream> getVideoStream() {
        return getMetadata().getStreams().stream()
                .filter(s -> "video".equals(s.getCodecType().name().toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    private Optional<Stream> getAudioStream() {
        return getMetadata().getStreams().stream()
                .filter(s -> "audio".equals(s.getCodecType().name().toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    private Optional<String> getFormatTag(String key) {
        Format format = getMetadata().getFormat();
        return Optional.ofNullable(format.getTag(key));
    }

    private Optional<String> getStreamTag(Stream stream, String key) {
        return Optional.ofNullable(stream.getTag(key));
    }

    public String getWidth() {
        return getVideoStream()
                .map(Stream::getWidth)
                .map(String::valueOf)
                .orElse(null);
    }

    public String getHeight() {
        return getVideoStream()
                .map(Stream::getHeight)
                .map(String::valueOf)
                .orElse(null);
    }

    public String getDuration() {
        return Optional.ofNullable(getMetadata().getFormat().getDuration())
                .map(d -> String.format(Locale.US, "%.2f s", d))
                .orElse(null);
    }

    public String getFPS() {
        return getVideoStream()
                .map(Stream::getRFrameRate)
                .map(r -> {
                    try {
                        String[] parts = r.toString().split("/");
                        double num = Double.parseDouble(parts[0]);
                        double den = Double.parseDouble(parts[1]);
                        return String.format(Locale.US, "%.2f", num / den);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    public String getBitrate() {
        return Optional.ofNullable(getMetadata().getFormat().getBitRate())
                .map(b -> String.format(Locale.US, "%.0f kbps", b / 1000.0))
                .orElse(null);
    }

    public String getCodec() {
        return getVideoStream()
                .map(Stream::getCodecLongName)
                .orElse(null);
    }

    public String getChannel() {
        return getAudioStream()
                .map(Stream::getChannelLayout)
                .orElse(null);
    }

    public String getCamera() {
        return getVideoStream()
                .flatMap(s -> getStreamTag(s, "com.apple.quicktime.model"))
                .or(() -> getFormatTag("com.apple.quicktime.model"))
                .orElse(null);
    }

    public String getDate() {
        return getFormatTag("creation_time")
                .map(t -> t.split("T")[0])
                .orElse(null);
    }

    public String getTime() {
        return getFormatTag("creation_time")
                .map(t -> t.split("T")[1].replace("Z", ""))
                .orElse(null);
    }

    public String getGPSWidth() {
        return getFormatTag("location")
                .map(loc -> loc.split("\\+")[1])
                .orElse(null);
    }

    public String getGPSHeight() {
        return getFormatTag("location")
                .map(loc -> loc.split("\\+")[2].replace("/", ""))
                .orElse(null);
    }

    public String getType() {
        String ext = FilenameUtils.getExtension(file.getName()).toUpperCase(Locale.US);
        if (!ext.isEmpty()) {
            return ext;
        }
        return Optional.ofNullable(getMetadata().getFormat().getFormatName())
                .map(f -> f.split(",")[0].toUpperCase(Locale.US))
                .orElse(null);
    }

    @Override
    public List<String> getPropertiesNames() {
        List<String> names = new ArrayList<>();
        names.add(Translator.translate("window.properties.media.type"));
        names.add(Translator.translate("window.properties.media.width"));
        names.add(Translator.translate("window.properties.media.height"));
        names.add(Translator.translate("window.properties.video.duration"));
        names.add(Translator.translate("window.properties.video.fps"));
        names.add(Translator.translate("window.properties.video.bitrate"));
        names.add(Translator.translate("window.properties.video.codec"));
        names.add(Translator.translate("window.properties.video.channel"));
        names.add(Translator.translate("window.properties.media.camera"));
        names.add(Translator.translate("window.properties.media.gps-date"));
        names.add(Translator.translate("window.properties.media.gps-time"));
        names.add(Translator.translate("window.properties.media.gps-width"));
        names.add(Translator.translate("window.properties.media.gps-height"));
        return names;
    }

    @Override
    public List<String> getPropertiesValues() {
        List<String> values = new ArrayList<>();
        values.add(getType());
        values.add(getWidth());
        values.add(getHeight());
        values.add(getDuration());
        values.add(getFPS());
        values.add(getBitrate());
        values.add(getCodec());
        values.add(getChannel());
        values.add(getCamera());
        values.add(getDate());
        values.add(getTime());
        values.add(getGPSWidth());
        values.add(getGPSHeight());
        return values;
    }

    @Override
    public Map<String, String> getProperties() {
        List<String> names = getPropertiesNames();
        List<String> values = getPropertiesValues();
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < names.size(); i++) {
            if (values.get(i) != null) {
                map.put(names.get(i), values.get(i));
            }
        }
        return map;
    }
}