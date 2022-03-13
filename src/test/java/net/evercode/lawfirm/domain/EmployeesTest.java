package net.evercode.lawfirm.domain;

import net.evercode.lawfirm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employees.class);
        Employees employees1 = new Employees();
        employees1.setId(1L);
        Employees employees2 = new Employees();
        employees2.setId(employees1.getId());
        assertThat(employees1).isEqualTo(employees2);
        employees2.setId(2L);
        assertThat(employees1).isNotEqualTo(employees2);
        employees1.setId(null);
        assertThat(employees1).isNotEqualTo(employees2);
    }
}
