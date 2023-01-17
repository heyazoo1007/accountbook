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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final MemberRepository memberRepository;

    public CreateCategoryResponseDto createCategory(
            CreateCategoryRequestDto request
    ) {

        Member member = validateMember(request.getMemberEmail());

        validateUniqueCategoryName(request.getCategoryName());

        return CreateCategoryResponseDto.of(categoryRepository.save(Category.builder()
                .member(member)
                .categoryName(request.getCategoryName())
                .createdAt(LocalDateTime.now())
                .build()));
    }

    public ModifyCategoryResponseDto modifyCategory(
            ModifyCategoryRequestDto request
    ) {

        Member member = validateMember(request.getMemberEmail());

        Category category = validateCategory(request.getCategoryId());

        requestMemberMismatchCategoryOwner(member, category);

        validateUniqueCategoryName(request.getCategoryName());

        category.setCategoryName(request.getCategoryName());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        return ModifyCategoryResponseDto.of(category);
    }

    public void deleteCategory(DeleteCategoryRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        Category category = validateCategory(request.getCategoryId());

        requestMemberMismatchCategoryOwner(member, category);

        categoryRepository.deleteById(request.getCategoryId());
    }

    public List<GetCategoryListResponseDto> getCategoryList() {
        List<Category> all = categoryRepository.findAll();
        return all.stream()
                .map(category -> GetCategoryListResponseDto.of(category))
                .collect(Collectors.toList());
    }

    private void validateUniqueCategoryName(String categoryName) {
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new AccountBookException(
                    "카테고리 이름이 중복됩니다.",
                    CONFLIC_CATEGORY_NAME_EXCEPTION
            );
        }
    }

    private static void requestMemberMismatchCategoryOwner(
            Member member, Category category
    ) {
        if (!member.getId().equals(category.getMember().getId())) {
            throw new AccountBookException(
                    "해당 카테고리에는 접근할 수 없습니다. ",
                    FORBIDDEN_EXCEPTION
            );
        }
    }

    private Category validateCategory(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 카테고리 입니다.",
                        NOT_FOUND_CATEGORY_EXCEPTION
                )
        );
    }

    private Member validateMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION
                )
        );
    }
}