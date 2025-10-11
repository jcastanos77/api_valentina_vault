package com.mv.api_valentinasvault.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;


    @Column(nullable = false)
    private String fullName;

    private Integer basicosPercent;

    private Integer ahorroPercent;

    private Integer lujosPercent;

    public Integer getBasicosPercent() {
        return basicosPercent;
    }

    public void setBasicosPercent(Integer basicosPercent) {
        this.basicosPercent = basicosPercent;
    }

    public Integer getAhorroPercent() {
        return ahorroPercent;
    }

    public void setAhorroPercent(Integer ahorroPercent) {
        this.ahorroPercent = ahorroPercent;
    }

    public Integer getLujosPercent() {
        return lujosPercent;
    }

    public void setLujosPercent(Integer lujosPercent) {
        this.lujosPercent = lujosPercent;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setId(UUID id) { this.id = id; }

    public void setEmail(String email) { this.email = email; }

    public void setFullName(String fullName) { this.fullName = fullName; }
}
