package net.evercode.lawfirm.domain;

import net.evercode.lawfirm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JobApplicationsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobApplications.class);
        JobApplications jobApplications1 = new JobApplications();
        jobApplications1.setId(1L);
        JobApplications jobApplications2 = new JobApplications();
        jobApplications2.setId(jobApplications1.getId());
        assertThat(jobApplications1).isEqualTo(jobApplications2);
        jobApplications2.setId(2L);
        assertThat(jobApplications1).isNotEqualTo(jobApplications2);
        jobApplications1.setId(null);
        assertThat(jobApplications1).isNotEqualTo(jobApplications2);
    }
}
