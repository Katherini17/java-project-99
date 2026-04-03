package hexlet.code.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageUtils {

    private static final String TOTAL_COUNT_HEADER = "X-Total-Count";

    public static <T> ResponseEntity<List<T>> buildPagingResponse(Page<T> page) {

        return ResponseEntity.ok()
                .header(TOTAL_COUNT_HEADER, String.valueOf(page.getTotalElements()))
                .header("Access-Control-Expose-Headers", TOTAL_COUNT_HEADER)
                .body(page.getContent());
    }

    public static <T> ResponseEntity<List<T>> buildPagingResponse(List<T> content) {
        return ResponseEntity.ok()
                .header(TOTAL_COUNT_HEADER, String.valueOf(content.size()))
                .header("Access-Control-Expose-Headers", TOTAL_COUNT_HEADER)
                .body(content);
    }

}
