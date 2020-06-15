package net.cabezudo.sofia.core.server.images;

import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sic.SofiaImageCode;
import net.cabezudo.sofia.core.sic.exceptions.EmptyQueueException;
import net.cabezudo.sofia.core.sic.exceptions.SICException;
import net.cabezudo.sofia.core.sic.objects.SICObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class Main {

  public static void main(String... args) throws EmptyQueueException {
//    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(width=300,height=300))";
//    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(scale=0.5))";
    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(scale=.2),resize(width=1200, height=800))";
//    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(height=300))";
    try {
      SofiaImageCode sofiaImageCode = new SofiaImageCode(code);
      System.out.println(sofiaImageCode.getCode());
      System.out.println(sofiaImageCode.getFormatedCode());
      SICObject sicObject = sofiaImageCode.compile();
      sicObject.run();
    } catch (SICException e) {
      System.out.println(e.getMessage());
      System.out.println(code);
      System.out.println(Utils.repeat(' ', e.getPosition().getRow() - 1) + "^");
      e.printStackTrace();
    }
  }
}
