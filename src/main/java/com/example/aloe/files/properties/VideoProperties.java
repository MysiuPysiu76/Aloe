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

/**
 * {@code VideoProperties} is a record that extracts and provides detailed metadata about a video file.
 * <p>
 * This class uses the {@code FFprobe} library (from FFmpeg) to analyze the video and retrieve properties
 * such as resolution, codec, bitrate, duration, frame rate (FPS), and embedded metadata like GPS coordinates or camera model.
 * </p>
 *
 * <p>It implements the {@link Properties} interface to provide a standardized method for retrieving
 * media-related properties and values in the form of labels and values or a property map.</p>
 *
 * @param file the video file whose properties are to be read
 * @see Properties
 * @since 1.9.6
 */
public record VideoProperties(@NotNull File file) implements Properties {

    /**
     * A cached reference to the FFprobeResult to avoid repeated probing of the video file.
     */
    private static final AtomicReference<FFprobeResult> cachedMetadata = new AtomicReference<>();

    /**
     * Retrieves and caches FFprobe metadata for the current video file.
     *
     * @return {@link FFprobeResult} containing metadata about the video
     */
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

    /**
     * Retrieves the first video stream from the file.
     *
     * @return optional containing video {@link Stream}, or empty if not present
     */
    private Optional<Stream> getVideoStream() {
        return getMetadata().getStreams().stream()
                .filter(s -> "video".equals(s.getCodecType().name().toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    /**
     * Retrieves the first audio stream from the file.
     *
     * @return optional containing audio {@link Stream}, or empty if not present
     */
    private Optional<Stream> getAudioStream() {
        return getMetadata().getStreams().stream()
                .filter(s -> "audio".equals(s.getCodecType().name().toLowerCase(Locale.ROOT)))
                .findFirst();
    }

    /**
     * Retrieves a tag value from the format section of the FFprobe metadata.
     *
     * @param key the tag key to search for
     * @return optional string containing the tag value
     */
    private Optional<String> getFormatTag(String key) {
        Format format = getMetadata().getFormat();
        return Optional.ofNullable(format.getTag(key));
    }

    /**
     * Retrieves a tag value from a stream.
     *
     * @param stream the stream object
     * @param key    the tag key
     * @return optional string containing the tag value
     */
    private Optional<String> getStreamTag(Stream stream, String key) {
        return Optional.ofNullable(stream.getTag(key));
    }

    /**
     * @return video width in pixels as string, or null if unavailable
     */
    public String getWidth() {
        return getVideoStream()
                .map(Stream::getWidth)
                .map(String::valueOf)
                .orElse(null);
    }

    /**
     * @return video height in pixels as string, or null if unavailable
     */
    public String getHeight() {
        return getVideoStream()
                .map(Stream::getHeight)
                .map(String::valueOf)
                .orElse(null);
    }

    /**
     * @return duration of the video in seconds with two decimal places, or null if unavailable
     */
    public String getDuration() {
        return Optional.ofNullable(getMetadata().getFormat().getDuration())
                .map(d -> String.format(Locale.US, "%.2f s", d))
                .orElse(null);
    }

    /**
     * @return frames per second (FPS) of the video, or null if not available or cannot be calculated
     */
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

    /**
     * @return bitrate in kbps, or null if unavailable
     */
    public String getBitrate() {
        return Optional.ofNullable(getMetadata().getFormat().getBitRate())
                .map(b -> String.format(Locale.US, "%.0f kbps", b / 1000.0))
                .orElse(null);
    }

    /**
     * @return full codec name (e.g., H.264 / AVC), or null if not available
     */
    public String getCodec() {
        return getVideoStream()
                .map(Stream::getCodecLongName)
                .orElse(null);
    }

    /**
     * @return audio channel layout (e.g., stereo, mono), or null if not available
     */
    public String getChannel() {
        return getAudioStream()
                .map(Stream::getChannelLayout)
                .orElse(null);
    }

    /**
     * @return camera model used to record the video, if embedded in metadata
     */
    public String getCamera() {
        return getVideoStream()
                .flatMap(s -> getStreamTag(s, "com.apple.quicktime.model"))
                .or(() -> getFormatTag("com.apple.quicktime.model"))
                .orElse(null);
    }

    /**
     * @return creation date of the video (YYYY-MM-DD), or null if not available
     */
    public String getDate() {
        return getFormatTag("creation_time")
                .map(t -> t.split("T")[0])
                .orElse(null);
    }

    /**
     * @return creation time of the video (HH:MM:SS), or null if not available
     */
    public String getTime() {
        return getFormatTag("creation_time")
                .map(t -> t.split("T")[1].replace("Z", ""))
                .orElse(null);
    }

    /**
     * @return GPS latitude embedded in video metadata, or null if not present
     */
    public String getGPSWidth() {
        return getFormatTag("location")
                .map(loc -> loc.split("\\+")[1])
                .orElse(null);
    }

    /**
     * @return GPS longitude embedded in video metadata, or null if not present
     */
    public String getGPSHeight() {
        return getFormatTag("location")
                .map(loc -> loc.split("\\+")[2].replace("/", ""))
                .orElse(null);
    }

    /**
     * @return file extension in uppercase, or detected format name if extension is not available
     */
    public String getType() {
        String ext = FilenameUtils.getExtension(file.getName()).toUpperCase(Locale.US);
        if (!ext.isEmpty()) {
            return ext;
        }
        return Optional.ofNullable(getMetadata().getFormat().getFormatName())
                .map(f -> f.split(",")[0].toUpperCase(Locale.US))
                .orElse(null);
    }

    /**
     * Returns a list of localized property names to be displayed in the UI.
     *
     * @return list of property names
     */
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

    /**
     * Returns a list of property values corresponding to the names returned by {@link #getPropertiesNames()}.
     *
     * @return list of property values
     */
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

    /**
     * Returns a map of property names and corresponding values.
     * Useful for displaying or exporting file metadata.
     *
     * @return map of file properties
     */
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
