package localhost.intro.session2;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static clojure.java.api.Clojure.read;
import static clojure.java.api.Clojure.var;
import static org.junit.Assert.*;

/**
 * The second operation Clojure Java API exposes is referencing
 * Clojure vars. Vars are wrappers for Clojure functions and as such
 * can be used for invoking Clojure code from Java.
 *
 * Together with read() var() allows to write LISP-like nested expressions
 * directly in Java.
 */
public final class VarTest {
    @Test
    public void symbolsCanBeUsedForRetrievingVars() {
        assertNotNull(var(read("clojure.core/+")));
    }

    @Test
    public void javaApiProvidesAnOverloadThatDoesNotNeedASymbol() {
        assertSame(var("clojure.core", "+"), var(read("clojure.core/+")));
    }

    @Test
    public void varsAreDecoratorsForClojureFunctions() {
        assertTrue(var("clojure.core", "+") instanceof IFn);
    }

    @Test
    public void varsCanBeUsedForCallingUnderlyingFunctions() {
        assertEquals(
                read("4"),
                var("clojure.core", "+").invoke(read("2"), read("2"))
        );
    }

    @Test
    public void clojureFunctionsCanAcceptUsualJavaObjectsAsParameters() {
        assertEquals(
                read("4"),
                var("clojure.core", "+").invoke(2L, 2L)
        );
    }

    @Test
    public void functionsThatOperateOnSequencesCanBeAppliedToJavaCollectionsToo() {
        assertEquals(
                new ArrayList() {{
                    add(3);
                    add(2);
                    add(1);
                }},
                var("clojure.core", "reverse").invoke(
                        new ArrayList() {{
                            add(1);
                            add(2);
                            add(3);
                        }}
                )
        );
    }

    @Test
    public void functionsThatWouldReturnVoidInClojureReturnNil() {
        assertNull(
                var("clojure.core", "println").invoke("Hello world!")
        );
    }

    @Test
    public void readMethodFromJavaApiIsJustAWrapperForClojureFunction() {
        assertEquals(
                read("(same :thing)"),
                var("clojure.edn", "read-string").invoke("(same :thing)")
        );
    }

    @Test
    public void varsWithSameNameCanExistInDifferentNamespaces() {
        assertNotSame(
                var("clojure.core", "read-string"),
                var("clojure.edn", "read-string")
        );
    }

    @Test
    public void listOfArgumentsCanBePassedByACollection() {
        assertEquals(
                read("103"),
                var("clojure.core", "+").applyTo(
                        (ISeq) read("(100 3)")
                )
        );
    }

    @Test
    public void clojureFunctionsImplementCallableAndRunnable() throws Exception {
        assertTrue(var("clojure.core", "+") instanceof Runnable);
        assertTrue(var("clojure.core", "+") instanceof Callable);
        assertEquals(0L, var("clojure.core", "+").call());
    }

    @Test
    public void collectionsHaveTheirCorrespondingConstructorFunctions() {
        assertEquals(read("(1 2 3)"), var("clojure.core", "list").invoke(1L, 2L, 3L));
        assertEquals(read("[1 2 3]"), var("clojure.core", "vector").invoke(1L, 2L, 3L));
        assertEquals(read("#{1 2 3}"), var("clojure.core", "hash-set").invoke(1L, 2L, 3L));
        assertEquals(read("{1 2 3 4}"), var("clojure.core", "array-map").invoke(1L, 2L, 3L, 4L));
        assertEquals(read("{1 2 3 4}"), var("clojure.core", "hash-map").invoke(1L, 2L, 3L, 4L));
    }
}
