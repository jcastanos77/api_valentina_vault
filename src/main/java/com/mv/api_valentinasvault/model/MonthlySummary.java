package com.mv.api_valentinasvault.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "monthly_summaries", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "year", "month"})
})
public class MonthlySummary {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int year;
    private int month;

    @Column(name = "total_income", nullable = false)
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(name = "basicos_spent")
    private BigDecimal basicosSpent = BigDecimal.ZERO;

    @Column(name = "ahorro_spent")
    private BigDecimal ahorroSpent = BigDecimal.ZERO;

    @Column(name = "lujos_spent")
    private BigDecimal lujosSpent = BigDecimal.ZERO;

    @Column(name = "automatic_transfer")
    private BigDecimal automaticTransfer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private BigDecimal ahorroDirecto;

    @Column(name = "is_closed")
    private boolean closed = false;


    // Getters y setters

    public MonthlySummary(UUID id, User user, int year, int month, BigDecimal totalIncome, BigDecimal basicosSpent, BigDecimal ahorroSpent, BigDecimal lujosSpent, BigDecimal automaticTransfer, LocalDateTime createdAt, BigDecimal ahorroDirecto) {
        this.id = id;
        this.user = user;
        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.basicosSpent = basicosSpent;
        this.ahorroSpent = ahorroSpent;
        this.lujosSpent = lujosSpent;
        this.automaticTransfer = automaticTransfer;
        this.createdAt = createdAt;
        this.ahorroDirecto = ahorroDirecto;
    }
    public MonthlySummary(){}

    public MonthlySummary(User user, int year, int monthValue) {
        this.user = user;
        this.year = year;
        this.month = monthValue;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getBasicosSpent() {
        return basicosSpent;
    }

    public void setBasicosSpent(BigDecimal basicosSpent) {
        this.basicosSpent = basicosSpent;
    }

    public BigDecimal getAhorroSpent() {
        return ahorroSpent;
    }

    public void setAhorroSpent(BigDecimal ahorroSpent) {
        this.ahorroSpent = ahorroSpent;
    }

    public BigDecimal getLujosSpent() {
        return lujosSpent;
    }

    public void setLujosSpent(BigDecimal lujosSpent) {
        this.lujosSpent = lujosSpent;
    }

    public BigDecimal getAutomaticTransfer() {
        return automaticTransfer;
    }

    public void setAutomaticTransfer(BigDecimal automaticTransfer) {
        this.automaticTransfer = automaticTransfer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getAhorroDirecto() {
        return ahorroDirecto;
    }

    public void setAhorroDirecto(BigDecimal ahorroDirecto) {
        this.ahorroDirecto = ahorroDirecto;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
