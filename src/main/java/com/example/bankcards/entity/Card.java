package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.CardType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number", nullable = false)
    private String cardNumber;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @NotNull
    private CardStatus cardStatus;

    @Column(name = "expiration", nullable = false)
    @NotNull
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    @NotNull
    private CardType cardType;

    @Column(name = "deleted", nullable = false)
    @NotNull
    private boolean isDeleted;

    @Column(name = "balance", nullable = false)
    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal balance = BigDecimal.valueOf(0.00);
}