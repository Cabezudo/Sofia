package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.06
 */
public class LibraryVersionConflictException extends Exception {

  private final Library actualLibrary;
  private final Library newLibrary;

  public LibraryVersionConflictException(Library actualLibrary, Library newLibrary) {
    super("The library " + newLibrary + " called by " + newLibrary.getCaller() + " is in version conflict with an existent library " + actualLibrary + " called by " + actualLibrary.getCaller() + ".");
    this.actualLibrary = actualLibrary;
    this.newLibrary = newLibrary;
  }
}
