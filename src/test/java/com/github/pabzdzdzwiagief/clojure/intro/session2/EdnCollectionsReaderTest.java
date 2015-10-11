package com.github.pabzdzdzwiagief.clojure.intro.session2;

import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentHashMap;
import org.junit.Test;

import java.util.*;

import static clojure.java.api.Clojure.read;
import static org.junit.Assert.*;

/**
 * Clojure as a proper LISP has lists.
 *
 * edn also defines literals for all the usual data structures
 * such as maps, sets or vectors. Each of them is immutable
 * and supports structural sharing.
 */
public final class EdnCollectionsReaderTest {
    @Test
    public void parenthesesDenoteAnEmptyList() {
        assertEquals(Collections.emptyList(), read("()"));
    }

    @Test
    public void listElementsAreSeparatedWithWhiteSpace() {
        assertEquals(new ArrayList() {{
            add(read("1"));
            add(read("2"));
            add(read("3"));
        }}, read("(1 2 3)"));
    }

    @Test
    public void commasCanBeUsedForSeparatingElementsButAreTreatedLikeWhitespace() {
        assertEquals(new ArrayList() {{
            add(read("1"));
            add(read("2"));
            add(read("3"));
            add(read("4"));
        }}, read("(1, 2 3,,,,,,, 4)"));
    }


    @Test
    public void clojureListsArePlainJavaLists() {
        assertTrue(read("()") instanceof List);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clojureListsDoNotImplementOptionalPartOfListInterface() {
        ((List) read("(1 2 3)")).add(read("4"));
    }

    @Test
    public void listsCanBeHeterogeneous() {
        assertEquals(new ArrayList() {{
            add(read("1"));
            add(read(":a"));
            add(read("b"));
            add(read("\"string\""));
        }}, read("(1 :a b \"string\")"));
    }

    @Test
    public void listsCanBeNested() {
        assertEquals(new ArrayList() {{
            add(read("1"));
            add(read("2"));
            add(new ArrayList() {{
                add(read("3"));
                add(read("4"));
                add(new ArrayList() {{
                    add(read("5"));
                }});
                add(read("6"));
            }});
        }}, read("(1 2 (3 4 (5) 6))"));
    }

    @Test
    public void squareBracketsDenoteAVectorWhichWorksTheSameWayAsList() {
        assertEquals(new ArrayList() {{
            add(read("1"));
            add(read("2"));
            add(new ArrayList() {{
                add(read("3"));
                add(read("4"));
                add(new ArrayList() {{
                    add(read("5"));
                }});
                add(read("6"));
            }});
        }}, read("[1 2 [3 4 [5] 6]]"));
    }

    @Test
    public void curlyBracesWithLeadingHashAreUsedForSetLiterals() {
        assertEquals(new HashSet() {{
            add(read("1"));
            add(read("2"));
            add(read("3"));
        }}, read("#{1 2 3}"));
    }

    @Test
    public void clojureSetsArePlainJavaSets() {
        assertTrue(read("#{1 :a b}") instanceof Set);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clojureSetsLikeListsAndVectorsAreNotModifiable() {
        ((Set) read("#{1 :a b}")).add(read("4"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLiteralsCannotContainDuplicates() {
        read("#{1 2 2}");
    }

    @Test
    public void curlyBracesWithoutLeadingColonAreForMaps() {
        assertTrue(read("{}") instanceof Map);
    }

    @Test
    public void oddElementsAreKeysAndEvenElementsAreValues() {
        assertEquals(new HashMap() {{
            put(read(":key1"), read(":value1"));
            put(read("key2"), "value2");
        }}, read("{:key1 :value1 key2 \"value2\"}"));
    }

    @Test
    public void unlikeInPythonListsArePerfectlyValidAsKeys() {
        assertEquals(new HashMap() {{
            put(read("(a list)"), read(":value"));
            put(read("(another list)"), "another value");
        }}, read("{(a list) :value (another list) \"another value\"}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void likeInSetsDuplicateKeysAreNotValid() {
        read("{(duplicate key) 1 (duplicate key) 2}");
    }

    @Test(expected = RuntimeException.class)
    public void numberOfMapElementsMustBeEven() {
        read("{(duplicate key) 1 :is-it-a-key-or-a-value}");
    }

    @Test
    public void mapLiteralsUpToEightElementsAreArrayMaps() {
        assertTrue(read("{1 1 2 2 3 3 4 4 5 5 6 6 7 7 8 8}") instanceof PersistentArrayMap);
    }

    @Test
    public void mapsBiggerThanEightElementsBecomeHashMaps() {
        assertTrue(read("{1 1 2 2 3 3 4 4 5 5 6 6 7 7 8 8 9 9}") instanceof PersistentHashMap);
    }
}
