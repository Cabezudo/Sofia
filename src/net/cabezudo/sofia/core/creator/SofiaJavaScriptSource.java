package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class SofiaJavaScriptSource extends SofiaSource {

  SofiaJavaScriptSource(Caller caller) {
    super(caller);
  }

  @Override
  Type getType() {
    return Type.JS;
  }

  @Override
  String getDescription() {
    if (getCaller() == null) {
      return "// File " + getPartialPath() + " called by the system.\n";
    } else {
      return "// " + getPartialPath() + " called from " + getCaller() + ".\n";
    }
  }
}
