package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemFullDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(long userId, ItemFullDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> updateItem(long userId, ItemFullDto dto, long id) {
        return patch("/" + id, userId, dto);
    }


    public ResponseEntity<Object> getItemId(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getUserItems(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getSearchItems(long userId, String text) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> saveComment(long itemId, CommentDto dto, long userId) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
