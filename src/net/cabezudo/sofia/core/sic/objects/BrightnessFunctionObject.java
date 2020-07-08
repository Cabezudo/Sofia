package net.cabezudo.sofia.core.sic.objects;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.values.SICColorModel;
import net.cabezudo.sofia.core.sic.objects.values.SICInteger;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class BrightnessFunctionObject extends SICObjectFunction {

  private final List<SICObject> list;
  private SICInteger valueParameter;
  private SICColorModel modelParameter;

  public BrightnessFunctionObject(Path basePath, Token token, SICParameters parameters) throws SICCompileTimeException {
    this.list = new ArrayList<>();

    SICElement parameterOrFunction = parameters.consume();
    while (parameterOrFunction != null) {
      if (parameterOrFunction.isParameter()) {
        SICParameter parameter = (SICParameter) parameterOrFunction;
        SICParameter parameterNameToken = parameter;
        String parameterName = parameter.getName();
        Token parameterValue = parameter.getValueToken();
        switch (parameterName) {
          case "value":
            valueParameter = new SICInteger(parameterValue);
            break;
          case "model":
            modelParameter = new SICColorModel(parameterValue);
            break;
          default:
            throw new SICCompileTimeException("Unexpected parameter " + parameterName + ".", parameterNameToken.getToken());
        }
      } else {
        throw new SICCompileTimeException("Unexpected function " + parameterOrFunction + ".", parameterOrFunction.getToken());
      }
      parameterOrFunction = parameters.consume();
    }
    String modelName = "tv";
    if (modelParameter != null) {
      modelName = modelParameter.getValue();
    }
    switch (modelName) {
      case "hsb":
        valueParameter = new SICInteger(valueParameter.getToken(), 0, 3000);
        break;
      case "tv":
        valueParameter = new SICInteger(valueParameter.getToken(), -100, 100);
        break;
      default:
        throw new RuntimeException("Unexpected value " + modelName + ".");
    }
  }

  @Override
  public SofiaImage run(SofiaImage sofiaImage) throws SICRuntimeException {
    BufferedImage image = sofiaImage.getImage();
    BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    bi.getGraphics().drawImage(image, 0, 0, null);

    String modelName = "tv";
    if (modelParameter != null) {
      modelName = modelParameter.getValue();
    }

    switch (modelName) {
      case "hsb":
        applyHSBBrightness(bi);
        break;
      case "tv":
        applyTVBrightness(bi);
        break;
      default:
        throw new RuntimeException("Unexpected value " + modelName + ".");
    }
    return new SofiaImage(sofiaImage.getImagePath(), bi);
  }

  private void applyTVBrightness(BufferedImage bi) {
    int[] pixel = {0, 0, 0, 0};
    int value = valueParameter.getValue();

    for (int y = 0; y < bi.getHeight(); y++) {
      for (int x = 0; x < bi.getWidth(); x++) {
        bi.getRaster().getPixel(x, y, pixel);
        int red = pixel[0];
        int green = pixel[1];
        int blue = pixel[2];
        int alpha = pixel[3];

        int newRed;
        int newGreen;
        int newBlue;
        if (value > 0) {
          newRed = Math.min(255, red + value);
          newGreen = Math.min(255, green + value);
          newBlue = Math.min(255, blue + value);
        } else {
          value = -value;
          newRed = Math.max(0, red - value);
          newGreen = Math.max(0, green - value);
          newBlue = Math.max(0, blue - value);
        }
        int newAlpha = alpha;
        bi.getRaster().setPixel(x, y, new int[]{newRed, newGreen, newBlue, newAlpha});
      }
    }
  }

  private void applyHSBBrightness(BufferedImage bi) {
    int[] pixel = {0, 0, 0, 0};
    float[] hsb = new float[3];
    float value = (float) valueParameter.getValue() / 100;

    for (int y = 0; y < bi.getHeight(); y++) {
      for (int x = 0; x < bi.getWidth(); x++) {
        bi.getRaster().getPixel(x, y, pixel);
        int red = pixel[0];
        int green = pixel[1];
        int blue = pixel[2];
        int alpha = pixel[3];
        Color.RGBtoHSB(red, green, blue, hsb);
        float hue = hsb[0];
        float saturation = hsb[1];
        float brightness;
        if (value > 0) {
          brightness = Math.min(1, hsb[2] * value);
        } else {
          brightness = Math.min(1, hsb[2] * value);
        }
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        int newRed = (rgb >> 16) & 0xFF;
        int newGreen = (rgb >> 8) & 0xFF;
        int newBlue = rgb & 0xFF;
        int newAlpha = alpha;
        bi.getRaster().setPixel(x, y, new int[]{newRed, newGreen, newBlue, newAlpha});
      }
    }
  }
}
