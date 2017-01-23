package dk.nykredit.api.capabilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SanitizerTest {

    @Test
    public void testOkInput() {
        assertEquals("This is ok input", Sanitizer.sanitize("This is ok input", true));
        assertEquals("This is ok input 2", Sanitizer.sanitize("This is ok input 2", true));
    }

    @Test
    public void testNullInput() {
        assertEquals("", Sanitizer.sanitize(null, true));
        assertEquals("", Sanitizer.sanitize(null, false));
    }

    @Test
    public void testInputWithoutSpace() {
        assertEquals("ThisIsOKInput", Sanitizer.sanitize("ThisIsOKInput", false));
        assertEquals("ThisIsOKInput2", Sanitizer.sanitize("ThisIsOKInput2", false));
        assertEquals("Thisisokinput", Sanitizer.sanitize("This is ok input", false));
    }

    @Test
    public void testSuspiciousInput() {
        assertEquals("This is ok input", Sanitizer.sanitize("This is ok input", true));
        assertEquals("This is ok input 2", Sanitizer.sanitize("This is ok input 2", true));
        assertEquals("", Sanitizer.sanitize("This is not ok input 2 '", true));
        assertEquals("", Sanitizer.sanitize("This is not ok input 2 'OR 1", true));
    }
}
