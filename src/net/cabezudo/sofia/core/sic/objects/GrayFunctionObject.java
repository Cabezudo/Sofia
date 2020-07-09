package net.cabezudo.sofia.core.sic.objects;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.values.SICGrayMethod;
import net.cabezudo.sofia.core.sic.objects.values.SICGrayMethodType;
import net.cabezudo.sofia.core.sic.objects.values.SICGrayShades;
import net.cabezudo.sofia.core.sic.objects.values.SICInteger;
import net.cabezudo.sofia.core.sic.objects.values.SICString;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class GrayFunctionObject extends SICObjectFunction {

  private final List<SICObject> list;
  private SICGrayMethod methodParameter;
  private SICString methodTypeParameter;
  private SICInteger valueParameter;

  public GrayFunctionObject(Path basePath, Token token, SICParameters parameters) throws SICCompileTimeException {
    this.list = new ArrayList<>();

    SICElement parameterOrFunction = parameters.consume();
    while (parameterOrFunction != null) {
      if (parameterOrFunction.isParameter()) {
        SICParameter parameter = (SICParameter) parameterOrFunction;
        SICParameter parameterNameToken = parameter;
        String parameterName = parameter.getName();
        Token parameterValue = parameter.getValueToken();
        switch (parameterName) {
          case "method":
            methodParameter = new SICGrayMethod(parameterValue);
            break;
          case "type":
            methodTypeParameter = new SICString(parameterValue);
            break;
          case "value":
            valueParameter = new SICInteger(parameterValue);
            break;
          default:
            throw new SICCompileTimeException("Unexpected parameter " + parameterName + ".", parameterNameToken.getToken());
        }
      } else {
        throw new SICCompileTimeException("Unexpected function " + parameterOrFunction + ".", parameterOrFunction.getToken());
      }
      parameterOrFunction = parameters.consume();
    }
    if (methodParameter != null && methodTypeParameter != null) {
      methodTypeParameter = new SICGrayMethodType(methodParameter.getToken(), methodTypeParameter.getToken());
    }
    if (methodParameter != null && SICGrayShades.TYPE_NAME.equals(methodParameter.getValue())) {
      if (valueParameter == null) {
        throw new SICCompileTimeException("A " + SICGrayShades.TYPE_NAME + " method must have a value for the number of shadows.", token);
      }
      valueParameter = new SICGrayShades(valueParameter.getToken());
    }
  }

  @Override
  public SofiaImage run(SofiaImage sofiaImage) throws SICRuntimeException {
    BufferedImage image = sofiaImage.getImage();
    BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    bi.getGraphics().drawImage(image, 0, 0, null);

    String methodName = "luma";
    if (methodParameter != null) {
      methodName = methodParameter.getValue();
    }
    String methodTypeName = "bt601";
    if (methodTypeParameter != null) {
      methodTypeName = methodTypeParameter.getValue();
    }
    switch (methodName) {
      case "averaging":
        applyGrayshades(bi, new Average());
        break;
      case "luma":
        switch (methodTypeName) {
          case "basic":
            applyGrayshades(bi, new BasicLuma());
            break;
          case "bt709":
            applyGrayshades(bi, new BT709Luma());
            break;
          case "bt601":
            applyGrayshades(bi, new BT601Luma());
            break;
          default:
            throw new RuntimeException("Invalid method type name " + methodTypeName + ".");
        }
        break;
      case "desaturation":
        break;
      case "decomposition":
        break;
      case "colorChannel":
        break;
      case "grayShades":
        int value = valueParameter.getValue();
        applyGrayshades(bi, new GrayShades(), value);
        break;
      default:
        throw new RuntimeException("Unexpected value " + methodName + ".");
    }
    return new SofiaImage(sofiaImage.getImagePath(), bi);
  }

  private void applyGrayshades(BufferedImage bi, GrayshadeMethod method) {
    applyGrayshades(bi, method, 0);
  }

  private void applyGrayshades(BufferedImage bi, GrayshadeMethod method, int value) {
    int[] pixel = {0, 0, 0, 0};
    for (int y = 0; y < bi.getHeight(); y++) {
      for (int x = 0; x < bi.getWidth(); x++) {
        bi.getRaster().getPixel(x, y, pixel);
        int[] newPixel = method.getPixel(pixel, value);
        bi.getRaster().setPixel(x, y, newPixel);
      }
    }
  }

  interface GrayshadeMethod {

    int[] getPixel(int[] pixel, int value);
  }

  class Average implements GrayshadeMethod {

    @Override
    public int[] getPixel(int[] pixel, int value) {
      int red = pixel[0];
      int green = pixel[0];
      int blue = pixel[0];
      int alpha = pixel[0];
      int newValue = (int) ((float) (red + green + blue) / 3);
      int[] newPixel = new int[4];
      newPixel[0] = newValue;
      newPixel[1] = newValue;
      newPixel[2] = newValue;
      newPixel[3] = alpha;
      return newPixel;
    }
  }

  class BasicLuma implements GrayshadeMethod {

    @Override
    public int[] getPixel(int[] pixel, int value) {
      int red = pixel[0];
      int green = pixel[0];
      int blue = pixel[0];
      int alpha = pixel[0];
      int newValue = (int) (red * 0.3f + green * 0.59f + blue * 0.11f);
      int[] newPixel = new int[4];
      newPixel[0] = newValue;
      newPixel[1] = newValue;
      newPixel[2] = newValue;
      newPixel[3] = alpha;
      return newPixel;
    }

  }

  class BT601Luma implements GrayshadeMethod {

    @Override
    public int[] getPixel(int[] pixel, int value) {
      int red = pixel[0];
      int green = pixel[0];
      int blue = pixel[0];
      int alpha = pixel[0];
      int newValue = (int) (red * 0.299f + green * 0.587f + blue * 0.114f);
      int[] newPixel = new int[4];
      newPixel[0] = newValue;
      newPixel[1] = newValue;
      newPixel[2] = newValue;
      newPixel[3] = alpha;
      return newPixel;
    }

  }

  class BT709Luma implements GrayshadeMethod {

    @Override
    public int[] getPixel(int[] pixel, int value) {
      int red = pixel[0];
      int green = pixel[0];
      int blue = pixel[0];
      int alpha = pixel[0];
      int newValue = (int) (red * 0.2126f + green * 0.7152f + blue * 0.0722f);
      int[] newPixel = new int[4];
      newPixel[0] = newValue;
      newPixel[1] = newValue;
      newPixel[2] = newValue;
      newPixel[3] = alpha;
      return newPixel;
    }

  }

  class GrayShades implements GrayshadeMethod {

    @Override
    public int[] getPixel(int[] pixel, int numberOfShadows) {
      int red = pixel[0];
      int green = pixel[0];
      int blue = pixel[0];
      int alpha = pixel[0];
      int conversionFactor = 255 / (numberOfShadows - 1);
      int averageValue = (int) ((float) (red + green + blue) / 3);
      int newValue = Math.round(averageValue / (float) conversionFactor) * conversionFactor;
      int[] newPixel = new int[4];
      newPixel[0] = newValue;
      newPixel[1] = newValue;
      newPixel[2] = newValue;
      newPixel[3] = alpha;
      return newPixel;
    }
  }

}
