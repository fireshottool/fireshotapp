package me.fox.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import javafx.util.Pair;
import me.fox.Fireshotapp;
import me.fox.components.Version;
import me.fox.config.UpdateConfig;
import me.fox.ui.frames.OptionCheckboxFrame;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * @author (Ausgefuchster)
 * @version (~ 07.01.2021)
 */

public class UpdateService {

    private final RequestService requestService;
    private final JsonService jsonService;

    public UpdateService(RequestService requestService, JsonService jsonService) {
        this.requestService = requestService;
        this.jsonService = jsonService;
    }

    public void checkForUpdate(boolean message) {
        Futures.addCallback(
                this.requestService.getVersion(),
                this.updateCallback(message),
                Fireshotapp.getInstance().getExecutorService()
        );
    }

    private FutureCallback<String> updateCallback(boolean notifyIfNoUpdateAvailable) {
        return new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String version) {
                if (version == null || version.equals("")) {
                    System.out.println("Failed to request Version");
                    return;
                }

                if (isNewerVersion(new Version(version))) {
                    if (shouldUpdate()) {
                        update(version);
                    } else {
                        askForUpdate(version);
                    }
                } else if (notifyIfNoUpdateAvailable) {
                    showTrayInfo("No update available");
                } else if (isUpdated()) {
                    showUpdatedInfoAndUpdateConfig();
                }
            }

            @Override
            public void onFailure(@Nonnull Throwable throwable) {
            }
        };
    }

    private void showUpdatedInfoAndUpdateConfig() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.showTrayInfo("Successfully updated to version " + Fireshotapp.VERSION.get());
        this.jsonService.getConfig().getUpdateConfig().setUpdated(false);
        this.jsonService.saveAndApply();
    }

    private void askForUpdate(String version) {
        Pair<Integer, Boolean> pair = OptionCheckboxFrame.showDialog(
                null,
                String.format("Update for Fireshotapp available from %s to %s", Fireshotapp.VERSION.get(), version),
                "Fireshotapp - Update"
        );

        if (pair.getKey() == JOptionPane.YES_OPTION) {
            if (pair.getValue()) {
                this.updateConfig(true);
            }
            this.update(version);
        } else if (pair.getKey() == JOptionPane.NO_OPTION && pair.getValue()) {
            this.updateConfig(false);
        }
    }

    private void updateConfig(boolean shouldUpdate) {
        this.jsonService.getConfig().getUpdateConfig().setAskForUpdate(false);
        this.jsonService.getConfig().getUpdateConfig().setUpdate(shouldUpdate);
        this.jsonService.saveAndApply();
    }

    private void update(String newVersion) {
        String url = String.format(
                "https://github.com/fireshottool/fireshotapp/releases/download/v%s/fireshotapp-setup-%s.exe",
                newVersion, newVersion
        );

        String filename = String.format(
                "%s%s%s.exe /SILENT",
                System.getenv("LOCALAPPDATA"),
                Paths.get("Programs", "Fireshotapp", "fireshotapp-setup-").toString(),
                newVersion
        );

        try {
            this.showUpdateWindow(url, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.jsonService.getConfig().getUpdateConfig().setUpdated(true);
        this.jsonService.saveAndApply();

        try {
            Runtime.getRuntime().exec(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    private void showUpdateWindow(String downloadUrl, String filename) throws IOException {
        final JProgressBar progressBar = new JProgressBar();
        progressBar.setMaximum(100);

        JFrame frame = new JFrame("Preparing Fireshotapp update...");
        JLabel label = new JLabel("Progress: 0%");

        frame.setSize(300, 170);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.add(label);
        frame.add(progressBar);

        label.setLocation(5, 5);
        label.setSize(100, 60);

        progressBar.setLocation(5, 60);
        progressBar.setSize(250, 30);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.downloadUpdate(downloadUrl, filename, progress -> SwingUtilities.invokeLater(() -> {
            progressBar.setValue((int) progress.doubleValue());
            label.setText(String.format("Progress: %.2f%%", progress));
        }));
    }

    private void downloadUpdate(String downloadURL, String filename, Consumer<Double> progressConsumer) throws IOException {
        URL url = new URL(downloadURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        long fileSize = httpURLConnection.getContentLength();

        BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
        FileOutputStream fileOutputStream = new FileOutputStream(filename);

        BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream, 1024);

        byte[] data = new byte[1024];
        long downloadedFileSize = 0;
        int x;
        while ((x = inputStream.read(data, 0, 1024)) >= 0) {
            downloadedFileSize += x;
            final double currentProgress = (((double) downloadedFileSize) / ((double) fileSize)) * 100d;
            progressConsumer.accept(currentProgress);
            outputStream.write(data, 0, x);
        }
        outputStream.close();
        inputStream.close();
    }

    private boolean isNewerVersion(Version version) {
        return Fireshotapp.VERSION.compareTo(version) < 0;
    }

    private boolean isUpdated() {
        return this.jsonService.getConfig().getUpdateConfig().isUpdated();
    }

    private boolean shouldUpdate() {
        UpdateConfig config = this.jsonService.getConfig().getUpdateConfig();
        return config.isUpdate() && !config.isAskForUpdate();
    }

    private void showTrayInfo(String message) {
        Fireshotapp.getInstance().getSystemTray().info("Fireshotapp", message);
    }
}
