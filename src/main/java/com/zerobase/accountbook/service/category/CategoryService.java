package com.zerobase.accountbook.service.category;

import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.category.dto.request.CreateCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.request.DeleteCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.request.ModifyCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.response.CreateCategoryResponseDto;
import com.zerobase.accountbook.controller.category.dto.response.ModifyCategoryResponseDto;
import com.zerobase.accountbook.domain.category.Category;
import com.zerobase.accountbook.domain.category.CategoryRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final MemberRepository memberRepository;

    public CreateCategoryResponseDto createCategory(CreateCategoryRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        return CreateCategoryResponseDto.of(categoryRepository.save(Category.builder()
                .member(member)
                .categoryName(request.getCategoryName())
                .createdAt(LocalDateTime.now())
                .build()));
    }

    public ModifyCategoryResponseDto modifyCategory(ModifyCategoryRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        Category category = validateCategory(request.getCategoryId());

        forbiddenMember(member, category);

        category.setCategoryName(request.getCategoryName());
        category.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        // 기존에 있던 카테고리 수정시 해당 카테고리 가지고 있는 매일지출내역의 정보도 모두 수정하는 로직 추가 예정
        // 매일 지출내역 main에 머지 후 pull해서 dailypaymentrepository DI 할 예정

        return ModifyCategoryResponseDto.of(category);
    }

    public void deleteCategory(DeleteCategoryRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        Category category = validateCategory(request.getCategoryId());

        forbiddenMember(member, category);

        // 기존에 있던 카테고리 수정시 해당 카테고리 가지고 있는 매일지출내역의 정보도 모두 삭제하는 로직 추가 예정
        // 매일 지출내역 main에 머지 후 pull해서 dailypaymentrepository DI 할 예정

        categoryRepository.deleteById(request.getCategoryId());
    }

    private static void forbiddenMember(Member member, Category category) {
        if (!member.getId().equals(category.getMember().getId())) {
            throw new AccountBookException("해당 카테고리에는 접근할 수 없습니다. ", FORBIDDEN_EXCEPTION);
        }
    }

    private Category validateCategory(long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (!optionalCategory.isPresent()) {
            throw new AccountBookException("존재하지 않는 카테고리 입니다.", NOT_FOUND_CATEGORY_EXCEPTION);
        }
        return optionalCategory.get();
    }

    private Member validateMember(String memberEmail) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException("존재하지 않는 회원입니다.", NOT_FOUND_USER_EXCEPTION);
        }
        Member member = optionalMember.get();
        return member;
    }
}
