package id.ac.ui.cs.advprog.udehnihreviewrating.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentDetailsTest {

    private StudentDetails studentDetails;
    private final Long testId = 456L;
    private final String testEmail = "test@example.com";
    private final List<SimpleGrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));

    @BeforeEach
    void setUp() {
        studentDetails = new StudentDetails(testId, testEmail, authorities);
    }

    @Test
    void testGetId() {
        assertEquals(testId, studentDetails.getId());
    }

    @Test
    void testGetEmail() {
        assertEquals(testEmail, studentDetails.getEmail());
    }

    @Test
    void testGetUsername() {
        assertEquals(testEmail, studentDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        assertNull(studentDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        assertEquals(authorities, studentDetails.getAuthorities());
        assertTrue(studentDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_STUDENT"));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(studentDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(studentDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(studentDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(studentDetails.isEnabled());
    }
}