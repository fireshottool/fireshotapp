package me.fox.services;

import com.google.common.util.concurrent.ListenableFuture;
import me.fox.Fireshot;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.config.RequestConfig;
import me.fox.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author (Ausgefuchster)
 * @version (~ 07.12.2020)
 */

public class RequestService implements ConfigManager {

    private String uploadURL, imageURL, imageDetectionURL;
    private final HttpClient client = HttpClientBuilder.create().build();

    public ListenableFuture<File> uploadImage(File file, boolean imageDetection, boolean googleSearch) {
        return Fireshot.getInstance().getExecutorService().submit(() -> {
            HttpEntity entity;
            HttpPost request;

            if (imageDetection) {
                request = new HttpPost(imageDetectionURL);
                entity = MultipartEntityBuilder.create()
                        .addPart("file", new FileBody(file, ContentType.IMAGE_PNG))
                        .addTextBody("language", "eng")
                        .build();
            } else {
                entity = MultipartEntityBuilder.create()
                        .addPart("file", new FileBody(file, ContentType.IMAGE_PNG))
                        .build();
                request = new HttpPost(uploadURL);
            }
            request.setEntity(entity);

            HttpResponse response = client.execute(request);
            entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() != 200) {
                //TODO Implement error message
                System.out.println("Response: " + response);
                return file;
            }

            String content = EntityUtils.toString(entity);

            if (googleSearch) {
                Util.googleSearch(content);
                return file;
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);

            return file;
        });
    }

    public ListenableFuture<Image> requestImage(String imageName) {
        return Fireshot.getInstance().getExecutorService().submit(() -> {
            try {
                URL url = new URL(this.imageURL + imageName);
                return ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void applyConfig(Config config) {
        RequestConfig requestConfig = config.getRequestConfig();
        this.uploadURL = requestConfig.getUploadURL();
        this.imageURL = requestConfig.getImageURL();
        this.imageDetectionURL = requestConfig.getImageDetectionURL();
    }
}
