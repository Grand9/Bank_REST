package com.example.bankcards.service.card;

import com.example.bankcards.entity.enums.CardType;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.bankcards.entity.enums.CardType.DIGIT_IN_ONE_SECTION;

@Component
@Slf4j
public class CardNumberGenerate {

        private static final int OWNER_ID_LENGTH = 6;

        private static final int SEQUENCE_LENGTH = 3;

        private static final int MAX_SEQUENCE = 999;

    public static String generateCardMask(String cardNumber) {
        int maskLength = cardNumber.length() - DIGIT_IN_ONE_SECTION;

        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < maskLength; i++) {
            mask.append("*");
            if ((i + 1) % DIGIT_IN_ONE_SECTION == 0 && i != maskLength - 1) {
                mask.append(" ");
            }
        }

        String visibleNumber = cardNumber.substring(maskLength);

        return mask.append(visibleNumber).toString();
    }

        public String generate(CardType type, @NotNull Long ownerId, @NotNull String lastNumber) {
            if (type == null) {
                throw new IllegalArgumentException("CardType must not be null");
            }
            if (ownerId == null) {
                throw new IllegalArgumentException("OwnerId must not be null");
            }
            if (lastNumber == null) {
                throw new IllegalArgumentException("LastNumber must not be null");
            }

            String prefix = type.getPrefix();
            int totalLength = type.getLength();

            String base = buildBaseNumber(prefix, ownerId, lastNumber, totalLength);

            return base + calculateLuhnCheckDigit(base);
        }

        private static String buildBaseNumber(String prefix, Long ownerId, String lastNumber, int totalLength) {
            String ownerIdPart = formatOwnerId(ownerId);
            int sequence = 0;

            if (lastNumber != null && lastNumber.startsWith(prefix)) {
                sequence = extractSequence(lastNumber, prefix, ownerIdPart) + 1;
            }

            if (sequence > MAX_SEQUENCE) {
                throw new IllegalStateException("Max card sequence reached for owner: " + ownerId);
            }

            String sequencePart = String.format("%0" + SEQUENCE_LENGTH + "d", sequence);
            String base = prefix + ownerIdPart + sequencePart;

            validateLength(base, totalLength);
            return base;
        }

        private static String formatOwnerId(Long ownerId) {
            String idString = ownerId.toString();
            String trimmed = idString.substring(Math.max(0, idString.length() - OWNER_ID_LENGTH));
            return String.format("%0" + OWNER_ID_LENGTH + "d", Long.parseLong(trimmed));
        }

        private static int extractSequence(String lastNumber, String prefix, String ownerIdPart) {
            try {
                int ownerIdStart = prefix.length();
                int ownerIdEnd = ownerIdStart + OWNER_ID_LENGTH;

                if (!lastNumber.substring(ownerIdStart, ownerIdEnd).equals(ownerIdPart)) {
                    return 0;
                }

                String sequencePart = lastNumber.substring(ownerIdEnd, ownerIdEnd + SEQUENCE_LENGTH);
                return Integer.parseInt(sequencePart);
            } catch (Exception e) {
                log.warn("Error extracting sequence from: {}", lastNumber);
                return 0;
            }
        }

        private static void validateLength(String base, int totalLength) {
            if (base.length() != totalLength - 1) {
                throw new IllegalStateException(
                        "Invalid base length: " + base.length() +
                                " for required total: " + totalLength
                );
            }
        }

        public static int calculateLuhnCheckDigit(String numberWithoutCheckDigit) {
            int sum = 0;
            boolean alternate = true;

            for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
                int n = Character.getNumericValue(numberWithoutCheckDigit.charAt(i));
                if (alternate) {
                    n *= 2;
                    if (n > 9) n -= 9;
                }
                sum += n;
                alternate = !alternate;
            }

            return (10 - (sum % 10)) % 10;
        }
    }