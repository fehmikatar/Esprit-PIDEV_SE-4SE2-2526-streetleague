package tn.esprit._4se2.pi.dto.Athlet;

public class ChatResponse {
    private String reply;
    private Double healthScore;

    // 👇 Add this default constructor
    public ChatResponse() {
    }

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public ChatResponse(String reply, Double healthScore) {
        this.reply = reply;
        this.healthScore = healthScore;
    }

    // getters and setters...
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public Double getHealthScore() { return healthScore; }
    public void setHealthScore(Double healthScore) { this.healthScore = healthScore; }
}