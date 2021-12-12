package edu.cmu.cs.cs214.hw5.core;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * KeywordTest - tests the Keyword class
 */
public class KeywordTest {
    Keyword keyword;

    @Before
    public void setUp() {
        keyword = new Keyword("Hello", (long)1, 2.0);
    }

    @Test
    public void testWord() {
        assertEquals(keyword.getWord(), "Hello");
    }

    @Test
    public void testFrequency() {
        assertEquals(keyword.getFrequency(), Long.valueOf(1));
    }

    @Test
    public void testRelevance() {
        assertEquals(keyword.getRelevance(), Double.valueOf(2.0));
    }


}
