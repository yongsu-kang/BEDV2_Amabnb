package com.prgrms.amabnb.reservation.entity;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
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
import javax.persistence.ManyToOne;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.reservation.exception.ReservationInvalidValueException;
import com.prgrms.amabnb.reservation.exception.ReservationStatusException;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    private static final int GUEST_MIN_VALUE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ReservationDate reservationDate;

    private int totalGuest;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Money totalPrice;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User guest;

    @Builder
    public Reservation(
        Long id,
        ReservationDate reservationDate,
        int totalGuest,
        Money totalPrice,
        ReservationStatus reservationStatus,
        Room room,
        User guest
    ) {
        this.id = id;
        setReservationDate(reservationDate);
        setTotalGuest(totalGuest);
        setTotalPrice(totalPrice);
        setRoom(room);
        setGuest(guest);
        setReservationStatus(reservationStatus);
    }

    public Reservation(Long id) {
        this.id = id;
    }

    public void modify(LocalDate checkOut, int totalGuest, Money payment) {
        if (isNotModifiable()) {
            throw new ReservationStatusException();
        }
        this.reservationDate = this.reservationDate.changeCheckOut(checkOut);
        this.totalGuest = totalGuest;
        this.totalPrice = this.totalPrice.add(payment);
    }

    public boolean isNotHost(User user) {
        return !this.room.isHost(user);
    }

    public boolean isNotGuest(User user) {
        return !this.guest.isSame(user);
    }

    public boolean isNotValidatePrice() {
        return !this.room.isValidatePrice(totalPrice, reservationDate.getPeriod());
    }

    public boolean isOverMaxGuest() {
        return this.room.isOverMaxGuestNum(totalGuest);
    }

    public void changeStatus(ReservationStatus status) {
        if (isNotModifiable()) {
            throw new ReservationStatusException();
        }
        this.reservationStatus = status;
    }

    private boolean isNotModifiable() {
        return this.reservationStatus != ReservationStatus.PENDING;
    }

    private void setReservationDate(ReservationDate reservationDate) {
        if (reservationDate == null) {
            throw new ReservationInvalidValueException("예약 날짜는 비어있을 수 없습니다.");
        }
        this.reservationDate = reservationDate;
    }

    private void setTotalGuest(int totalGuest) {
        if (totalGuest < GUEST_MIN_VALUE) {
            throw new ReservationInvalidValueException("숙박 인원는 1미만일 수 없습니다.");
        }
        this.totalGuest = totalGuest;
    }

    private void setTotalPrice(Money totalPrice) {
        if (totalPrice == null) {
            throw new ReservationInvalidValueException("총 가격은 비어있을 수 없습니다.");
        }
        this.totalPrice = totalPrice;
    }

    private void setRoom(Room room) {
        if (room == null) {
            throw new ReservationInvalidValueException("숙소는 비어있을 수 없습니다.");
        }
        this.room = room;
    }

    private void setGuest(User guest) {
        if (guest == null) {
            throw new ReservationInvalidValueException("게스트는 비어있을 수 없습니다.");
        }
        this.guest = guest;
    }

    private void setReservationStatus(ReservationStatus reservationStatus) {
        if (reservationStatus == null) {
            throw new ReservationInvalidValueException("예약 상태는 비어있을 수 없습니다.");
        }
        this.reservationStatus = reservationStatus;
    }
}
