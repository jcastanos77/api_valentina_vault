package com.mv.api_valentinasvault.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_rules")
public class UserRule {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private int basicosPercent;
    private int ahorroPercent;
    private int lujosPercent;

    public UserRule() {}

    public UserRule(User user, int basicosPercent, int ahorroPercent, int lujosPercent) {
        this.user = user;
        this.basicosPercent = basicosPercent;
        this.ahorroPercent = ahorroPercent;
        this.lujosPercent = lujosPercent;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getBasicosPercent() {
        return basicosPercent;
    }

    public void setBasicosPercent(int basicosPercent) {
        this.basicosPercent = basicosPercent;
    }

    public int getAhorroPercent() {
        return ahorroPercent;
    }

    public void setAhorroPercent(int ahorroPercent) {
        this.ahorroPercent = ahorroPercent;
    }

    public int getLujosPercent() {
        return lujosPercent;
    }

    public void setLujosPercent(int lujosPercent) {
        this.lujosPercent = lujosPercent;
    }
}
