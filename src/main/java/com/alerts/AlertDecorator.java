package com.alerts;

public abstract class AlertDecorator implements AlertComponent {
    protected AlertComponent decoratedAlert;

    public AlertDecorator(AlertComponent decoratedAlert) {
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public void sendAlert() {
        decoratedAlert.sendAlert();
    }
}
