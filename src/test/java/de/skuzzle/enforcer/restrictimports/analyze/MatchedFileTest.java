package de.skuzzle.enforcer.restrictimports.analyze;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MatchedFileTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(MatchedFile.class).verify();
    }
}
