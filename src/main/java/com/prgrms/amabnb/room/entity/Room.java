package com.prgrms.amabnb.room.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.room.exception.RoomInvalidValueException;
import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    private static final int MAX_NAME_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Money price;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int maxGuestNum;

    @Embedded
    private RoomAddress address;

    @Embedded
    private RoomOption roomOption;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomScope roomScope;

    @ManyToOne(fetch = FetchType.LAZY)
    private User host;

    @OneToMany
    @JoinColumn(name = "review_id")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RoomImage> roomImages = new ArrayList<>();

    @Builder
    public Room(Long id, String name, Money price, String description, int maxGuestNum,
        RoomAddress address, RoomOption roomOption, RoomType roomType, RoomScope roomScope,
        User host, List<RoomImage> roomImages) {
        validateRoom(name, price, maxGuestNum, description, address, roomOption, roomType, roomScope);
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.maxGuestNum = maxGuestNum;
        this.address = address;
        this.roomOption = roomOption;
        this.roomType = roomType;
        this.roomScope = roomScope;
        this.host = host;
        addRoomImages(roomImages);
    }

    public void addRoomImages(List<RoomImage> roomImages) {
        roomImages.forEach(this::addRoomImage);
    }

    public void addRoomImage(RoomImage roomImage) {
        if (roomImage.getRoom() != this) {
            roomImage.setRoom(this);
        }

        this.getRoomImages().add(roomImage);
    }

    public boolean isValidatePrice(Money totalPrice, int period) {
        return totalPrice.equals(price.multiply(period));
    }

    public boolean isOverMaxGuestNum(int totalGuest) {
        return totalGuest > maxGuestNum;
    }

    private void validateRoom(String name, Money price, int maxGuestNum, String description, RoomAddress roomAddress,
        RoomOption roomOption, RoomType roomType, RoomScope roomScope) {
        validateName(name);
        validateMaxGuestNum(maxGuestNum);
        validateDescription(description);
        isPresentPrice(price);
        isPresentRoomAddress(roomAddress);
        isPresentRoomOption(roomOption);
        isPresentRoomType(roomType);
        isPresentRoomScope(roomScope);
    }

    public boolean isHost(User user) {
        return this.host.isSame(user);
    }

    private void validateName(String name) {
        if (Objects.isNull(name) || name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            throw new RoomInvalidValueException("숙소 이름 입력값이 잘못됐습니다");
        }
    }

    private void validateMaxGuestNum(int maxGuestNum) {
        if (maxGuestNum < 1) {
            throw new RoomInvalidValueException("최대 인원 수 입력값이 잘못됐습니다");
        }
    }

    private void validateDescription(String description) {
        if (Objects.isNull(description) || description.isBlank()) {
            throw new RoomInvalidValueException("숙소 정보 입력값이 잘못됐습니다");
        }
    }

    private void isPresentRoomOption(RoomOption roomOption) {
        if (Objects.isNull(roomOption)) {
            throw new RoomInvalidValueException("숙소 옵션을 입력하지 않았습니다.");
        }
    }

    private void isPresentRoomAddress(RoomAddress roomAddress) {
        if (Objects.isNull(roomAddress)) {
            throw new RoomInvalidValueException("숙소 주소를 입력하지 않았습니다");
        }
    }

    private void isPresentPrice(Money price) {
        if (Objects.isNull(price)) {
            throw new RoomInvalidValueException("가격을 입력하지 않았습니다");
        }
    }

    private void isPresentRoomType(RoomType roomType) {
        if (Objects.isNull(roomType)) {
            throw new RoomInvalidValueException("숙소 유형이 정해지지 않았습니다");
        }
    }

    private void isPresentRoomScope(RoomScope roomScope) {
        if (Objects.isNull(roomScope)) {
            throw new RoomInvalidValueException("숙소 이용 범위가 정해지지 않았습니다");
        }
    }

    public void changeName(String name) {
        if (!this.name.equals(name)) {
            validateName(name);
            this.name = name;
        }
    }

    public void changePrice(Money price) {
        if (!this.price.equals(price)) {
            this.price = price;
        }
    }

    public void changeDescription(String description) {
        if (!this.description.equals(description)) {
            validateDescription(description);
            this.description = description;
        }
    }

    public void changeMaxGuestNum(int maxGuestNum) {
        if (this.maxGuestNum != maxGuestNum) {
            validateMaxGuestNum(maxGuestNum);
            this.maxGuestNum = maxGuestNum;
        }
    }

    public void changeRoomOption(RoomOption roomOption) {
        if (!this.roomOption.equals(roomOption)) {
            this.roomOption = roomOption;
        }
    }
}
