package com.alerts;

public class EmailAlertDecorator extends AlertDecorator {
    public EmailAlertDecorator(AlertComponent decoratedAlert) {
        super(decoratedAlert);
    }

    @Override
    public void sendAlert() {
        super.sendAlert();
        sendEmailAlert();
    }

    private void sendEmailAlert() {
        System.out.println("Sending email alert...");
    }
}
