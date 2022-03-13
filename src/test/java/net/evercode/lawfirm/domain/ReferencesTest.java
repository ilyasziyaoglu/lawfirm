package net.evercode.lawfirm.domain;

import net.evercode.lawfirm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReferencesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(References.class);
        References references1 = new References();
        references1.setId(1L);
        References references2 = new References();
        references2.setId(references1.getId());
        assertThat(references1).isEqualTo(references2);
        references2.setId(2L);
        assertThat(references1).isNotEqualTo(references2);
        references1.setId(null);
        assertThat(references1).isNotEqualTo(references2);
    }
}
