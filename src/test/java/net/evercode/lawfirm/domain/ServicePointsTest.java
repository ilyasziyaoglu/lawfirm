package net.evercode.lawfirm.domain;

import net.evercode.lawfirm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServicePointsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServicePoints.class);
        ServicePoints servicePoints1 = new ServicePoints();
        servicePoints1.setId(1L);
        ServicePoints servicePoints2 = new ServicePoints();
        servicePoints2.setId(servicePoints1.getId());
        assertThat(servicePoints1).isEqualTo(servicePoints2);
        servicePoints2.setId(2L);
        assertThat(servicePoints1).isNotEqualTo(servicePoints2);
        servicePoints1.setId(null);
        assertThat(servicePoints1).isNotEqualTo(servicePoints2);
    }
}
