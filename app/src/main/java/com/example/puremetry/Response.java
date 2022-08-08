package com.example.puremetry;

public class Response {

    private String question;
    private String message;

    public Response(String question, String message) {
        this.question = question;
        this.message = message;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
