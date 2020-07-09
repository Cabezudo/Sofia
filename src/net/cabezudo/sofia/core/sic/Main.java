package net.cabezudo.sofia.core.sic;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICUnexpectedEndOfCodeException;
import net.cabezudo.sofia.core.sic.exceptions.EmptyQueueException;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.objects.SICRuntimeException;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class Main {

  public static void main(String... args) throws EmptyQueueException, JSONParseException, PropertyNotExistException, SICCompileTimeException, SICUnexpectedEndOfCodeException {
//    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(width=300,height=300))";
    //String code = "    main(    loadImage(   name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg   )  ,  resize( scale   =   0.5   )   ,   resize(    width   =   1200   , height  =  800  )  )";
    //  String code = "\nmain(\nloadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(scale=.2),resize(\nwidth=1200, height=800))";
    //JSONObject json = JSON.parse("{\"code\":\"resize(\\n  width=1200,\\n  height=800,\\n  resize(\\n    width=200,\\n    height=100\\n  )\\n)\\n\"}").toJSONObject();
//    String code = json.getString("code");
//    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(height=300))";
//    String code = "main(loadImage(name=/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/images/test.jpg),resize(width=300,height=300))";
//    test("main(loadImage(name=/images/test.jpg),resize(width=600,height=400)");

// Test for JUnit for the new proyect
//    test("main(loadImage(name=/images/test.600.400.jpg),resize(width=60,height=40))");
//    test("main(loadImage(name=/images/test.600.400.jpg),resize(width=60,height=40),gray(method=luma))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=averaging))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=luma))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=luma,type=basic))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=luma,type=bt709))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=luma,type=bt601))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=desaturation))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=decomposition,type=maximum))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=decomposition,type=minimum))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=colorChannel))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=colorChannel,channel=yellow))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=colorChannel,type=red))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=colorChannel,type=green))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=colorChannel,type=blue))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=grayShades,value=30))");
//    test("main(loadImage(name=/images/test.600.400.jpg),gray(method=grayShades))");
  }

  private static void test(String code) {
    System.out.println("*** " + code);
    Path basePath = Paths.get("/home/esteban/NetBeansProjects/sofia.cabezudo.net/system/sources/sites/manager/1/");
    try {
      SofiaImageCode sofiaImageCode = new SofiaImageCode(basePath, code, true);

      if (sofiaImageCode.getMessages().size() == 0) {
        Tokens tokens = sofiaImageCode.getTokens();
        for (Token token : tokens) {
          System.out.print(token.getValue());
        }

        SICObject object = sofiaImageCode.compile();
        object.run();
      } else {
        System.out.println(sofiaImageCode.getMessages().toJSON());
      }
    } catch (SICCompileTimeException | SICRuntimeException e) {
      System.out.println(e.getMessage() + " " + e.getPosition());
      e.printStackTrace();
    }
  }
}
