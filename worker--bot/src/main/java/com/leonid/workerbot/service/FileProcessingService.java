package com.leonid.workerbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.leonid.model.DeployRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileProcessingService {

    @Value("${bot.token}")
    private String botToken;
    @Value("${place.for.download}")
    private String placeForDownload;

    private final RestTemplate restTemplate = new RestTemplate();

    public String resolveSrcFilePath(DeployRequest request) {

        String metaDataFileUrl = String.format("https://api.telegram.org/bot%s/getFile?file_id=%s", botToken, request.getFileId());

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                metaDataFileUrl,
                HttpMethod.GET,
                null,
                JsonNode.class
        );

        JsonNode filePathNode = Objects.requireNonNull(response.getBody()).get("result").get("file_path");
        String realFileUrl = File.getFileUrl(botToken, filePathNode.asText());
        System.err.println(realFileUrl);

        return realFileUrl;

        // todo @Deprecated
//        {
//            String fileName = request.getChatId() + "_" + request.getAppName() + ".zip";
//            byte[] fileBytes = restTemplate.getForObject(fileUrl, byte[].class);
//            Path path = Path.of(placeForDownload, fileName);
//            try {
//                assert fileBytes != null;
//                Files.write(path, fileBytes);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            return path.toString();
//        }
    }

}
