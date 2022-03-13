package net.evercode.lawfirm.domain;

import net.evercode.lawfirm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Configs.class);
        Configs configs1 = new Configs();
        configs1.setId(1L);
        Configs configs2 = new Configs();
        configs2.setId(configs1.getId());
        assertThat(configs1).isEqualTo(configs2);
        configs2.setId(2L);
        assertThat(configs1).isNotEqualTo(configs2);
        configs1.setId(null);
        assertThat(configs1).isNotEqualTo(configs2);
    }
}
