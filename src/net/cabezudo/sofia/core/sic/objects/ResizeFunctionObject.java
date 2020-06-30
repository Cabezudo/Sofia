package net.cabezudo.sofia.core.sic.objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.cabezudo.sofia.core.logger.Logger;
import net.cabezudo.sofia.core.server.images.SofiaImage;
import net.cabezudo.sofia.core.sic.SICCompilerMessages;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFunction;
import net.cabezudo.sofia.core.sic.elements.SICParameter;
import net.cabezudo.sofia.core.sic.elements.SICParameters;
import net.cabezudo.sofia.core.sic.objects.values.SICInteger;
import net.cabezudo.sofia.core.sic.objects.values.SICNumber;
import net.cabezudo.sofia.core.sic.objects.values.SICPercentage;
import net.cabezudo.sofia.core.sic.objects.values.SICPixels;
import net.cabezudo.sofia.core.sic.objects.values.SICValue;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.14
 */
public class ResizeFunctionObject extends SICObjectFunction {

  private final List<SICObject> list;
  private SICNumber<?> widthParameter;
  private SICNumber<?> heightParameter;
  private SICNumber<?> scaleParameter;

  public ResizeFunctionObject(SICParameters parameters, SICCompilerMessages messages) {
    this.list = new ArrayList<>();

    SICElement parameterOrFunction = parameters.consume();
    while (parameterOrFunction != null) {
      if (parameterOrFunction.isParameter()) {
        SICParameter parameter = (SICParameter) parameterOrFunction;
        SICParameter parameterNameToken = parameter;
        String parameterName = parameter.getName();
        switch (parameterName) {
          case "width":
            widthParameter = getPixelsOrPercentage(widthParameter, parameter, messages);
            Logger.debug("Set the width parameter to %s.", widthParameter);
            if (scaleParameter != null) {
              messages.add("You can't set the width parameter with the scale parameter.", parameterNameToken.getPosition());
            }
            if (widthParameter.isZero()) {
              messages.add("You can't set the width parameter to zero.", parameterNameToken.getPosition());
            }
            break;
          case "height":
            heightParameter = getPixelsOrPercentage(heightParameter, parameter, messages);
            Logger.debug("Set the height parameter to %s.", heightParameter);
            if (scaleParameter != null) {
              messages.add("You can't set the height parameter with the scale parameter.", parameterNameToken.getPosition());
            }
            if (heightParameter.isZero()) {
              messages.add("You can't set the height parameter to zero.", parameterNameToken.getPosition());
            }
            break;
          case "scale":
            SICValue<?> value = ValueFactory.get(parameter.getValueToken());
            if (!value.isDecimal() && !value.isInteger()) {
              messages.add("Invalid value " + value + " for a scale.", parameterNameToken.getPosition());
            }
            scaleParameter = (SICNumber) value;
            Logger.debug("Set the scale parameter to %s.", scaleParameter);
            if (widthParameter != null) {
              messages.add("You can't set the scale parameter with the width parameter.", parameterNameToken.getPosition());
            }
            if (heightParameter != null) {
              messages.add("You can't set the scale parameter with the height parameter.", parameterNameToken.getPosition());
            }
            break;
          default:
            messages.add("Unexpected parameter " + parameterName + ".", parameterNameToken.getPosition());
        }
      } else {
        SICFunction functionParameter = (SICFunction) parameterOrFunction;
        SICObject sicObject = functionParameter.compile(messages);
        list.add(sicObject);
      }
      parameterOrFunction = parameters.consume();
    }
  }

  @Override
  public SofiaImage run(SofiaImage sofiaImage) throws SICRuntimeException {
    Integer width = null;
    Integer height = null;
    if (scaleParameter == null) {
      do {
        if (widthParameter == null && heightParameter == null) {
          throw new RuntimeException("I don't have width, height nor scale parameters.");
        }
        double ratio = sofiaImage.getWidth() / (double) sofiaImage.getHeight();
        Logger.fine("[ResizeFunctionObject:run] Ratio: %s.", ratio);
        if (widthParameter == null) {
          height = getPixels(heightParameter, sofiaImage.getHeight());
          width = (int) (ratio * height);
          break;
        }
        if (heightParameter == null) {
          width = getPixels(widthParameter, sofiaImage.getWidth());
          height = (int) (width / ratio);
          break;
        }
        width = getPixels(widthParameter, sofiaImage.getWidth());
        height = getPixels(heightParameter, sofiaImage.getHeight());
      } while (false);
      Logger.info("[ResizeFunctionObject:run] Resize image using a width of %spx and a height of %spx", width, height);
    } else {
      BigDecimal scale = new BigDecimal(scaleParameter.getValue().toString());
      width = (int) (sofiaImage.getWidth() * scale.doubleValue());
      height = (int) (sofiaImage.getHeight() * scale.doubleValue());
      Logger.info("[ResizeFunctionObject:run] Resize image using a scale of %s to width of %spx and a height of %spx", scale, width, height);
    }

    BufferedImage newImage = new BufferedImage(width, height, sofiaImage.getImage().getType());
    Graphics2D g2d = newImage.createGraphics();
    g2d.drawImage(sofiaImage.getImage(), 0, 0, width, height, null);
    g2d.dispose();

    SofiaImage newSofiaImage = new SofiaImage(sofiaImage.getImagePath(), newImage);
    return newSofiaImage;
  }

  private SICNumber<?> getPixelsOrPercentage(SICNumber<?> v, SICParameter parameter, SICCompilerMessages messages) {
    String name = parameter.getNameToken().getValue();
    if (v != null) {
      messages.add("Value " + name + " was already asigned with a " + v.getValue() + ".", parameter.getValueToken().getPosition());
    }
    SICValue<?> value = ValueFactory.get(parameter.getValueToken());
    if (value.isNumber()) {
      SICNumber<?> number = (SICNumber) value;
      if (number.isInteger()) {
        return new SICPixels(parameter.getValueToken(), (SICInteger) value);
      }
      if (number.isPercentage()) {
        return new SICPercentage(parameter.getValueToken(), (BigDecimal) value.getValue());
      }
      if (!value.isPixels() && !value.isPercentage()) {
        Position position = parameter.getValueToken().getPosition();
        String type = value.getTypeName();
        messages.add("A " + name + " must be a number or percentage but have a " + type + ".", position);
        parameter.getValueToken().setError(true);
        return new SICInvalidValue(parameter.getValueToken());
      }
    }
    Logger.debug("[ResizeFunctionObject:getPixelsOrPercentage] Value type: %s", value.getClass().getName());
    messages.add("Invalid value " + parameter.getValueToken().getValue() + " for " + name + ".", parameter.getValueToken().getPosition());
    parameter.getValueToken().setError(true);
    return new SICInvalidValue(parameter.getValueToken());
  }
}
