package org.xssfinder;

/**
 * {@code LabelledXssGenerator} objects create XSS attack strings and store them under a reference in case they are
 * then later encountered when detecting successful XSS attacks.
 *
 * Instances of LabelledXssGenerator are provided when invoking a custom submitter.
 *
 * @see CustomSubmitter#submit(Object, LabelledXssGenerator)
 */
public interface LabelledXssGenerator {

    /**
     * Generates an XSS attack string, and stores it under the reference {@code label}.
     * @param label An identifier used to refer to the given XSS attack when reporting on successful attacks. This is
     *              typically the name of the parameter the generated XSS attack is put in, or similar.
     * @return An XSS attack string.
     */
    String getXssAttackTextForLabel(String label);
}
