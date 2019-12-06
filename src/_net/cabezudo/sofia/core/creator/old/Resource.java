package net.cabezudo.sofia.core.creator;

import java.nio.file.Path;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.26
 */
class Resource implements Comparable<Resource> {

  private final Path origin;
  private final Path target;

  Resource(Path origin, Path target) {
    this.origin = origin;
    this.target = target;
  }

  Path getOrigin() {
    return origin;
  }

  Path getTarget() {
    return target;
  }

  @Override
  public int compareTo(Resource r) {
    return getOrigin().compareTo(r.getOrigin());
  }

  @Override
  public String toString() {
    return "[" + getOrigin() + ", " + getTarget() + "]";
  }
}
