package model;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

public class HTTPModel {
    @Getter
    String requestMethod;
    @Getter
    String path;
    @Getter
    String contentType;
    @Getter
    String contentLength;
    @Getter
    String authorization;
    @Getter
    String body;

    public HTTPModel(String header, String body) {
        this.body = body;
        String[] headerParts = header.split("\n");
        requestMethod = headerParts[0].split(" ")[0];
        path = headerParts[0].split(" ")[1];

        for (int i = 0; i < headerParts.length; i++) {
            if (headerParts[i].contains("Content-Type: "))
                contentType = headerParts[i].substring("Content-Type: ".length());

            if (headerParts[i].contains("Content-Length: "))
                contentLength = headerParts[i].substring("Content-Length: ".length());

            if (headerParts[i].contains("Authorization: "))
                authorization = headerParts[i].substring("Authorization: ".length()).split(" ")[1];
        }

    }

}
