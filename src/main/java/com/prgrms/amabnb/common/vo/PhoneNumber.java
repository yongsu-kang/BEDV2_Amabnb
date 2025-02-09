package com.prgrms.amabnb.common.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.prgrms.amabnb.user.exception.UserInvalidValueException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber {

    private static final String PHONE_NUMBER_REGEX = "01\\d{1}-\\d{3,4}-\\d{4}";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Column(name = "phone_number", unique = true, length = 20)
    private String number;

    public PhoneNumber(String number) {
        validatePhoneNumber(number);
        this.number = number;
    }

    private void validatePhoneNumber(String number) {
        validateBlank(number);
        validateFormat(number);
    }

    private void validateBlank(String number) {
        if (Objects.isNull(number) || number.isBlank()) {
            throw new UserInvalidValueException("휴대폰 번호는 비어있을 수 없습니다.");
        }
    }

    private void validateFormat(String number) {
        if (!PHONE_NUMBER_PATTERN.matcher(number).matches()) {
            throw new UserInvalidValueException("휴대폰 번호 포맷을 만족해야 합니다. 현재 휴대폰 번호 : %s".formatted(number));
        }
    }

}
