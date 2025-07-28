package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "from_card_id")
    @NotNull(message = "Card from cannot not null")
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id")
    @NotNull(message = "Card to cannot not null")
    private Card toCard;

    @Column(name = "amount", nullable = false)
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private TransferStatus status = TransferStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;
}