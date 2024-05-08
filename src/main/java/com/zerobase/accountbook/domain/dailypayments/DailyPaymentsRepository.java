package com.zerobase.accountbook.domain.dailypayments;

import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import com.zerobase.accountbook.service.monthlytotalamount.dto.TotalPaymentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DailyPaymentsRepository extends JpaRepository<DailyPayments, Long> {

    List<DailyPayments> findAllByMemberIdAndDateContaining(Long memberId, String date);

    @Query(value = "select sum(dp.paid_amount) " +
                   "from daily_payments dp " +
                   "where dp.member_id = :memberId" +
                   "  and dp.date >= :startDate" +
                   "  and dp.date <= :endDate",
            nativeQuery = true)
    Integer findMonthlySum(@Param("memberId") Long memberId,
                           @Param("startDate") String startDate,
                           @Param("endDate") String endDate);

    @Query(
            nativeQuery = true,
            value = "select * " +
                    "from daily_payments dp " +
                    "where dp.member_id = :memberId " +
                    "and (dp.paid_where like %:keyword% " +
                    "or dp.memo like %:keyword%)"
    )
    List<DailyPayments> searchKeyword(long memberId, String keyword);

    @Query(
            nativeQuery = true,
            value = "select c.id as categoryId, c.category_name as categoryName, dp.totalAmount " +
                    "from (select sum(paid_amount) as totalAmount, category_id " +
                    "      from daily_payments " +
                    "      where date >= :startDate " +
                    "        and date <= :endDate " +
                    "        and member_id = :memberId " +
                    "      group by category_id" +
                    ") dp " +
                    "join category c " +
                    "  on c.id = dp.category_id"
    )
    List<DailyPaymentsCategoryDto> findMonthlyCategory(@Param("startDate") String startDate,
                                                       @Param("endDate") String endDate,
                                                       @Param("memberId") Long memberId);

    @Query(
            nativeQuery = true,
            value = "select c.id as categoryId, c.category_name as categoryName, tapc.totalAmount " +
                    "from (select sum(total_amount) as totalAmount, category_id " +
                    "      from total_amount_per_category " +
                    "      where date like :year% " +
                    "        and member_id =:memberId " +
                    "      group by category_id" +
                    ") tapc " +
                    "join category c " +
                    "  on c.id = tapc.category_id"
    )
    List<DailyPaymentsCategoryDto> findYearlyCategory(@Param("year") String year, @Param("memberId") Long memberId);


    @Query(
            nativeQuery = true,
            value = "select member_id as memberId, sum(paid_amount) as totalAmount " +
                    "from daily_payments " +
                    "where date >=:startDate " +
                    "  and date <=:endDate " +
                    "group by member_id "
    )
    List<TotalPaymentDto> findAllTotalAmountByYearMonth(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
