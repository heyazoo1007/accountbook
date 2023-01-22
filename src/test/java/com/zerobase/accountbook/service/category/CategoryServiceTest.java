package com.zerobase.accountbook.service.category;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.category.dto.request.CreateCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.request.DeleteCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.request.ModifyCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.response.CreateCategoryResponseDto;
import com.zerobase.accountbook.controller.category.dto.response.GetCategoryListResponseDto;
import com.zerobase.accountbook.controller.category.dto.response.ModifyCategoryResponseDto;
import com.zerobase.accountbook.domain.category.Category;
import com.zerobase.accountbook.domain.category.CategoryRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void success_createCategory() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(categoryRepository.existsByCategoryName(anyString()))
                .willReturn(false);

        Category category = Category.builder()
                .id(1L)
                .member(member)
                .categoryName("category1")
                .build();
        given(categoryRepository.save(any())).willReturn(category);

        CreateCategoryRequestDto requestDto = CreateCategoryRequestDto.builder()
                .categoryName("category1")
                .build();

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        //when
        CreateCategoryResponseDto responseDto =
                categoryService.createCategory(requestEmail, requestDto);

        //then
        verify(categoryRepository, times(1))
                .save(captor.capture());
        assertEquals(captor.getValue().getCategoryName(), responseDto.getCategoryName());
    }

    @Test
    void fail_createCategory_존재하지_않는_회원() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        CreateCategoryRequestDto requestDto = CreateCategoryRequestDto.builder()
                .categoryName("category1")
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.createCategory(requestEmail, requestDto));
    }

    @Test
    void fail_createCategory_카테고리명_중복() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(categoryRepository.existsByCategoryName(anyString()))
                .willReturn(true);

        CreateCategoryRequestDto requestDto = CreateCategoryRequestDto.builder()
                .categoryName("category1")
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.createCategory(requestEmail, requestDto));
    }

    @Test
    void success_modifyCategory() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email(requestEmail).build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Category category = Category.builder()
                .id(1L)
                .member(member)
                .categoryName("category1")
                .build();
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));

        given(categoryRepository.existsByCategoryName(anyString()))
                .willReturn(false);

        ModifyCategoryRequestDto requestDto = ModifyCategoryRequestDto.builder()
                .categoryId(1L)
                .categoryName("modifyCategoryName")
                .build();

        ArgumentCaptor<Category> captor =
                ArgumentCaptor.forClass(Category.class);

        //when
        ModifyCategoryResponseDto responseDto =
                categoryService.modifyCategory(requestEmail, requestDto);

        //then
        verify(categoryRepository, times(1))
                .save(captor.capture());
        assertEquals(
                captor.getValue().getCategoryName(),
                responseDto.getCategoryName()
        );
    }

    @Test
    void fail_modifyCategory_존재하지_않는_회원() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        ModifyCategoryRequestDto requestDto = ModifyCategoryRequestDto.builder()
                .categoryId(1L)
                .categoryName("modifyCategoryName")
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.modifyCategory(requestEmail, requestDto));
    }

    @Test
    void fail_modifyCategory_존재하지_않는_카테고리() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email(requestEmail).build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        ModifyCategoryRequestDto requestDto = ModifyCategoryRequestDto.builder()
                .categoryId(1L)
                .categoryName("modifyCategoryName")
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.modifyCategory(requestEmail, requestDto));
    }

    @Test
    void fail_modifyCategory_카테고리_주인이_아님() {
        //given

        String notOwnerEmail = "notOwner@abc.com";
        Member notOwner = Member.builder().id(2L).email(notOwnerEmail).build();
        Member owner = Member.builder().id(1L).email("owner@abc.com").build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));

        Category category = Category.builder()
                .id(1L)
                .member(owner)
                .categoryName("category1")
                .build();
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));

        ModifyCategoryRequestDto requestDto = ModifyCategoryRequestDto.builder()
                .categoryId(1L)
                .categoryName("modifyCategoryName")
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.modifyCategory(notOwnerEmail, requestDto));
    }

    @Test
    void fail_modifyCategory_카테고리명_중복() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email(requestEmail).build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Category category = Category.builder()
                .id(1L)
                .member(member)
                .categoryName("category1")
                .build();
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));

        given(categoryRepository.existsByCategoryName(anyString()))
                .willReturn(true);

        ModifyCategoryRequestDto requestDto = ModifyCategoryRequestDto.builder()
                .categoryId(1L)
                .categoryName("modifyCategoryName")
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.modifyCategory(requestEmail, requestDto));
    }

    @Test
    void success_deleteCategory() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email(requestEmail).build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Category category = Category.builder()
                .id(1L)
                .member(member)
                .categoryName("category1")
                .build();
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));

        DeleteCategoryRequestDto requestDto = DeleteCategoryRequestDto.builder()
                .categoryId(1L)
                .build();

        //when
        categoryService.deleteCategory(requestEmail, requestDto);

        //then
        verify(categoryRepository, times(1))
                .deleteById(requestDto.getCategoryId());
    }

    @Test
    void fail_deleteCategory_존재하지_않는_회원() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        DeleteCategoryRequestDto requestDto = DeleteCategoryRequestDto.builder()
                .categoryId(1L)
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.deleteCategory(requestEmail, requestDto));
    }

    @Test
    void fail_deleteCategory_존재하지_않는_카테고리() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email(requestEmail).build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        DeleteCategoryRequestDto requestDto = DeleteCategoryRequestDto.builder()
                .categoryId(1L)
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.deleteCategory(requestEmail, requestDto));
    }

    @Test
    void fail_deleteCategory_카테고리_주인이_아님() {
        //given

        String notOwnerEmail = "notOwner@abc.com";
        Member notOwner = Member.builder().id(2L).email(notOwnerEmail).build();
        Member owner = Member.builder().id(1L).email("owner@abc.com").build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));

        Category category = Category.builder()
                .id(1L)
                .member(owner)
                .categoryName("category1")
                .build();
        given(categoryRepository.findById(anyLong()))
                .willReturn(Optional.of(category));

        DeleteCategoryRequestDto requestDto = DeleteCategoryRequestDto.builder()
                .categoryId(1L)
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.deleteCategory(notOwnerEmail, requestDto));
    }

    @Test
    void success_getCategoryList() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email(requestEmail).build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        Category category1 = Category.builder()
                .id(1L)
                .member(member)
                .categoryName("category1")
                .build();
        Category category2 = Category.builder()
                .id(2L)
                .member(member)
                .categoryName("category2")
                .build();
        List<Category> list = new ArrayList<>();
        list.add(category1);
        list.add(category2);
        given(categoryRepository.findAllByMemberId(anyLong()))
                .willReturn(list);

        //when
        List<GetCategoryListResponseDto> responseDtos =
                categoryService.getCategoryList(requestEmail);

        //then
        assertEquals(2, responseDtos.size());
    }

    @Test
    void fail_getCategoryList_존재하지_않는_회원() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());


        //when

        //then
        assertThrows(AccountBookException.class,
                () -> categoryService.getCategoryList(requestEmail));
    }
}