package net.cabezudo.sofia.core.mail;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.JSONable;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.json.values.JSONValue;
import net.cabezudo.sofia.emails.EMail;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.10.11
 */
public class Message implements JSONable {

  private final String fromName;
  private final EMail from;
  private final String toName;
  private final EMail to;
  private final String subject;
  private final String text;
  private final String html;

  public Message(String fromName, EMail from, String toName, EMail to, String subject, String text, String html) {
    if (from == null) {
      throw new RuntimeException("No se encontró la dirección de envío.");
    }
    this.fromName = fromName;
    this.from = from;
    this.toName = toName;
    this.to = to;
    this.subject = subject;
    this.text = text;
    this.html = html;
  }

  @Override
  public String toJSON() {
    return toJSONTree().toString();
  }

  @Override
  public JSONValue toJSONTree() {
    JSONObject jsonMessage = new JSONObject();

    JSONObject jsonFromObject = new JSONObject();
    JSONPair jsonFromEMailPair = new JSONPair("Email", from.getAddress());
    jsonFromObject.add(jsonFromEMailPair);
    JSONPair jsonFromNamePair = new JSONPair("Name", fromName);
    jsonFromObject.add(jsonFromNamePair);
    JSONPair jsonFromPair = new JSONPair("From", jsonFromObject);
    jsonMessage.add(jsonFromPair);

    JSONArray jsonToArray = new JSONArray();
    JSONObject jsonToObject = new JSONObject();
    JSONPair jsonToEMailPair = new JSONPair("Email", to.getAddress());
    jsonToObject.add(jsonToEMailPair);
    JSONPair jsonToNamePair = new JSONPair("Name", toName);
    jsonToObject.add(jsonToNamePair);
    jsonToArray.add(jsonToObject);
    JSONPair jsonToPair = new JSONPair("To", jsonToArray);
    jsonMessage.add(jsonToPair);

    JSONPair jsonSubjectPair = new JSONPair("Subject", subject);
    jsonMessage.add(jsonSubjectPair);

    JSONPair jsonTextPartPair = new JSONPair("TextPart", text);
    jsonMessage.add(jsonTextPartPair);

    JSONPair jsonHTMLPartPair = new JSONPair("HTMLPart", html);
    jsonMessage.add(jsonHTMLPartPair);

    return jsonMessage;
  }
  /*      {
                 "Messages":[
                    {
                       "From":{
                          "Email":"esteban@cabezudo.net",
                          "Name":"Mailjet Pilot"
                       },
                       "To\":[
                          {
                             "Email":"esteban.cabezudo@gmail.com",
                             "Name":"passenger 1"
                          }
                       ],
                       "Subject":"Your email flight plan!",
                       "TextPart":"Dear passenger 1, welcome to Mailjet! May the delivery force be with you!",
                       "HTMLPart":"<h3>Dear passenger 1, welcome to Mailjet!</h3><br />May the delivery force be with you!"
                    }
                 ]
              }


  [ { "From": { "Email": "esteban@cabezudo.net", "Name": "No responder" }, "To": { "Email": "esteban.cabezudo@gmail.com", "Name": "Esteban Cabezudo" }, "Subject": "subject", "TextPart": "Plain text", "HTMLPart": "html text" } ]
  [ { "From": { "Email": "esteban@cabezudo.net",
   "Name\":"Mailjet Pilot"
},
"To\":[
   {
      "Email":"esteban.cabezudo@gmail.com",
      "Name":"passenger 1"
   }
],
"Subject":"Your email flight plan!",
"TextPart":"Dear passenger 1, welcome to Mailjet! May the delivery force be with you!",
"HTMLPart":"<h3>Dear passenger 1, welcome to Mailjet!</h3><br />May the delivery force be with you!"
}
]

   */

}
