package ru.gmp.ml;

import org.json.*;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public class MindustryManager {

    public static JSONArray getReleases() throws IOException {
        String url = "https://api.github.com/repos/Anuken/Mindustry/releases";
        return new JSONArray(executeHttpGet(url));
    }

    public static JSONObject getReleaseByTag(JSONArray releases, String tag) {
        for (int i = 0; i < releases.length(); i++) {
            JSONObject release = releases.getJSONObject(i);
            if (tag.equals(release.getString("tag_name"))) {
                return release;
            }
        }
        throw new RuntimeException("Release not found for tag: " + tag);
    }

    public static void downloadAsset(Main main, JSONObject release, String tag) throws IOException {
        JSONArray assets = release.getJSONArray("assets");
        if (assets.isEmpty()) {
            throw new RuntimeException("No assets found for release");
        }

        JSONObject firstAsset = assets.getJSONObject(0);
        String downloadUrl = firstAsset.getString("browser_download_url");
        String fileName = firstAsset.getString("name");

        Path dirPath = Paths.get("./game/" + tag);
        Path filePath = dirPath.resolve(fileName);

        Files.createDirectories(dirPath);

        if (Files.exists(filePath)) return;
        downloadWithNio(main, downloadUrl, filePath);
    }
    public static Set<String> extractTagsFromReleases(JSONArray releases) {
        Set<String> tags = new HashSet<>();
        for (int i = 0; i < releases.length(); i++) {
            JSONObject release = releases.getJSONObject(i);
            String tagName = release.getString("tag_name");
            tags.add(tagName);
        }
        return tags;
    }

    private static void downloadWithNio(Main main, String fileUrl, Path outputPath) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Java-Code");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(0);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error: " + responseCode);
        }
        ProgressBar pb = new ProgressBar("Downloading...", main.getWindow());

        long fileSize = connection.getContentLengthLong();
        try (ReadableByteChannel inChannel = Channels.newChannel(connection.getInputStream());
             FileChannel outChannel = FileChannel.open(
                     outputPath,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.WRITE,
                     StandardOpenOption.TRUNCATE_EXISTING)) {
            pb.show();

            long totalRead = 0;
            final long totalBytes = fileSize > 0 ? fileSize : 1;

            ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 8);
            int old_percentage = 0;

            while (true) {
                int bytesRead = inChannel.read(buffer);
                if (bytesRead == -1) break;

                buffer.flip();
                while (buffer.hasRemaining()) {
                    totalRead += outChannel.write(buffer);
                }
                buffer.clear();

                int percentage = Math.toIntExact(totalRead * 100 / fileSize);

                if (old_percentage < percentage) main.getLogger().info("Downloaded {}%", percentage);
                pb.set(percentage);
                old_percentage = percentage;
            }
            outChannel.close();
            inChannel.close();
            pb.hide();
        } finally {
            connection.disconnect();
        }
    }

    private static String executeHttpGet(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }
}