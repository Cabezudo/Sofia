package net.cabezudo.sofia.core.texts;

import java.util.Locale;
import net.cabezudo.sofia.core.sites.Site;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.13
 */
public class TextManager {

  private TextManager() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static String get(Site site, Locale locale, String messageKey, Object... parameters) {
    switch (messageKey) {
      case "change.password.user.not.found.by.hash":
        return "El enlace utilizado ya no es válido";
      case "domain.ok":
        return "El nombre de dominio es correcto";
      case "domain.empty":
        return "El nombre de dominio no puede estar vacío";
      case "domain.invalidCharacter":
        return "El caracter '" + parameters[0] + "' en el nombre de dominio '" + parameters[1] + "' no es válido";
      case "domain.missingDot":
        return "El nombre de dominio '" + parameters[0] + "' debe tener un punto";
      case "domain.notExists":
        return "El nombre de dominio '" + parameters[0] + "' no existe";
      case "email.ok":
        return "La dirección de correo es correcta";
      case "email.isEmpty":
        return "Debe especificar una dirección de correo";
      case "email.invalidLocalPart":
        return "La direccion de correo '" + parameters[0] + "' tiene un caracter '" + parameters[1] + "' no válido";
      case "email.arrobaMissing":
        return "Las direcciones de correo deben tener un arroba";
      case "email.domain.ok":
        return "El dominio de la dirección de correo es correcto";
      case "hostname.empty":
        return "El nombre de host no puede estar vacío";
      case "hostname.invalidCharacter":
        return "El caracter '" + parameters[0] + "' en el nombre de host '" + parameters[1] + "' no es válido";
      case "hostname.notExists":
        return "El nombre de host '" + parameters[0] + "' no existe";
      case "hostname.ok":
        return "El nombre de host '" + parameters[0] + "' es correcto";
      case "lastName.ok":
        return "El apellido es correcto";
      case "login.fail":
        return "El usuario o la contraseña son incorrectos";
      case "messages.sent":
        return "El mensaje fué enviado";
      case "name.ok":
        return "El nombre es correcto";
      case "password.ok":
        return "La contraseña tiene la forma correcta";
      case "password.empty":
        return "Debe especificar una contraseña";
      case "password.short":
        return "La contraseña es muy corta";
      case "password.pair.do.not.match":
        return "La contraseña y su verificación no son iguales";
      case "password.pair.empty":
        return "Debe especificar una verificación para la contraseña";
      case "password.recovery.mail.sent":
        return "El correo para recuperar su contraseña ha sido enviado";
      case "password.change.ok":
        return "La contraseña ha sido cambiada";
      case "person.email.in.use":
        return "El correo '" + parameters[0] + "' ya está siendo utilizado por un usuario";
      case "site.host.deleted":
        return "El host fue eliminado";
      case "site.hostname.exist.for.other.site":
        return "Ya existe el nombre de host '" + parameters[0] + "' en el sitio '" + parameters[1] + "'";
      case "site.host.base.can.not.be.deleted":
        return "No se puede eliminar el host base";
      case "site.deleted":
        return "El sitio fue eliminado";
      case "site.name.exist":
        return "Ya existe un sitio con ese nombre";
      case "site.name.ok":
        return "El nombre de sitio está correcto";
      case "site.name.empty":
        return "El nombre del sitio no debe estar vacío";
      case "site.version.empty":
        return "Debe especificarse una version para el sitio";
      case "site.version.invalid":
        return "La version '" + parameters[0] + "' indicada para el sitio no es válida";
      case "site.version.zero":
        return "La version indicada para el sitio no puede ser cero";
      case "site.version.negative":
        return "La version indicada para el sitio no puede ser negativa";
      case "site.version.integer":
        return "La version indicada para el sitio debe ser un número entero";
      case "site.updated":
        return "Datos del sitio actualizados";
      case "site.name.created":
        return "El sitio fué creado";
      case "user.already.added":
        return "El usuario ya ha sido agregado en el sistema";
      case "user.added":
        return "El usuario ha sido agregado";
      case "user.logged":
        return "El usuario está registrado en el sistema";
      case "user.notLogged":
        return "El usuario no ha accedido al sistema";
      default:
        throw new InvalidKeyException("I can't found the text key " + messageKey + " for the locale " + locale);
    }
  }
}
