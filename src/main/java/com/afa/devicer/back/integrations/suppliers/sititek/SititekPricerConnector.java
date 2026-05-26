package com.afa.devicer.back.integrations.suppliers.sititek;

import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.devicer.back.integrations.BaseConnector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.SingularField"})
public class SititekPricerConnector implements BaseConnector {

    @Value("${integrations.suppliers.sititek.feed.url}")
    private String feedUrl;
    @Value("${integrations.suppliers.sititek.feed.login}")
    private String feedLogin;
    @Value("${integrations.suppliers.sititek.feed.password}")
    private String feedPassword;
    @Value("${integrations.suppliers.sititek.feed.file}")
    private String fileName;
    @Value("${integrations.suppliers.sititek.feed.dir.in}")
    private String saveDirectory;
    @Value("${integrations.suppliers.sititek.enabled}")
    private Boolean enabled;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(feedUrl)
                .requestInterceptor(new BasicAuthenticationInterceptor(feedLogin, feedPassword))
                .build();
    }

    public void download() {
        if (!enabled) {
            return;
        }
        try {
            downloadFile();
        } catch (IOException e) {
            throw new DevicerException(DevicerErrors.INTEGRATION_SUPPLIER_SITITEK_FEED_DOWNLOAD_ERRORS, e);
        }
    }

    public void downloadFile() throws IOException {
        final String fileUrl = feedUrl + "/" + fileName;
        final ResponseEntity<Resource> response = restClient.get()
                .uri(fileUrl)
                .header("Accept", "application/vnd.ms-excel, */*")
                .retrieve()
                .toEntity(Resource.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            final Resource resource = response.getBody();

            // Извлекаем имя файла из заголовка или URL
            final String fileName = extractFileName(response, fileUrl);
            final Path savePath = Paths.get(saveDirectory).resolve(fileName);

            try (final InputStream is = resource.getInputStream();
                 final FileOutputStream os = new FileOutputStream(savePath.toFile())) {
                is.transferTo(os);
            }

            log.info("Файл успешно скачан: {}", savePath);
        } else {
            log.error("Ошибка при скачивании: {}", response.getStatusCode());
        }
    }

    private String extractFileName(final ResponseEntity<Resource> response, final String url) {
        final String disposition = response.getHeaders().getContentDisposition().getFilename();
        if (disposition != null) {
            return disposition;
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
