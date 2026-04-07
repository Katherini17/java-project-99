package hexlet.code.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageUtilsTest {

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

    @Test
    void testBuildPagingResponseEmptyPage() {
        var page = new PageImpl<>(List.of());

        var response = PageUtils.buildPagingResponse(page);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("0");
        assertThat(response.getBody()).isEmpty();
    }
}
