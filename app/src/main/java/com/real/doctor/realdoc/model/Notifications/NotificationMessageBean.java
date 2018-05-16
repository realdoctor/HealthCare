package com.real.doctor.realdoc.model.Notifications;

public class NotificationMessageBean extends NotificationBean {
    private String question;
    private String answer;

    public NotificationMessageBean(){
    }

    public NotificationMessageBean(String question, String answer){
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
