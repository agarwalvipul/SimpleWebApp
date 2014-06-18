package com.simplewebapp.service;

import com.simplewebapp.model.User;

public interface MailSenderService {

    public void sendActivationMailInformation(User user, String url);
}