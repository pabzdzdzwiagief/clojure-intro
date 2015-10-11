package com.github.pabzdzdzwiagief.clojure.intro.session1;

import clojure.lang.Keyword;
import clojure.lang.Symbol;
import org.junit.Test;

import java.math.BigDecimal;

import static clojure.java.api.Clojure.read;
import static org.junit.Assert.*;

/**
 * Since Clojure is so much about data, let's start
 * with syntax used to denote it.
 *
 * The syntax has been formalized as
 * Extensible Data Notation, or in short "edn".
 *
 * Common data types like integers or strings are represented by the same
 * underlying classes.
 * Clojure has however provides wider choice of literals than Java.
 */
public final class EdnPrimitivesReaderTest {
    @Test
    public void clojureNilIsJavaNull() {
        assertNull(read("nil"));
        assertNull(read(""));
    }

    @Test
    public void clojureHasNumberLiterals() {
        assertEquals(1L, read("1"));
        assertEquals(1.0, read("1.0"));
        assertEquals(BigDecimal.valueOf(1.23), read("1.23M"));
    }

    @Test
    public void booleansAreAlsoPresent() {
        assertEquals(true, read("true"));
        assertEquals(false, read("false"));
    }

    @Test
    public void clojureBooleansAreSingletons() {
        assertSame(read("true"), read("true"));
        assertEquals(read("false"), read("false"));
    }

    @Test
    public void clojureHasCharacterLiterals() {
        assertEquals('A', read("\\A"));
    }

    @Test
    public void clojureHasStringLiteralsAnalogousToJava() {
        assertEquals("some string", read("\"some string\""));
    }

    @Test
    public void unquotedTokensAreSymbols() {
        assertTrue(read("a-symbol") instanceof Symbol);
    }

    @Test
    public void symbolsCanHavePrefixes() {
        assertTrue(read("a-prefix/a-symbol") instanceof Symbol);
    }

    @Test
    public void leftSideOfSymbolIsAName() {
        assertEquals("a-symbol", ((Symbol) read("a-prefix/a-symbol")).getName());
    }

    @Test
    public void rightSideOfSymbolIsANamespace() {
        assertEquals("a-prefix", ((Symbol) read("a-prefix/a-symbol")).getNamespace());
    }

    @Test
    public void symbolsWithoutPrefixesHaveNoNamespace() {
        assertNull(((Symbol) read("a-symbol")).getNamespace());
    }

    @Test
    public void symbolsMayHaveNonAlphanumericCharacters() {
        assertTrue(read("valid-symbols/=8-D-|-<") instanceof Symbol);
    }

    @Test
    public void mathematicalOperatorsAreAlsoValidSymbols() {
        assertTrue(read("+") instanceof Symbol);
        assertTrue(read("-") instanceof Symbol);
        assertTrue(read("*") instanceof Symbol);
        assertTrue(read("/") instanceof Symbol);
    }

    @Test
    public void tokensWithColonInFrontAreKeywords() {
        assertTrue(read(":a-keyword") instanceof Keyword);
    }

    @Test
    public void nameOfAKeywordDoesNotContainLeadingColon() {
        assertEquals("a-keyword", ((Keyword) read(":a-keyword")).getName());
    }

    @Test
    public void keywordNamespacesAreAnalogousToThoseForSymbols() {
        assertEquals("a-namespace", ((Keyword) read(":a-namespace/a-keyword")).getNamespace());
        assertNull(((Keyword) read(":a-keyword")).getNamespace());
    }

    @Test
    public void keywordsAreInterned() {
        assertSame(read(":same"), read(":same"));
    }

    @Test(expected = RuntimeException.class)
    public void colonAloneIsNotAValidToken() {
        read(":");
    }

    @Test
    public void whateverComesAfterSemicolonIsTreatedAsLineComment() {
        assertEquals(42L, read("42 ; the ultimate answer"));
        assertEquals(42L, read("; the ultimate answer\n42"));
    }

    @Test
    public void clojureHasAlsoBlockComments() {
        assertEquals(42L, read("42 #_(the\nultimate\nanswer\n)"));
        assertEquals(42L, read("#_(the\nultimate\nanswer\n) 42"));
    }
}
