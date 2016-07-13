/*
 * Amadeus Confidential Information:
 * Unauthorized use and disclosure strictly forbidden.
 * @1998-2015 - Amadeus s.a.s - All Rights Reserved.
 */
package com.amadeus.fun;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author bmallick
 */
public class MailerService {

  // Initialize log4j
  public static final Logger logger = Logger.getLogger(MailerService.class);
  ConfigReader cfg = new ConfigReader();

  public static void main(String v[]) {
    logger
        .info("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    ExcelReader exr = new ExcelReader();
    HashMap<Double, String> birthdayBabies = exr.getBirthdayBabies();
    Iterator<Entry<Double, String>> it = birthdayBabies.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<Double, String> pairs = it.next();
      String[] babyInfo = pairs.getValue().split(":");

      logger.info("ID: " + Integer.toString(pairs.getKey().intValue()));
      logger.info("Name: " + babyInfo[0]);
      logger.info("Email: " + babyInfo[1]);
      logger.info("Picture: " + babyInfo[2]);
      logger.info("Team: " + babyInfo[3]);

      // Send mail to birthday babies
      if (v.length == 1 && v[0] != null && "sendmail".equals(v[0])) {
        new MailerService().sendMail(babyInfo[0], babyInfo[1], Integer.toString(pairs.getKey().intValue()) + ".jpg",
            babyInfo[2] + ".jpg", babyInfo[3]);
      }
    }
    try {
      it.remove(); // Avoids a ConcurrentModificationException
    }
    catch (Exception e) {
      logger.info("No birthday babies found for today!");
    }
  }

  public void sendMail(String name, String to, String babyImageId, String imageName, String teamName) {
    ResizeImage resize = new ResizeImage();
    String imagePath = cfg.getPropValue("IMAGE_PATH") + "\\";

    // Sender's email ID needs to be mentioned
    String from = cfg.getPropValue("FROM_ADDRESS");
    String cc = cfg.getPropValue("CC_ADDRESS");

    // Set email host
    String host = cfg.getPropValue("SMTP_HOST");

    Properties props = new Properties();
    props.put("mail.smtp.starttls.enable", cfg.getPropValue("SMTP_STARTTLS_ENABLE"));
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", cfg.getPropValue("SMTP_PORT"));

    String messageHtml = "<table border = \"1\" bgcolor=\"FBEFEF\"><tr><td colspan = 2><img src=\"cid:header\" /></td></tr><tr><td><center><img src=\"cid:image\" /><br><br><b>" +
        name +
        " (" +
        teamName +
        ")" +
        "</b><br><br>Best Wishes, <br><b>APT Fun Committee</b></center></td><td><center><img src=\"cid:pic\" style=\"width:100%\"/></center></td></tr></table><br><br>Please DONOT reply to this email id: apt.funcommittee@amadeus.com";

    Session session = Session.getDefaultInstance(props);

    try {
      // Create a default MimeMessage object.
      Message message = new MimeMessage(session);

      // Set From: header field of the header.
      message.setFrom(new InternetAddress(from));

      // Set To: header field of the header.
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

      // Set Subject: header field
      message.setSubject("Happy Birthday " + name);

      // This mail has 2 parts, the BODY and the embedded images
      MimeMultipart multipart = new MimeMultipart("related");

      // First part (the html)
      BodyPart messageBodyPart = new MimeBodyPart();
      String htmlText = messageHtml;
      messageBodyPart.setContent(htmlText, "text/html");

      // Add it
      multipart.addBodyPart(messageBodyPart);

      // Birthday header image
      messageBodyPart = new MimeBodyPart();
      DataSource ds1 = new FileDataSource("./default-images/default-birthday-header.jpg");
      String bdayHeaderImage = imagePath + "birthday-header.jpg";
      if (new File(bdayHeaderImage).exists()) {
        resize.resizeImage(bdayHeaderImage, 996, 295);
        ds1 = new FileDataSource(bdayHeaderImage);
      }

      messageBodyPart.setDataHandler(new DataHandler(ds1));
      messageBodyPart.setHeader("Content-ID", "header");

      // Add image to the multipart
      multipart.addBodyPart(messageBodyPart);

      // Birthday baby image
      messageBodyPart = new MimeBodyPart();
      DataSource ds2 = new FileDataSource("./default-images/default-baby.jpg");
      String babyImage = imagePath + babyImageId;
      if (new File(babyImage).exists()) {
        resize.resizeImage(babyImage, 245, 355);
        ds2 = new FileDataSource(babyImage);
      }

      messageBodyPart.setDataHandler(new DataHandler(ds2));
      messageBodyPart.setHeader("Content-ID", "image");

      // Add image to the multipart
      multipart.addBodyPart(messageBodyPart);

      // Dynamic birthday wish image
      messageBodyPart = new MimeBodyPart();
      DataSource ds3 = new FileDataSource("./default-images/default-picture.jpg");
      String bdayImage = imagePath + imageName;
      if (new File(bdayImage).exists()) {
        resize.resizeImage(bdayImage, 764, 422);
        ds3 = new FileDataSource(bdayImage);
      }

      messageBodyPart.setDataHandler(new DataHandler(ds3));
      messageBodyPart.setHeader("Content-ID", "pic");

      // Add image to the multipart
      multipart.addBodyPart(messageBodyPart);

      // Put everything together
      message.setContent(multipart);
      // Send message
      Transport.send(message);

      logger.info("Sent message successfully...");

    }
    catch (MessagingException e) {
      logger.error("Error sending mail! ERROR : " + e.getMessage());
    }
  }
}
