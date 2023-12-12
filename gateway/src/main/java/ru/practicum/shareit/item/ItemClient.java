package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

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

    public ResponseEntity<Object> getUserItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> add(long userId, ItemToGetDto itemToGetDto) {
        return post("", userId, itemToGetDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId,
                                         ItemToGetDto itemToGetDto) {
        return patch("/" + itemId, userId, itemToGetDto);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentToGetDto commentToGetDto) {
        return post("/" + itemId + "/comment", userId, commentToGetDto);
    }

    public ResponseEntity<Object> search(String text, Integer from, Integer size) {
        return get("/search?text=" + text + "&from=" + from + "&size=" + size);
    }
}
