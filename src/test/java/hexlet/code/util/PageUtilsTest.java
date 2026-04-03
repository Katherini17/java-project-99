package hexlet.code.util;

import org.springframework.http.HttpStatus;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;

class PageUtilsTest {

    @Test
    void testBuildPagingResponseFromList() {
        var content = List.of("item1", "item2", "item3");

        var response = PageUtils.buildPagingResponse(content);

        assertThat(response).isNotNull().satisfies(res -> {
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");
            assertThat(res.getHeaders().getFirst("Access-Control-Expose-Headers")).isEqualTo("X-Total-Count");
            assertThat(res.getBody()).containsExactly("item1", "item2", "item3");
        });
    }

    @Test
    void testBuildPagingResponseFromPage() {
        var content = List.of("item1");
        var page = new PageImpl<>(content);

        var response = PageUtils.buildPagingResponse(page);

        assertThat(response).isNotNull().satisfies(res -> {
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(res.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");
            assertThat(res.getBody()).containsExactly("item1");
        });
    }
}
