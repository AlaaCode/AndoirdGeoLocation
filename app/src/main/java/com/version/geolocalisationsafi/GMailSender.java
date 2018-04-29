package com.version.geolocalisationsafi;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.os.AsyncTask;
import android.util.Log;


public class GMailSender  {
    Session session = null;
    String rec, subject, textMessage;

    public GMailSender(String rec, String subject,String textMessage){
        this.rec = rec;
        this.subject = subject;
        this.textMessage = textMessage;
    }

    public void Send(){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ensas.geo@gmail.com", "ensas123");
            }
        });

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();
    }

    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("ensas.geo@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rec));
                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");
                Transport.send(message);
                Log.w("GMAIL API" , "no error");
            } catch(MessagingException e) {
                e.printStackTrace();
                Log.w("GMAIL API ERROR" , e.getMessage());
            } catch(Exception e) {
                e.printStackTrace();
                Log.w("GMAIL API ERROR" , e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}