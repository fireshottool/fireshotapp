package me.fox.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import javafx.util.Pair;
import me.fox.Fireshotapp;
import me.fox.components.Version;
import me.fox.ui.frames.OptionCheckboxFrame;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author (Ausgefuchster)
 * @version (~ 07.01.2021)
 */

public class UpdateService {

    private final RequestService requestService;
    private final JsonService jsonService;
    private final String fileSeparator = System.getProperty("file.separator");

    public UpdateService(RequestService requestService, JsonService jsonService) {
        this.requestService = requestService;
        this.jsonService = jsonService;
    }

    public void checkUpdate(boolean message) {
        Futures.addCallback(this.requestService.getVersion(), new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String string) {
                if (string == null || string.equals("")) {
                    System.out.println("Failed to request Version");
                } else {
                    if (compare(new Version(string))) {
                        if (jsonService.getConfig().getUpdateConfig().isUpdate() && !jsonService.getConfig().getUpdateConfig().isAskForUpdate()) {
                            update(string);
                            return;
                        }
                        Pair<Integer, Boolean> pair = OptionCheckboxFrame.showDialog(null, "Update for Fireshotapp available from "
                                + Fireshotapp.VERSION.get() + " to " + string, "Fireshotapp - Update");
                        if (pair.getKey() == JOptionPane.YES_OPTION) {
                            if (pair.getValue()) {
                                jsonService.getConfig().getUpdateConfig().setAskForUpdate(false);
                                jsonService.getConfig().getUpdateConfig().setUpdate(true);
                                jsonService.saveAndApply();
                            }
                            update(string);
                        } else if (pair.getKey() == JOptionPane.NO_OPTION && pair.getValue()) {
                            jsonService.getConfig().getUpdateConfig().setAskForUpdate(false);
                            jsonService.getConfig().getUpdateConfig().setUpdate(false);
                            jsonService.saveAndApply();
                        }
                    } else if (message) {
                        Fireshotapp.getInstance().getSystemTray()
                                .info("Fireshotapp", "No update available");
                    } else if (jsonService.getConfig().getUpdateConfig().isUpdated()) {
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Fireshotapp.getInstance().getSystemTray()
                                .info("Fireshotapp", "Successfully updated to version " + Fireshotapp.VERSION.get());
                        jsonService.getConfig().getUpdateConfig().setUpdated(false);
                        jsonService.saveAndApply();
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        }, Fireshotapp.getInstance().getExecutorService());
    }


    private void update(String newVersion) {
        StringBuilder urlBuilder = new StringBuilder("https://github.com/fireshottool/fireshotapp/releases/download/v")
                .append(newVersion).append("/fireshotapp-setup-").append(newVersion).append(".exe");
        StringBuilder builder = new StringBuilder(System.getenv("LOCALAPPDATA"));
        builder.append(fileSeparator).append("Programs").append(fileSeparator)
                .append(fileSeparator).append("Fireshotapp").append(fileSeparator)
                .append("fireshotapp-setup-").append(newVersion).append(".exe");

        try {

            final JProgressBar jProgressBar = new JProgressBar();
            jProgressBar.setMaximum(100);
            JFrame frame = new JFrame("Preparing Fireshotapp update...");
            JLabel label = new JLabel("Progress: 0%");
            frame.setSize(300, 170);
            frame.setLayout(null);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            frame.add(label);
            frame.add(jProgressBar);
            label.setLocation(5, 5);
            label.setSize(100, 60);
            jProgressBar.setLocation(5, 60);
            jProgressBar.setSize(250, 30);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            long fileSize = httpURLConnection.getContentLength();

            BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(builder.toString());

            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream, 1024);

            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x = 0;
            while ((x = inputStream.read(data, 0, 1024)) >= 0) {
                downloadedFileSize += x;

                // calculate progress
                final double currentProgress = ((((double) downloadedFileSize) / ((double) fileSize)) * 100d);

                // update progress bar
                SwingUtilities.invokeLater(() -> {
                    jProgressBar.setValue((int) currentProgress);
                    label.setText("Progress: " + String.format("%.2f", currentProgress) + "%");
                });

                outputStream.write(data, 0, x);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jsonService.getConfig().getUpdateConfig().setUpdated(true);
        this.jsonService.saveAndApply();
        builder.append(" /SILENT");
        try {
            Runtime.getRuntime().exec(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Updating...");
        System.exit(1);
    }

    private boolean compare(Version version) {
        return Fireshotapp.VERSION.compareTo(version) < 0;
    }
}
