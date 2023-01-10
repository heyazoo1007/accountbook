package com.zerobase.accountbook.service.category;

import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.category.dto.request.CreateCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.response.CreateCategoryResponseDto;
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

    private Member validateMember(String memberEmail) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException("존재하지 않는 회원입니다.", NOT_FOUND_USER_EXCEPTION);
        }
        Member member = optionalMember.get();
        return member;
    }
}
