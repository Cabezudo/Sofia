package net.cabezudo.sofia.core.creator;

/**
 *
 * @author estebancabezudo
 */
class SofiaJSONSource extends SofiaSource {

  public SofiaJSONSource(Caller caller) {
    super(caller);
  }

  @Override
  Type getType() {
    return Type.JSON;
  }

  @Override
  String getDescription() {
    return super.getPartialPath().toString();
  }
}
