package com.prgrms.amabnb.room.entity.dto.request;

import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class CreateRoomRequest {

    private Long userId;

    @Max(value = 10000000)
    @PositiveOrZero
    private int price;

    @NotBlank
    private String description;

    @Positive
    private int maxGuestNum;

    @Pattern(regexp = "^\\d{5}$")
    private String zipcode;

    @NotBlank
    private String address;

    private String detailAddress;

    @PositiveOrZero
    private int bedCnt;

    @PositiveOrZero
    private int bedRoomCnt;

    @PositiveOrZero
    private int bathRoomCnt;

    @NotNull
    private RoomType roomType;

    @NotNull
    private RoomScope roomScope;

    @Builder
    public CreateRoomRequest(int price, String description, int maxGuestNum, String zipcode, String address,
                             String detailAddress, int bedCnt, int bedRoomCnt, int bathRoomCnt, RoomType roomType,
                             RoomScope roomScope) {
        this.price = price;
        this.description = description;
        this.maxGuestNum = maxGuestNum;
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.bedCnt = bedCnt;
        this.bedRoomCnt = bedRoomCnt;
        this.bathRoomCnt = bathRoomCnt;
        this.roomType = roomType;
        this.roomScope = roomScope;
    }

    public static Room toRoom(CreateRoomRequest request) {
        return Room.builder()
                .price(new Money(request.getPrice()))
                .description(request.getDescription())
                .maxGuestNum(request.getMaxGuestNum())
                .address(new RoomAddress(request.getZipcode(), request.getAddress(), request.getDetailAddress()))
                .roomOption(new RoomOption(request.getBedCnt(), request.getBedRoomCnt(), request.getBathRoomCnt()))
                .roomType(request.getRoomType())
                .roomScope(request.getRoomScope())
                .build();
    }
}