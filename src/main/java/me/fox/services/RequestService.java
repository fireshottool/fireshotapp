package me.fox.services;

import com.google.common.util.concurrent.ListenableFuture;
import me.fox.Fireshotapp;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.config.RequestConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author (Ausgefuchster)
 * @version (~ 07.12.2020)
 */

public class RequestService implements ConfigManager {

    private final HttpClient client = HttpClientBuilder.create().build();
    private String uploadURL, imageURL, imageDetectionURL;

    /**
     * Uploads an image to the server.
     *
     * @param file           image to upload
     * @param imageDetection whether it should use OCR or not
     * @param googleSearch   whether it should be searched on google or not
     * @return {@link ListenableFuture} with the {@link File} to delete it
     */
    public ListenableFuture<File> uploadImage(File file, boolean imageDetection, boolean googleSearch) {
        return Fireshotapp.getInstance().getExecutorService().submit(() -> {
            HttpPost request = this.buildRequest(file, imageDetection);
            HttpResponse response = this.client.execute(request);

            if (this.isStatusOk(response)) {
                this.searchOrCopyToClipboard(this.getEntityAsString(response), googleSearch);
            } else {
                this.logErrorMessage(response);
            }
            return file;
        });
    }

    /**
     * Requests an image from the server.
     *
     * @param imageName to request it
     * @return {@link ListenableFuture} with the {@link Image}
     */
    public ListenableFuture<Image> requestImage(String imageName) {
        return Fireshotapp.getInstance().getExecutorService().submit(() -> {
            try {
                URL url = new URL(this.imageURL + imageName);
                return ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * Gets the current {@link me.fox.components.Version} from the {@link Fireshotapp}.
     *
     * @return {@link ListenableFuture} with the {@link me.fox.components.Version} as {@link String}
     */
    public ListenableFuture<String> getVersion() {
        return Fireshotapp.getInstance().getExecutorService().submit(() -> {
            HttpGet request = new HttpGet("https://fireshotapp.eu/tool/version");
            try {
                HttpResponse response = this.client.execute(request);
                if (response.getStatusLine().getStatusCode() != 200) {
                    return "";
                }

                return EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        });
    }

    private String getEntityAsString(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    private void searchOrCopyToClipboard(String data, boolean search) {
        if (search) {
            this.googleSearch(data);
        } else {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
        }
    }

    private boolean isStatusOk(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private void logErrorMessage(HttpResponse response) {
        //TODO Implement error message
        System.out.println("Response: " + response);
    }

    private HttpPost buildRequest(File file, boolean imageDetection) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addPart("file", new FileBody(file, ContentType.IMAGE_PNG));

        HttpPost request;

        if (imageDetection) {
            request = new HttpPost(imageDetectionURL);
            builder.addTextBody("language", "eng");
        } else {
            request = new HttpPost(uploadURL);
        }

        request.setEntity(builder.build());
        return request;
    }

    private void googleSearch(String imageUrl) {
        System.out.println("google search");
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com/searchbyimage?image_url=" + imageUrl));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void applyConfig(Config config) {
        RequestConfig requestConfig = config.getRequestConfig();
        this.uploadURL = requestConfig.getUploadURL();
        this.imageURL = requestConfig.getImageURL();
        this.imageDetectionURL = requestConfig.getImageDetectionURL();
    }
}
