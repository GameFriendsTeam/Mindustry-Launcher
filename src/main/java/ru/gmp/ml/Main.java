package ru.gmp.ml;

import org.apache.logging.log4j.*;
import org.json.*;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private MLWindow win;
    private JSONArray releases;
    private Thread th;

    private final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) { new Main(args); }

    public Main(String[] args) {
        preparingMindustryMan();
        preparingWindow();
    }


    public Logger getLogger() {
        return logger;
    }
    public Frame getWindow() { return win; }

    public void preparingMindustryMan() {
        try {
            releases = MindustryManager.getReleases();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void preparingWindow() {
        this.logger.info("Preparing window...");
        MLWindow myWin = new MLWindow();

        //JSONObject jsonObj =
        myWin.addChoice(VersionSorter.sort(MindustryManager.extractTagsFromReleases(releases)));
        myWin.addButton(this, Main::onSelected);

        this.logger.info("Show window...");
        myWin.start();

        this.win = myWin;
    }
    public static void onSelected(Main main, String selected) {
        Logger logger = LogManager.getLogger(Main.class);

        logger.info("Selected: {}", selected);
        JSONArray releases = null;

        try {
            logger.info("Getting releases list");
            releases = MindustryManager.getReleases();

            logger.info("Getting {} release", selected);
            JSONObject release = MindustryManager.getReleaseByTag(releases, selected);

            logger.info("Downloading {}...", selected);
            MindustryManager.downloadAsset(main, release, selected);
            logger.info("Downloaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicInteger code = new AtomicInteger(-1414);
        code.set(0);
        main.th = new Thread(() -> {
            String jarPath = "./game/"+selected+"/Mindustry.jar";
            try {
                ProcessBuilder pb = new ProcessBuilder("javaw", "-jar", jarPath);
                Process process = pb.start();

                code.set(process.waitFor());

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });main.th.start();
        if (code.get() != -1414) main.exit(code.get());
    }

    public void exit(int status) {
        if (th != null) {
            this.logger.info("Waiting the game...");
            try { th.join(); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        this.logger.info("Hide window...");
        try {
            win.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.logger.info("Shutdown...");
        System.exit(status);
    }
}