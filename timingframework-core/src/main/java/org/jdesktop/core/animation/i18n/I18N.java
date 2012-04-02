package org.jdesktop.core.animation.i18n;

import java.util.ResourceBundle;

import com.surelogic.RegionEffects;
import com.surelogic.Utility;

/**
 * A utility that manages a resource bundle of strings based upon the locale for
 * an application.
 * <p>
 * Numbered errors are read from the <tt>Err</tt> file found in this package.
 * 
 * @author Tim Halloran
 */
@Utility
public final class I18N {

  private static final ResourceBundle ERR = ResourceBundle.getBundle(I18N.class.getPackage().getName() + ".Err");

  private static final String ERROR_FORMAT = "(Timing Framework #%d) %s";

  @RegionEffects("reads any(java.util.ResourceBundle):Instance")
  private static String getString(final String keyTemplate, final Object... args) {
    return ERR.getString(String.format(keyTemplate, args));
  }

  /**
   * Gets the string defined for the given error number from the i18 resource
   * bundle. The key for the error message in the <tt>Err</tt> properties file
   * is <tt>error.</tt><i>nnnnn</i>. For example, <tt>I18N.err(23)</tt> would
   * result in the string <tt>"(Timing Framework #23) A singular problem."</tt>
   * if the line <tt>error.00023=A singular problem.</tt>is contained in the
   * <tt>Err</tt> properties file. If the key is not defined in the <tt>Err</tt>
   * properties file an exception is thrown.
   * 
   * @param number
   *          the error message number.
   * @return the error message for the given number.
   */
  @RegionEffects("reads any(java.util.ResourceBundle):Instance")
  public static String err(final int number) {
    final String result = getString("error.%05d", number);
    return String.format(ERROR_FORMAT, number, result);
  }

  /**
   * Gets and formats the string defined for the given error number from the i18
   * resource bundle. Calling this method is equivalent to calling
   * <tt>String.format(I18N.err(number), args).</tt>
   * 
   * The key for the error message in the <tt>Err</tt> properties file is
   * <tt>error.</tt><i>nnnnn</i>. For example, <tt>I18N.err(24, "bad")</tt>
   * would result in the string <tt>"(Timing Framework #24) A bad problem."</tt>
   * if the line <tt>error.00024=A %s problem.</tt> is contained in the
   * <tt>Err</tt> properties file. If the key is not defined in the <tt>Err</tt>
   * properties file an exception is thrown.
   * 
   * @param number
   *          the error message number.
   * @param args
   *          the variable arguments to format the resulting error message with.
   * @return the formatted error message for the given number.
   * @see String#format(String, Object...)
   */
  @RegionEffects("reads any(java.util.ResourceBundle):Instance")
  public static String err(final int number, Object... args) {
    return String.format(I18N.err(number), args);
  }

  private I18N() {
    // utility
  }
}
