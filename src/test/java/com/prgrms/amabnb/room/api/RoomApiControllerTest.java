package com.prgrms.amabnb.room.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.service.CreateRoomService;
import com.prgrms.amabnb.room.service.SearchRoomService;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.entity.vo.Email;
import com.prgrms.amabnb.user.entity.vo.PhoneNumber;
import com.prgrms.amabnb.user.repository.UserRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class RoomApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CreateRoomService createRoomService;

    @Autowired
    SearchRoomService searchRoomService;

    @Autowired
    UserRepository userRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    @DisplayName("숙소 등록 성공 테스트")
    void createRoom() throws Exception {
        User savedUser = userRepository.save(createUser());
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        createRoomRequest.setUserId(savedUser.getId());

        mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document("room-create",
                requestFields(
                    fieldWithPath("userId").type(JsonFieldType.NUMBER).description("userId"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("roomName"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("price"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("description"),
                    fieldWithPath("maxGuestNum").type(JsonFieldType.NUMBER).description("maxGuestNum"),
                    fieldWithPath("zipcode").type(JsonFieldType.STRING).description("zipcode"),
                    fieldWithPath("address").type(JsonFieldType.STRING).description("address"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("detailAddress"),
                    fieldWithPath("bedCnt").type(JsonFieldType.NUMBER).description("bedCnt"),
                    fieldWithPath("bedRoomCnt").type(JsonFieldType.NUMBER).description("bedRoomCnt"),
                    fieldWithPath("bathRoomCnt").type(JsonFieldType.NUMBER).description("bathRoomCnt"),
                    fieldWithPath("roomType").type(JsonFieldType.STRING).description("roomType"),
                    fieldWithPath("roomScope").type(JsonFieldType.STRING).description("roomScope"),
                    fieldWithPath("imagePaths").type(JsonFieldType.ARRAY).description("roomImagePath")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("숙소는 userId가 없으면 등록되지 않는다.")
    void nullUserIdTest() throws Exception {
        //given
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        createRoomRequest.setUserId(null);

        //when,then
        mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("숙소는 등록된 유저가 아니면 등록되지 않는다.")
    void notSavedUserRoomRegisterTest() throws Exception {
        //given
        Long notSavedUserId = 17842319782341L;
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        createRoomRequest.setUserId(notSavedUserId);

        //when,then
        mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoomRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("필터 검색을 할 수 있다.")
    void name() throws Exception {
        //given
        User savedUser = userRepository.save(createUser());
        CreateRoomRequest createRoomRequest = createCreateRoomRequest();
        createRoomRequest.setUserId(savedUser.getId());

        // when, then
        // mockMvc.perform(get("/rooms")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .param("minBeds", "1")
        //         .param("minBedrooms", "1")
        //         .param("minBathrooms", "1")
        //         .param("minPrice", "1")
        //         .param("maxPrice", "10000000")
        //         .param("roomTypes", "HOUSE")
        //         .param("roomScopes", "PRIVATE")
        //         .content(objectMapper.writeValueAsString(new PageRequestDto(2, 10))))
        //     .andExpect(status().isOk());

    }

    private User createUser() {
        return User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();
    }

    private PageRequest createPageRequest() {
        return PageRequest.of(0, 10);
    }

    private CreateRoomRequest createCreateRoomRequest() {
        return CreateRoomRequest.builder()
            .name("방이름")
            .price(1)
            .description("방설명")
            .maxGuestNum(1)
            .zipcode("00000")
            .address("창원")
            .detailAddress("의창구")
            .bedCnt(2)
            .bedRoomCnt(1)
            .bathRoomCnt(1)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .imagePaths(List.of("aaa", "bbb"))
            .build();
    }

    private SearchRoomFilterCondition createFilterCondition() {
        return SearchRoomFilterCondition.builder()
            .minBeds(0)
            .minBedrooms(0)
            .minBathrooms(0)
            .minPrice(100)
            .maxPrice(100000000)
            .roomScopes(List.of("PRIVATE"))
            .roomTypes(List.of("HOUSE"))
            .build();
    }

}
