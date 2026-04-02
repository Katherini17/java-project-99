package hexlet.code.util;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class PageUtils {

    public static <T>ResponseEntity<List<T>> buildPagingResponse(Page<T> page) {
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(page.getTotalElements()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(page.getContent());
    }

    public static <T>ResponseEntity<List<T>> buildPagingResponse(List<T> content) {
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(content.size()))
                .header("Access-Control-Expose-Headers", "X-Total-Count")
                .body(content);
    }

}
