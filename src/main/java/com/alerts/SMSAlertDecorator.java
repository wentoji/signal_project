package com.alerts;
public class SMSAlertDecorator extends AlertDecorator {
    public SMSAlertDecorator(AlertComponent decoratedAlert) {
        super(decoratedAlert);
    }

    @Override
    public void sendAlert() {
        super.sendAlert();
        sendSMSAlert();
    }

    private void sendSMSAlert() {
        System.out.println("Sending SMS alert...");
    }
}
