package com.example.demo.repositories;

import com.example.demo.entities.Payment;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByPaymentStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(PaymentMethod method);

    // מציאת תשלומים במצב ממתין לזמן ממושך
    List<Payment> findByPaymentStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime dateTime);

    // סטטיסטיקות תשלומים לפי שיטת תשלום
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p " +
            "WHERE p.paymentStatus = :status GROUP BY p.paymentMethod")
    List<Object[]> countAndSumPaymentsByMethod(@Param("status") PaymentStatus status);

    // תשלומים לפי תאריכים
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // תשלומים שנכשלו
    List<Payment> findByPaymentStatusOrderByUpdatedAtDesc(PaymentStatus status);

    // מציאת סך התשלומים המוצלחים בתקופה מסוימת
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' " +
            "AND p.paymentDate BETWEEN :startDate AND :endDate")
    Double sumCompletedPaymentsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}